package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
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

        try {
            CdkRecord record = plugin.getCdkRecordDao().getCdkByCode(code);
            if (record == null) {
                sender.sendMessage(getMsg("command.use.invalid_code"));
                return true;
            }

            if (!record.canBeUsed()) {
                sender.sendMessage(getMsg("command.use.expired_or_used"));
                return true;
            }

            if (!record.isPerPlayerMultiple()) {
                if (plugin.getCdkLogDao().hasPlayerUsedCode(player.getUniqueId().toString(), code)) {
                    sender.sendMessage(getMsg("command.use.already_used"));
                    return true;
                }
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
                plugin.getCdkLogDao().logCdkUsage(player.getName(), player.getUniqueId().toString(), code, record.getCdkType(), record.getCommands());

                record.setRemainingUses(record.getRemainingUses() - 1);
                plugin.getCdkRecordDao().updateCdk(record);

                sender.sendMessage(getMsg("command.use.success"));

                boolean broadcastEnabled = plugin.getConfig().getBoolean("settings.broadcast", false);
                if (broadcastEnabled) {
                    String broadcastMessage = plugin.getConfig().getString("settings.broadcast-message", "§e玩家 {player} 使用了一个 {type} CDK!")
                            .replace("{player}", player.getName())
                            .replace("{type}", record.getCdkType().isEmpty() ? "普通" : record.getCdkType());
                    Bukkit.broadcastMessage(broadcastMessage);
                }
            } else {
                sender.sendMessage(getMsg("command.use.use_error"));
            }

        } catch (Exception e) {
            sender.sendMessage(getMsg("command.use.error", e.getMessage()));
            plugin.getLogger().severe("使用CDK时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
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