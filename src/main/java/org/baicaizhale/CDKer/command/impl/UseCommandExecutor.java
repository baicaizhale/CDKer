package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.model.PendingSequence;
import org.baicaizhale.CDKer.model.RedeemResult;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UseCommandExecutor extends AbstractSubCommand {

    public UseCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!CommandUtils.hasPermission(sender, "cdk.use")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        if (!requirePlayer(sender)) {
            sender.sendMessage(getMsg("command.common.player_only"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMsg("command.use.usage"));
            return true;
        }

        Player player = (Player) sender;
        String code = args[0];

        // 数据库查询放到异步线程，避免阻塞主线程；扣减与发奖在主线程串行执行
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                CdkRecord record = plugin.getCdkRecordDao().getCdkByCode(code);
                plugin.getServer().getScheduler().runTask(plugin, () -> handleRedeem(player, record));
            } catch (Exception e) {
                plugin.getLogger().severe("查询CDK时出错: " + e.getMessage());
                e.printStackTrace();
                plugin.getServer().getScheduler().runTask(plugin,
                        () -> CommandUtils.sendMessage(player, getMsg("command.common.internal_error")));
            }
        });

        return true;
    }

    private void handleRedeem(Player player, CdkRecord record) {
        if (!player.isOnline()) {
            return;
        }
        if (record == null) {
            CommandUtils.sendMessage(player, getMsg("command.use.invalid_code"));
            return;
        }

        try {
            // 单个事务内完成：校验单人限用 + 扣次数 + 写日志，全部成功才提交
            RedeemResult result = plugin.getCdkRecordDao()
                    .redeem(record, player.getUniqueId().toString(), player.getName());
            switch (result) {
                case ALREADY_USED:
                    CommandUtils.sendMessage(player, getMsg("command.use.already_used"));
                    return;
                case USED_UP:
                    CommandUtils.sendMessage(player, getMsg("command.use.expired_or_used"));
                    return;
                case INVALID:
                    CommandUtils.sendMessage(player, getMsg("command.use.invalid_code"));
                    return;
                default:
                    break;
            }

            // 顺序执行绑定命令，支持 sleep 等待控制：
            // 等待按服务器墙钟折算成绝对到期时间，期间玩家离线或服务器重启都不丢失，
            // 剩余命令挂库，由玩家上线时的 PlayerJoinListener 恢复执行
            runSequence(player.getUniqueId(), record.getCdkCode(), record.getCdkType(),
                    record.getCommands(), 0, false);

        } catch (Exception e) {
            plugin.getLogger().severe("使用CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(player, getMsg("command.common.internal_error"));
        }
    }

    /**
     * 从 startIndex 起在主线程顺序执行命令列表：普通命令循环依次派发，遇到 sleep 则
     * 换算成绝对到期时间落库并挂定时器后返回。每步都用 UUID 重新解析玩家，
     * 不持有旧的 Player 引用（重登后旧引用会永远判定为离线）。
     *
     * @param hadFailure 序列中此前是否已有命令执行失败，挂起恢复后延续该状态
     */
    private void runSequence(UUID playerUuid, String cdkCode, String cdkType,
                             List<String> commands, int startIndex, boolean hadFailure) {
        int index = startIndex;
        while (index < commands.size()) {
            Player player = Bukkit.getPlayer(playerUuid);
            if (player == null) {
                // 执行中发现玩家离线（如命令把玩家踢下线）：挂起剩余命令，等玩家上线继续
                suspendSequence(playerUuid, cdkCode, cdkType, commands, index, hadFailure);
                return;
            }

            String finalCommand = CommandUtils.sanitizeCommand(
                    CommandUtils.replaceCommandVariables(commands.get(index), player));
            if (finalCommand == null || finalCommand.isEmpty()) {
                index++;
                continue;
            }

            // sleep 等待控制：到点后继续后续命令
            if (CommandUtils.isSleepCommand(finalCommand)) {
                long seconds = CommandUtils.parseSleepSeconds(finalCommand);
                if (seconds <= 0) {
                    // 无法解析的 sleep 只跳过等待，不当作命令失败，
                    // 避免拼写错误导致整个兑换被回滚（语法应在 create/set 时校验）
                    plugin.getLogger().warning("忽略无法解析的 sleep 时长，继续执行后续命令: " + finalCommand);
                    index++;
                    continue;
                }
                scheduleSleep(playerUuid, cdkCode, cdkType, commands, index + 1, hadFailure, seconds);
                return;
            }

            plugin.getLogger().info("执行CDK命令: " + finalCommand);
            if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand)) {
                hadFailure = true;
                plugin.getLogger().warning("执行命令失败: " + finalCommand);
            }
            index++;
        }
        finishRedeem(playerUuid, cdkCode, cdkType, hadFailure);
    }

    /**
     * 处理 sleep 等待：先把剩余序列（连同绝对到期时间）写入挂起表，再挂内存定时器。
     * 服务器正常运行时走定时器快路径；中途崩溃/重启则内存定时器消失，
     * 由留在库中的挂起记录在玩家上线后兜底恢复。
     */
    private void scheduleSleep(UUID playerUuid, String cdkCode, String cdkType,
                               List<String> commands, int nextIndex, boolean hadFailure, long seconds) {
        long resumeAt = System.currentTimeMillis() + seconds * 1000L;
        int pendingId = -1;
        try {
            pendingId = plugin.getPendingSequenceDao().insert(playerUuid, cdkCode, cdkType,
                    commands.subList(nextIndex, commands.size()), hadFailure, resumeAt);
        } catch (Exception e) {
            // 落库失败仍按内存定时器执行，仅等待期间重启才会丢失
            plugin.getLogger().severe("写入CDK挂起序列失败，改为仅内存等待: " + cdkCode + " - " + e.getMessage());
        }
        plugin.getLogger().info("CDK命令等待 " + seconds + " 秒后继续: cdk=" + cdkCode);
        long ticks = Math.max(1, (resumeAt - System.currentTimeMillis()) / 50L + 1);
        int finalPendingId = pendingId;
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> onSleepTimer(finalPendingId, playerUuid, cdkCode, cdkType, commands, nextIndex, hadFailure),
                ticks);
    }

    /**
     * sleep 到点后的内存路径：只有玩家在线且成功删掉挂起记录（记录仍归自己管）才继续执行，
     * 与 PlayerJoinListener 的恢复路径通过"删除成功才执行"互斥，避免剩余命令被执行两次。
     */
    private void onSleepTimer(int pendingId, UUID playerUuid, String cdkCode, String cdkType,
                              List<String> commands, int nextIndex, boolean hadFailure) {
        if (Bukkit.getPlayer(playerUuid) == null) {
            // 玩家不在线：挂起记录保留在库中，等玩家上线后由 PlayerJoinListener 恢复
            return;
        }
        if (pendingId > 0) {
            try {
                if (!plugin.getPendingSequenceDao().deleteById(pendingId)) {
                    return;
                }
            } catch (Exception e) {
                // 删除失败时宁可等玩家下次上线再恢复，也不冒双跑风险
                plugin.getLogger().severe("删除CDK挂起序列失败，等待玩家下次上线恢复: " + cdkCode + " - " + e.getMessage());
                return;
            }
        }
        runSequence(playerUuid, cdkCode, cdkType, commands, nextIndex, hadFailure);
    }

    /**
     * 把剩余命令挂起到数据库，到期时间取当前时刻（玩家下次上线即继续）。
     */
    private void suspendSequence(UUID playerUuid, String cdkCode, String cdkType,
                                 List<String> commands, int fromIndex, boolean hadFailure) {
        try {
            plugin.getPendingSequenceDao().insert(playerUuid, cdkCode, cdkType,
                    commands.subList(fromIndex, commands.size()), hadFailure, System.currentTimeMillis());
            plugin.getLogger().info("玩家离线，CDK剩余命令已挂起，等待玩家上线后继续: " + cdkCode);
        } catch (Exception e) {
            plugin.getLogger().severe("挂起CDK剩余命令失败，剩余命令被放弃: " + cdkCode + " - " + e.getMessage());
        }
    }

    /**
     * 恢复一条挂起序列（挂起记录已由调用方删除），供 PlayerJoinListener 在玩家上线时调用。
     */
    public void resumePending(PendingSequence pending) {
        runSequence(pending.getPlayerUuid(), pending.getCdkCode(), pending.getCdkType(),
                pending.getRemainingCommands(), 0, pending.isHadFailure());
    }

    /**
     * 所有绑定命令执行完毕后的统一收尾：成功则提示并发广播，失败则回滚本次兑换。
     */
    private void finishRedeem(UUID playerUuid, String cdkCode, String cdkType, boolean success) {
        Player player = Bukkit.getPlayer(playerUuid);
        if (success) {
            if (player != null) {
                CommandUtils.sendMessage(player, getMsg("command.use.success"));
            }

            boolean broadcastEnabled = plugin.getConfig().getBoolean("settings.broadcast", false);
            if (broadcastEnabled && player != null) {
                String broadcastMessage = plugin.getConfig().getString("settings.broadcast-message",
                                "§e玩家 {player} 使用了一个 {type} CDK!")
                        .replace("{player}", player.getName())
                        .replace("{type}", cdkType == null || cdkType.isEmpty()
                                ? getRawMsg("command.common.type_normal") : cdkType);
                Bukkit.broadcastMessage(broadcastMessage);
            }
        } else {
            // 奖励命令执行失败：回滚本次兑换（恢复剩余次数并删除使用日志），
            // 避免出现"CDK已被消耗但奖励没有发放"的情况
            rollbackRedeem(cdkCode, playerUuid);
            if (player != null) {
                CommandUtils.sendMessage(player, getMsg("command.use.use_error"));
                CommandUtils.sendMessage(player, getMsg("command.use.rolled_back"));
            }
        }
    }

    /**
     * 回滚一次兑换：恢复剩余使用次数并删除本次写入的使用日志。
     */
    private void rollbackRedeem(String cdkCode, UUID playerUuid) {
        try {
            plugin.getCdkRecordDao().rollbackRedeem(cdkCode, playerUuid.toString());
            plugin.getLogger().warning("CDK奖励命令执行失败，已回滚本次兑换: " + cdkCode);
        } catch (Exception e) {
            plugin.getLogger().severe("回滚CDK兑换失败: " + cdkCode + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return getMsg("command.use.usage");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // 可以在这里提供已存在的CDK码作为补全建议
        return new ArrayList<>();
    }
}
