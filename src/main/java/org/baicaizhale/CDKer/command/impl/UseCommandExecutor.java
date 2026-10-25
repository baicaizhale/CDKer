package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.model.RedeemResult;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

            // 顺序执行绑定命令，支持 sleep 等待控制（跨服务器 tick，非阻塞主线程）
            executeCommands(player, record);

        } catch (Exception e) {
            plugin.getLogger().severe("使用CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(player, getMsg("command.common.internal_error"));
        }
    }

    /**
     * 顺序执行 CDK 绑定的命令列表，遇到 sleep 等待指令时通过调度器延迟执行后续命令，
     * 不会阻塞服务器主线程。所有命令执行完毕后统一判定成功/失败并处理广播或回滚。
     */
    private void executeCommands(Player player, CdkRecord record) {
        List<String> commands = new ArrayList<>(record.getCommands());
        AtomicBoolean allSuccess = new AtomicBoolean(true);
        runNextCommand(player, commands, record, allSuccess, 0);
    }

    private void runNextCommand(Player player, List<String> commands, CdkRecord record,
                                AtomicBoolean allSuccess, int index) {
        if (index >= commands.size()) {
            finishRedeem(player, record, allSuccess.get());
            return;
        }

        // 玩家离线后停止后续命令（已执行的奖励不予回滚）
        if (!player.isOnline()) {
            return;
        }

        String raw = commands.get(index);
        String finalCommand = CommandUtils.sanitizeCommand(
                CommandUtils.replaceCommandVariables(raw, player));
        if (finalCommand == null || finalCommand.isEmpty()) {
            runNextCommand(player, commands, record, allSuccess, index + 1);
            return;
        }

        // sleep 等待控制：延迟后继续后续命令
        if (CommandUtils.isSleepCommand(finalCommand)) {
            long seconds = CommandUtils.parseSleepSeconds(finalCommand);
            if (seconds > 0) {
                plugin.getLogger().info("CDK命令等待 " + seconds + " 秒后继续: " + finalCommand);
                plugin.getServer().getScheduler().runTaskLater(plugin,
                        () -> runNextCommand(player, commands, record, allSuccess, index + 1),
                        seconds * 20L);
                return;
            }
        }

        plugin.getLogger().info("执行CDK命令: " + finalCommand);
        if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand)) {
            allSuccess.set(false);
            plugin.getLogger().warning("执行命令失败: " + finalCommand);
        }
        runNextCommand(player, commands, record, allSuccess, index + 1);
    }

    /**
     * 所有绑定命令执行完毕后的统一收尾：成功则提示并发广播，失败则回滚本次兑换。
     */
    private void finishRedeem(Player player, CdkRecord record, boolean success) {
        if (success) {
            CommandUtils.sendMessage(player, getMsg("command.use.success"));

            boolean broadcastEnabled = plugin.getConfig().getBoolean("settings.broadcast", false);
            if (broadcastEnabled) {
                String broadcastMessage = plugin.getConfig().getString("settings.broadcast-message",
                                "§e玩家 {player} 使用了一个 {type} CDK!")
                        .replace("{player}", player.getName())
                        .replace("{type}", record.getCdkType() == null || record.getCdkType().isEmpty()
                                ? getRawMsg("command.common.type_normal") : record.getCdkType());
                Bukkit.broadcastMessage(broadcastMessage);
            }
        } else {
            // 奖励命令执行失败：回滚本次兑换（恢复剩余次数并删除使用日志），
            // 避免出现"CDK已被消耗但奖励没有发放"的情况
            rollbackRedeem(record, player);
            CommandUtils.sendMessage(player, getMsg("command.use.use_error"));
            CommandUtils.sendMessage(player, getMsg("command.use.rolled_back"));
        }
    }

    /**
     * 回滚一次兑换：恢复剩余使用次数并删除本次写入的使用日志。
     */
    private void rollbackRedeem(CdkRecord record, Player player) {
        try {
            plugin.getCdkRecordDao().rollbackRedeem(record.getCdkCode(), player.getUniqueId().toString());
            plugin.getLogger().warning("CDK奖励命令执行失败，已回滚本次兑换: " + record.getCdkCode());
        } catch (Exception e) {
            plugin.getLogger().severe("回滚CDK兑换失败: " + record.getCdkCode() + " - " + e.getMessage());
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
