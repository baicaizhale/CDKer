package org.baicaizhale.CDKer.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern EXPIRE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$");

    public static String generateCdkCode(String charset, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return sb.toString();
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || sender.hasPermission("cdk.admin");
    }

    /**
     * 校验过期时间：仅接受 forever 或严格零填充的 yyyy-MM-dd HH:mm。
     * 数据库端按字符串比较（expire_time > 当前时间），非零填充或乱格式会得出错误排序，
     * 而垃圾格式在 CdkRecord.isExpired() 里解析失败一律视为未过期，导致"列表有效、兑换过期"。
     */
    public static boolean isValidExpireTime(String value) {
        if (value == null) {
            return false;
        }
        if ("forever".equals(value)) {
            return true;
        }
        if (!EXPIRE_PATTERN.matcher(value).matches()) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);
        try {
            format.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String replaceCommandVariables(String command, Player player) {
        return command.replace("{player}", player.getName())
                     .replace("%player%", player.getName())
                     .replace("{uuid}", player.getUniqueId().toString())
                     .replace("{world}", player.getWorld().getName())
                     .replace("{x}", String.valueOf(player.getLocation().getBlockX()))
                     .replace("{y}", String.valueOf(player.getLocation().getBlockY()))
                     .replace("{z}", String.valueOf(player.getLocation().getBlockZ()));
    }

    public static String sanitizeCommand(String command) {
        if (command == null || command.isEmpty()) return command;
        String trimmed = command.trim();
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1).trim();
        }
        return trimmed;
    }

    public static List<String> parseCommands(String commandStr) {
        return Arrays.stream(commandStr.split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
    }
}
