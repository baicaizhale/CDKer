package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkLog;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
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
    public boolean execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }

        if (args.length < 1) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        Player player = asPlayer(sender);
        String code = args[0].toUpperCase();

        try {
            CdkRecord record = plugin.getCdkRecordDao().getCdkByCode(code);
            if (record == null) {
                CommandUtils.sendMessage(sender, "§c无效的CDK码。");
                return true;
            }

            // 检查CDK是否可用
            if (!record.canBeUsed()) {
                if (record.isExpired()) {
                    CommandUtils.sendMessage(sender, "§c此CDK码已过期。");
                } else {
                    CommandUtils.sendMessage(sender, "§c此CDK码已被使用完。");
                }
                return true;
            }

            // 检查玩家是否已使用过此类型的CDK
            if (record.getCdkType() != null && !record.getCdkType().isEmpty()) {
                List<CdkLog> logs = plugin.getCdkLogDao().getLogsByPlayer(player.getUniqueId().toString());
                for (CdkLog log : logs) {
                    if (record.getCdkType().equals(log.getCdkType())) {
                        CommandUtils.sendMessage(sender, "§c您已经使用过此类型的CDK码。");
                        return true;
                    }
                }
            }

            // 执行命令
            boolean success = true;
            for (String command : record.getCommands()) {
                String processedCommand = CommandUtils.replaceCommandVariables(command, player);
                if (!Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)) {
                    success = false;
                    plugin.getLogger().warning("执行命令失败: " + processedCommand);
                }
            }

            if (success) {
                // 更新CDK使用次数
                record.setRemainingUses(record.getRemainingUses() - 1);
                plugin.getCdkRecordDao().updateCdk(record);

                // 记录使用日志
                CdkLog log = new CdkLog(
                    player.getName(),
                    player.getUniqueId().toString(),
                    code,
                    record.getCdkType(),
                    String.join("|", record.getCommands())
                );
                plugin.getCdkLogDao().insertLog(log);

                // 发送成功消息
                CommandUtils.sendMessage(sender, "§a成功使用CDK码！");

                // 广播消息
                if (plugin.getConfig().getBoolean("settings.broadcast", true)) {
                    String message = plugin.getConfig().getString("settings.broadcast-message", "§e玩家 {player} 使用了一个 {type} CDK!")
                        .replace("{player}", player.getName())
                        .replace("{type}", record.getCdkType().isEmpty() ? "普通" : record.getCdkType());
                    Bukkit.broadcastMessage(message);
                }
            } else {
                CommandUtils.sendMessage(sender, "§c执行CDK命令时出错，请联系管理员。");
            }

            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("使用CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c使用CDK时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk use <cdkcode> §7- 使用CDK";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.use";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}