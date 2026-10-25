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

    /**
     * 判断是否为 sleep 等待控制指令。
     * sleep 用于在多条绑定命令之间插入延迟，例如"临时给予"场景：
     * 先发放道具，等待一段时间后再收回。
     * 语法: sleep <时长>，其中时长可为纯数字（单位秒）或带单位 30s / 5m / 1h。
     */
    public static boolean isSleepCommand(String command) {
        if (command == null) return false;
        return command.trim().toLowerCase().startsWith("sleep ");
    }

    /**
     * 解析 sleep 指令的等待时长（单位秒）。非法格式返回 0（表示不等待）。
     */
    public static long parseSleepSeconds(String command) {
        if (command == null) return 0;
        String rest = command.trim().substring("sleep ".length()).trim().toLowerCase();
        if (rest.isEmpty()) return 0;

        long multiplier = 1;
        String numPart = rest;
        char unit = rest.charAt(rest.length() - 1);
        if (unit == 's' || unit == 'm' || unit == 'h') {
            multiplier = unit == 's' ? 1 : (unit == 'm' ? 60 : 3600);
            numPart = rest.substring(0, rest.length() - 1);
        }
        try {
            long value = Long.parseLong(numPart.trim());
            return value * multiplier;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
