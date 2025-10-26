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
            sender.sendMessage("§c该命令只能由玩家执行。");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§c用法: /cdk use <兑换码>");
            return true;
        }

        Player player = (Player) sender;
        String code = args[0]; // 不再强制转大写，确保与数据库存储一致

        try {
            CdkRecord record = plugin.getCdkRecordDao().getCdkByCode(code);
            if (record == null) {
                sender.sendMessage("§c无效的兑换码。");
                return true;
            }

            if (!record.canBeUsed()) {
                sender.sendMessage("§c该兑换码已过期或无剩余次数。");
                return true;
            }

            // 检查是否允许同一玩家多次使用
            if (!record.isPerPlayerMultiple()) {
                if (plugin.getCdkLogDao().hasPlayerUsedCode(player.getUniqueId().toString(), code)) {
                    sender.sendMessage("§c您已经使用过该兑换码。");
                    return true;
                }
            }

            // 执行命令
            boolean success = true;
            for (String command : record.getCommands()) {
                String finalCommand = CommandUtils.replaceCommandVariables(command, player);
                if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand)) {
                    success = false;
                    plugin.getLogger().warning("执行命令失败: " + finalCommand);
                }
            }

            if (success) {
                // 记录使用日志
                plugin.getCdkLogDao().logCdkUsage(player.getName(), player.getUniqueId().toString(), code, record.getCdkType(), record.getCommands());

                // 更新剩余次数
                record.setRemainingUses(record.getRemainingUses() - 1);
                plugin.getCdkRecordDao().updateCdk(record);

                sender.sendMessage("§a兑换成功！");
                
                // 根据配置决定是否广播
                boolean broadcastEnabled = plugin.getConfig().getBoolean("settings.broadcast", false);
                if (broadcastEnabled) {
                    String broadcastMessage = plugin.getConfig().getString("settings.broadcast-message", "§e玩家 {player} 使用了一个 {type} CDK!")
                            .replace("{player}", player.getName())
                            .replace("{type}", record.getCdkType().isEmpty() ? "普通" : record.getCdkType());
                    Bukkit.broadcastMessage(broadcastMessage);
                }
            } else {
                sender.sendMessage("§c执行兑换码命令时出错，请联系管理员。");
            }

        } catch (Exception e) {
            sender.sendMessage("§c使用兑换码时出错: " + e.getMessage());
            plugin.getLogger().severe("使用CDK时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk use <兑换码>";
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // 可以在这里提供已存在的CDK码作为补全建议
        return new ArrayList<>();
    }
}