package org.baicaizhale.CDKer.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成随机CDK码
     * @param charset 字符集
     * @param length 长度
     * @return CDK码
     */
    public static String generateCdkCode(String charset, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return sb.toString();
    }

    /**
     * 检查玩家是否有权限
     * @param sender 命令发送者
     * @param permission 权限节点
     * @return 是否有权限
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || sender.hasPermission("cdk.admin");
    }

    /**
     * 发送带颜色代码的消息
     * @param sender 命令发送者
     * @param message 消息内容
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * 替换命令中的变量
     * @param command 命令文本
     * @param player 玩家
     * @return 替换后的命令
     */
    public static String replaceCommandVariables(String command, Player player) {
        return command.replace("{player}", player.getName())
                     .replace("%player%", player.getName())
                     .replace("{uuid}", player.getUniqueId().toString())
                     .replace("{world}", player.getWorld().getName())
                     .replace("{x}", String.valueOf(player.getLocation().getBlockX()))
                     .replace("{y}", String.valueOf(player.getLocation().getBlockY()))
                     .replace("{z}", String.valueOf(player.getLocation().getBlockZ()));
    }

    /**
     * 将命令字符串转换为列表
     * @param commandStr 命令字符串
     * @return 命令列表
     */
    public static List<String> parseCommands(String commandStr) {
        return Arrays.stream(commandStr.split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
    }
}