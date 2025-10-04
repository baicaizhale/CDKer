package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.model.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * 命令行工具类，提供通用的命令相关功能。
 */
public class CommandUtils {

    /**
     * 检查命令发送者是否有执行命令的权限。
     * @param sender 命令发送者
     * @param permission 所需权限
     * @param prefix 插件前缀
     * @param langConfig 语言配置
     * @return 如果玩家有权限则返回 true，否则返回 false
     */
    public static boolean checkPermission(CommandSender sender, String permission, String prefix, LanguageConfig langConfig) {
        if (!sender.hasPermission(permission)) {
            String noPermissionMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("no_permission"));
            sender.sendMessage(noPermissionMessage);
            // plugin.getLogger().info("[To Player] " + sender.getName() + ": " + noPermissionMessage); // 移除此行，因为 CommandUtils 不应该直接访问 plugin 实例
            return false;
        }
        return true;
    }
}