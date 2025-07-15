package org.baicaizhale.cDKer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CDKCommandExecutor implements CommandExecutor {

    private final CDKer plugin;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration langConfig = plugin.getLangConfig();
        String prefix = plugin.getPrefix();

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender, prefix, langConfig);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreateCommand(sender, args, prefix, langConfig);
            case "add":
                return handleAddCommand(sender, args, prefix, langConfig);
            case "delete":
                return handleDeleteCommand(sender, args, prefix, langConfig);
            case "list":
                return handleListCommand(sender, prefix, langConfig);
            case "reload":
                return handleReloadCommand(sender, prefix, langConfig);
            case "export":
                return handleExportCommand(sender, prefix, langConfig);
            case "use":
                return handleUseCommand(sender, args, prefix, langConfig);
            default:
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("unknown_command")));
                sendHelpMessage(sender, prefix, langConfig);
                return true;
        }
    }

    private void sendHelpMessage(CommandSender sender, String prefix, FileConfiguration langConfig) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_header")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_create")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_create_multiple")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_add")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_delete")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_list")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_reload")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_export")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_use")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("help_footer")));
    }

    private boolean handleCreateCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission("cdk.create")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_single")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_multiple")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_example_single")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_example_multiple")));
            return true;
        }

        String cdkType = args[1].toLowerCase();
        String id;
        int quantity;
        String commandsString;
        String expirationTime = null;

        if (cdkType.equals("single")) {
            if (args.length < 5) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_single")));
                return true;
            }
            id = args[2];
            try {
                quantity = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_quantity")));
                return true;
            }
            commandsString = args[4];
            if (args.length > 5) {
                expirationTime = args[5] + " " + args[6];
            }
        } else if (cdkType.equals("multiple")) {
            if (args.length < 6) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_multiple")));
                return true;
            }
            String nameOrRandom = args[2].toLowerCase();
            if (nameOrRandom.equals("random")) {
                id = UUID.randomUUID().toString().substring(0, 8);
            } else {
                id = args[3];
            }
            try {
                quantity = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_quantity")));
                return true;
            }
            commandsString = args[5];
            if (args.length > 6) {
                expirationTime = args[6] + " " + args[7];
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_cdk_type")));
            return true;
        }

        List<String> commands = new ArrayList<>();
        if (commandsString.startsWith("\"") && commandsString.endsWith("\"")) {
            String rawCommands = commandsString.substring(1, commandsString.length() - 1);
            String[] splitCommands = rawCommands.split("\\|");
            for (String cmd : splitCommands) {
                commands.add(cmd.trim());
            }
        } else {
            commands.add(commandsString);
        }

        Date expirationDate = null;
        if (expirationTime != null) {
            try {
                expirationDate = DATE_FORMAT.parse(expirationTime);
            } catch (ParseException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_date_format")));
                return true;
            }
        }

        FileConfiguration cdkConfig = plugin.getCDKConfig();

        if (cdkConfig.contains(id)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_already_exists").replace("%cdk%", id)));
            return true;
        }

        cdkConfig.set(id + ".type", cdkType);
        cdkConfig.set(id + ".commands", commands);
        cdkConfig.set(id + ".remainingUses", quantity);
        if (expirationDate != null) {
            cdkConfig.set(id + ".expiration", DATE_FORMAT.format(expirationDate));
        }
        plugin.saveCDKConfig();

        if (cdkType.equals("single")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_success_single")
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", id)));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_success_multiple")
                    .replace("%cdk%", id)
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", id)));
        }
        return true;
    }

    private boolean handleAddCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission("cdk.add")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_usage")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_example")));
            return true;
        }

        String cdkCode = args[1];
        int quantity;
        try {
            quantity = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_quantity")));
            return true;
        }

        FileConfiguration cdkConfig = plugin.getCDKConfig();

        if (cdkConfig.contains(cdkCode)) {
            int currentUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
            cdkConfig.set(cdkCode + ".remainingUses", currentUses + quantity);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_success")
                    .replace("%id%", cdkCode)
                    .replace("%quantity%", String.valueOf(currentUses + quantity))));
        } else {
            cdkConfig.set(cdkCode + ".type", "single");
            cdkConfig.set(cdkCode + ".commands", new ArrayList<String>());
            cdkConfig.set(cdkCode + ".remainingUses", quantity);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_success")
                    .replace("%id%", cdkCode)
                    .replace("%quantity%", String.valueOf(quantity))));
        }

        plugin.saveCDKConfig();
        return true;
    }

    private boolean handleDeleteCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission("cdk.delete")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("delete_usage")));
            return true;
        }

        String deleteType = args[1].toLowerCase();
        String content = args[2];

        FileConfiguration cdkConfig = plugin.getCDKConfig();
        FileConfiguration usedCodesConfig = plugin.getUsedCodesConfig();

        if (deleteType.equals("cdk")) {
            if (cdkConfig.contains(content)) {
                cdkConfig.set(content, null);
                Set<String> players = usedCodesConfig.getKeys(false);
                for (String player : players) {
                    if (usedCodesConfig.contains(player + "." + content)) {
                        usedCodesConfig.set(player + "." + content, null);
                    }
                }
                plugin.saveCDKConfig();
                plugin.saveUsedCodesConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("delete_success").replace("%cdk%", content)));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_not_found").replace("%cdk%", content)));
            }
        } else if (deleteType.equals("id")) {
            Set<String> allCdkCodes = cdkConfig.getKeys(false);
            int deletedCount = 0;
            for (String cdkCode : allCdkCodes) {
                if (cdkCode.startsWith(content)) {
                    cdkConfig.set(cdkCode, null);
                    Set<String> players = usedCodesConfig.getKeys(false);
                    for (String player : players) {
                        if (usedCodesConfig.contains(player + "." + cdkCode)) {
                            usedCodesConfig.set(player + "." + cdkCode, null);
                        }
                    }
                    deletedCount++;
                }
            }
            if (deletedCount > 0) {
                plugin.saveCDKConfig();
                plugin.saveUsedCodesConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("delete_success").replace("%cdk%", "所有ID为 " + content + " 的CDK (共 " + deletedCount + " 个)")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_not_found").replace("%cdk%", "ID为 " + content + " 的CDK")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("delete_usage")));
        }
        return true;
    }

    private boolean handleListCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission("cdk.list")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        FileConfiguration cdkConfig = plugin.getCDKConfig();
        Set<String> cdkCodes = cdkConfig.getKeys(false);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_header")));

        if (cdkCodes.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_empty")));
        } else {
            for (String cdkCode : cdkCodes) {
                String id = cdkConfig.getString(cdkCode + ".id", cdkCode);
                List<String> commands = cdkConfig.getStringList(cdkCode + ".commands");
                String expiration = cdkConfig.getString(cdkCode + ".expiration", langConfig.getString("never_expires", "永不"));
                int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);

                String commandsDisplay = commands.isEmpty() ? "无命令" : String.join(", ", commands);

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_item")
                        .replace("%cdk%", cdkCode)
                        .replace("%id%", id)
                        .replace("%commands%", commandsDisplay)
                        .replace("%expiration%", expiration)
                        .replace("%remainingUses%", String.valueOf(remainingUses))
                ));
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_footer")));
        return true;
    }

    private boolean handleReloadCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission("cdk.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("reload_success")));
        return true;
    }

    private boolean handleExportCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission("cdk.export")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        FileConfiguration cdkConfig = plugin.getCDKConfig();
        File exportFile = new File(plugin.getDataFolder(), "cdk_export.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
            Set<String> cdkCodes = cdkConfig.getKeys(false);
            if (cdkCodes.isEmpty()) {
                writer.println("当前没有任何CDK可导出。");
            } else {
                writer.println("CDK 导出列表:");
                for (String cdkCode : cdkCodes) {
                    String type = cdkConfig.getString(cdkCode + ".type", "unknown");
                    List<String> commands = cdkConfig.getStringList(cdkCode + ".commands");
                    int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
                    String expiration = cdkConfig.getString(cdkCode + ".expiration", "永不");

                    writer.println("--------------------");
                    writer.println("CDK码: " + cdkCode);
                    writer.println("类型: " + type);
                    writer.println("剩余次数: " + remainingUses);
                    writer.println("过期时间: " + expiration);
                    writer.println("命令:");
                    for (String cmd : commands) {
                        writer.println("  - " + cmd);
                    }
                }
                writer.println("--------------------");
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("export_success").replace("%file%", exportFile.getName())));
        } catch (IOException e) {
            plugin.getLogger().severe("导出CDK时出错: " + e.getMessage());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("export_failed")));
        }
        return true;
    }

    private boolean handleUseCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("use_player_only")));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("cdk.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission_use")));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("use_usage")));
            return true;
        }

        String cdkCode = args[1];
        FileConfiguration cdkConfig = plugin.getCDKConfig();
        FileConfiguration usedCodesConfig = plugin.getUsedCodesConfig();

        if (!cdkConfig.contains(cdkCode)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_code")));
            return true;
        }

        String expirationStr = cdkConfig.getString(cdkCode + ".expiration");
        if (expirationStr != null) {
            try {
                Date expirationDate = DATE_FORMAT.parse(expirationStr);
                if (new Date().after(expirationDate)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_expired").replace("%cdk%", cdkCode)));
                    cdkConfig.set(cdkCode, null);
                    plugin.saveCDKConfig();
                    return true;
                }
            } catch (ParseException e) {
                plugin.getLogger().warning("CDK " + cdkCode + " 的过期时间格式错误: " + expirationStr);
            }
        }

        String cdkType = cdkConfig.getString(cdkCode + ".type", "single");
        if (cdkType.equalsIgnoreCase("single") && usedCodesConfig.contains(player.getName() + "." + cdkCode)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_already_used").replace("%cdk%", cdkCode)));
            return true;
        }

        int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
        if (remainingUses <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("max_usage")));
            cdkConfig.set(cdkCode, null);
            plugin.saveCDKConfig();
            return true;
        }

        List<String> commands = cdkConfig.getStringList(cdkCode + ".commands");
        for (String commandText : commands) {
            String commandToExecute = commandText.replace("%player%", player.getName());
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandToExecute);
        }

        cdkConfig.set(cdkCode + ".remainingUses", remainingUses - 1);

        if (cdkType.equalsIgnoreCase("single")) {
            usedCodesConfig.set(player.getName() + "." + cdkCode, true);
        }

        plugin.saveCDKConfig();
        plugin.saveUsedCodesConfig();

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("use_success").replace("%cdk%", cdkCode)));

        if (remainingUses - 1 == 0) {
            cdkConfig.set(cdkCode, null);
            plugin.saveCDKConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("max_usage")));
        }
        return true;
    }
}