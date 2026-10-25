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
     * sleep 允许的最大等待时长（秒）：30 天。
     * 超过视为非法，防止误写出天荒地老的等待把挂起记录永久滞留。
     */
    private static final long MAX_SLEEP_SECONDS = 30L * 24 * 60 * 60;

    /**
     * 判断是否为 sleep 等待控制指令。
     * sleep 用于在多条绑定命令之间插入延迟，例如"临时给予"场景：
     * 先发放道具，等待一段时间后再收回。
     * 语法: sleep <时长>，其中时长可为纯数字（单位秒）或带单位 30s / 5m / 1h。
     */
    public static boolean isSleepCommand(String command) {
        if (command == null) return false;
        String trimmed = command.trim();
        // 裸 "sleep"（无参数）也按 sleep 指令识别，时长解析不出来会在校验/运行时各自处理
        return trimmed.equalsIgnoreCase("sleep") || trimmed.toLowerCase().startsWith("sleep ");
    }

    /**
     * 解析 sleep 指令的等待时长（单位秒）。
     * 非法格式（缺失、解析失败、非正数、超过 30 天上限）一律返回 0。
     */
    public static long parseSleepSeconds(String command) {
        if (command == null) return 0;
        String rest = command.trim().toLowerCase();
        if (!rest.startsWith("sleep")) return 0;
        rest = rest.substring("sleep".length()).trim();
        if (rest.isEmpty()) return 0;

        long multiplier = 1;
        String numPart = rest;
        char unit = rest.charAt(rest.length() - 1);
        if (unit == 's' || unit == 'm' || unit == 'h') {
            multiplier = unit == 's' ? 1 : (unit == 'm' ? 60 : 3600);
            numPart = rest.substring(0, rest.length() - 1);
        }
        try {
            long seconds = Math.multiplyExact(Long.parseLong(numPart.trim()), multiplier);
            return (seconds > 0 && seconds <= MAX_SLEEP_SECONDS) ? seconds : 0;
        } catch (NumberFormatException | ArithmeticException e) {
            return 0;
        }
    }

    /**
     * 校验命令序列中的 sleep 语法：凡被识别为 sleep 指令的条目都必须解析出合法时长，
     * 用于 create/set 时把拼写错误（如 sleep 1hour）挡在写入数据库之前。
     *
     * @return 第一条不合法的 sleep 命令原文；全部合法返回 null
     */
    public static String findInvalidSleepCommand(List<String> commands) {
        for (String command : commands) {
            if (isSleepCommand(command) && parseSleepSeconds(command) <= 0) {
                return command;
            }
        }
        return null;
    }
}
