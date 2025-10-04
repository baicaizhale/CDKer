package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.model.CDK;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * 处理 /cdk add 命令的执行器。
 */
public class AddCommandExecutor {

    private final CDKer plugin;
    private final ConfigurationManager configManager;

    public AddCommandExecutor(CDKer plugin, ConfigurationManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * 处理添加 CDK 命令的逻辑。
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 插件前缀
     * @param langConfig 语言配置
     * @return 命令是否成功执行
     */
    public boolean handleAddCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.add", prefix, langConfig)) return true;

        if (args.length < 3) {
            String addUsageMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("add_usage"));
            sender.sendMessage(addUsageMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + addUsageMessage);
            return true;
        }

        String cdkCode = args[1];
        int quantityToAdd;
        try {
            quantityToAdd = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            String invalidQuantityMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity"));
            sender.sendMessage(invalidQuantityMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidQuantityMessage);
            return true;
        }

        CDK cdk = configManager.getCdkMap().get(cdkCode);
        if (cdk == null) {
            String cdkNotFoundMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode));
            sender.sendMessage(cdkNotFoundMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + cdkNotFoundMessage);
            return true;
        }

        cdk.setRemainingUses(cdk.getRemainingUses() + quantityToAdd);
        configManager.saveCdkConfig();

        String addSuccessMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("add_success")
                .replace("%id%", cdkCode)
                .replace("%quantity%", String.valueOf(quantityToAdd)));
        sender.sendMessage(addSuccessMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + addSuccessMessage);
        return true;
    }

    /**
     * 检查玩家是否有执行命令的权限。
     * @param sender 命令发送者
     * @param permission 所需权限
     * @param prefix 插件前缀
     * @param langConfig 语言配置
     * @return 如果玩家有权限则返回 true，否则返回 false
     */
    private boolean checkPermission(CommandSender sender, String permission, String prefix, LanguageConfig langConfig) {
        if (!sender.hasPermission(permission)) {
            String noPermissionMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("no_permission"));
            sender.sendMessage(noPermissionMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + noPermissionMessage);
            return false;
        }
        return true;
    }
}