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

public class UseCommandExecutor extends AbstractSubCommand {

    public UseCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
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

            boolean success = true;
            for (String command : record.getCommands()) {
                String finalCommand = CommandUtils.sanitizeCommand(CommandUtils.replaceCommandVariables(command, player));
                if (finalCommand == null || finalCommand.isEmpty()) continue;
                plugin.getLogger().info("执行CDK命令: " + finalCommand);
                if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand)) {
                    success = false;
                    plugin.getLogger().warning("执行命令失败: " + finalCommand);
                }
            }

            if (success) {
                CommandUtils.sendMessage(player, getMsg("command.use.success"));

                boolean broadcastEnabled = plugin.getConfig().getBoolean("settings.broadcast", false);
                if (broadcastEnabled) {
                    String broadcastMessage = plugin.getConfig().getString("settings.broadcast-message", "§e玩家 {player} 使用了一个 {type} CDK!")
                            .replace("{player}", player.getName())
                            .replace("{type}", record.getCdkType().isEmpty() ? "普通" : record.getCdkType());
                    Bukkit.broadcastMessage(broadcastMessage);
                }
            } else {
                CommandUtils.sendMessage(player, getMsg("command.use.use_error"));
            }

        } catch (Exception e) {
            plugin.getLogger().severe("使用CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(player, getMsg("command.common.internal_error"));
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
