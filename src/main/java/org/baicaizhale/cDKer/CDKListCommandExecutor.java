package org.baicaizhale.cDKer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

public class CDKListCommandExecutor implements CommandExecutor {
    private final CDKer plugin;

    public CDKListCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if ("list".equalsIgnoreCase(args[0])) {
            return showCDKList(sender);
        }
        if ("reload".equalsIgnoreCase(args[0])) {
            return reloadConfig(sender);
        }
        return false;
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || !(sender instanceof Player);
    }

    private boolean showCDKList(CommandSender sender) {
        FileConfiguration langConfig = plugin.getLangConfig();
        String prefix = plugin.getPrefix();
        if (!hasPermission(sender, "cdk.list")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }
        FileConfiguration cdkConfig = plugin.getCDKConfig();
        Set<String> cdkCodes = cdkConfig.getKeys(false);
        if (cdkCodes.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_empty", "当前没有任何兑换码！")));
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_header", "有效的兑换码列表:")));
        for (String cdkCode : cdkCodes) {
            int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
            String expiration = cdkConfig.getString(cdkCode + ".expiration", langConfig.getString("never_expires", "永不"));
            String status = remainingUses > 0 ? ChatColor.GREEN + langConfig.getString("cdk_available", "可用") : ChatColor.RED + langConfig.getString("cdk_used_up", "已用完");
            sender.sendMessage(ChatColor.YELLOW + "兑换码: " + cdkCode + " | 剩余次数: " + remainingUses + " | 过期时间: " + expiration + " | 状态: " + status);
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_footer", "")));
        return true;
    }

    private boolean reloadConfig(CommandSender sender) {
        FileConfiguration langConfig = plugin.getLangConfig();
        String prefix = plugin.getPrefix();
        if (!hasPermission(sender, "cdk.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }
        plugin.reloadConfig();
        File cdkFile = new File(plugin.getDataFolder(), "cdk.yml");
        if (cdkFile.exists()) {
            FileConfiguration cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);
            plugin.setCDKConfig(cdkConfig);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("reload_success", "配置文件已成功重载！")));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_not_found", "未找到 cdk.yml 文件！")));
        }
        return true;
    }
}
