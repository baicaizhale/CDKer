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
