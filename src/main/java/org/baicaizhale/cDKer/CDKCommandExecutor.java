package org.baicaizhale.cDKer;

// 导入 Bukkit 聊天颜色工具类
import org.bukkit.ChatColor;
// 导入 Bukkit 命令相关接口
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
// 导入 Bukkit 配置文件处理类
import org.bukkit.configuration.file.FileConfiguration;
// 导入 Bukkit 玩家类
import org.bukkit.entity.Player;

// 导入 Java IO、日期、集合等工具类
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// CDK 命令执行器类，实现 CommandExecutor 接口
public class CDKCommandExecutor implements CommandExecutor {

    // 插件主类实例
    private final CDKer plugin;
    // 日期格式化工具，用于处理过期时间
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // 构造方法，注入插件主类
    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    // 命令处理主��口
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration langConfig = plugin.getLangConfig(); // 获取语言配置
        String prefix = plugin.getPrefix(); // 获取消息前缀

        // 无参数或 help 命令时，发送帮助信息
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender, prefix, langConfig);
            return true;
        }

        // 根据命令参数分发到不同处理方法
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
                sendUnknownCommand(sender, prefix, langConfig);
                return true;
        }
    }

    // 发送未知命令提示及帮助信息
    private void sendUnknownCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("unknown_command")));
        sendHelpMessage(sender, prefix, langConfig);
    }

    // 检查权限，若无权限则发送提示
    private boolean checkPermission(CommandSender sender, String permission, String prefix, FileConfiguration langConfig) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return false;
        }
        return true;
    }

    // 发送帮助信息
    private void sendHelpMessage(CommandSender sender, String prefix, FileConfiguration langConfig) {
        String[] helpKeys = {"help_header", "help_create", "help_create_multiple", "help_add", "help_delete", "help_list", "help_reload", "help_export", "help_use", "help_footer"};
        for (String key : helpKeys) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString(key)));
        }
    }

    // 处理 create 命令，创建新的 CDK
    private boolean handleCreateCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!checkPermission(sender, "cdk.create", prefix, langConfig)) return true;

        if (args.length < 5) {
            sendHelpMessage(sender, prefix, langConfig);
            return true;
        }

        String cdkType = args[1].toLowerCase(); // CDK 类型
        String id;
        int quantity;
        String commandsString;
        String expirationTime = null;

        try {
            if (cdkType.equals("single")) {
                id = args[2];
                quantity = Integer.parseInt(args[3]);
                commandsString = args[4];
                if (args.length > 5) expirationTime = args[5] + (args.length > 6 ? " " + args[6] : "");
            } else if (cdkType.equals("multiple")) {
                id = args[2].equalsIgnoreCase("random") ? UUID.randomUUID().toString().substring(0, 8) : args[3];
                quantity = Integer.parseInt(args[4]);
                commandsString = args[5];
                if (args.length > 6) expirationTime = args[6] + (args.length > 7 ? " " + args[7] : "");
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_cdk_type")));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_quantity")));
            return true;
        }

        List<String> commands = parseCommands(commandsString); // 解析命令字符串
        Date expirationDate = null;
        if (expirationTime != null) {
            try {
                expirationDate = DATE_FORMAT.parse(expirationTime); // 解析过期时间
            } catch (ParseException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_date_format")));
                return true;
            }
        }

        FileConfiguration cdkConfig = plugin.getCDKConfig(); // 获取 CDK 配置

        if (cdkConfig.contains(id)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_already_exists").replace("%cdk%", id)));
            return true;
        }

        cdkConfig.set(id + ".type", cdkType); // 设置 CDK 类型
        cdkConfig.set(id + ".commands", commands); // 设置命令
        cdkConfig.set(id + ".remainingUses", quantity); // 设置可用次数
        if (expirationDate != null) cdkConfig.set(id + ".expiration", DATE_FORMAT.format(expirationDate)); // 设置过期时间
        plugin.saveCDKConfig(); // 保存配置

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString(cdkType.equals("single") ? "create_success_single" : "create_success_multiple")
                .replace("%quantity%", String.valueOf(quantity)).replace("%id%", id).replace("%cdk%", id)));
        return true;
    }

    // 解析命令字符串，支持多条命令
    private List<String> parseCommands(String commandsString) {
        if (commandsString.startsWith("\"") && commandsString.endsWith("\"")) {
            String rawCommands = commandsString.substring(1, commandsString.length() - 1);
            String[] splitCommands = rawCommands.split("\\|");
            List<String> commands = new ArrayList<>();
            for (String cmd : splitCommands) commands.add(cmd.trim());
            return commands;
        } else {
            return Collections.singletonList(commandsString);
        }
    }

    // 处理 add 命令，增加 CDK 使用次数
    private boolean handleAddCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!checkPermission(sender, "cdk.add", prefix, langConfig)) return true;

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
                    .replace("%id%", cdkCode).replace("%quantity%", String.valueOf(currentUses + quantity))));
        } else {
            cdkConfig.set(cdkCode + ".type", "single");
            cdkConfig.set(cdkCode + ".commands", new ArrayList<String>());
            cdkConfig.set(cdkCode + ".remainingUses", quantity);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_success")
                    .replace("%id%", cdkCode).replace("%quantity%", String.valueOf(quantity))));
        }

        plugin.saveCDKConfig();
        return true;
    }

    // 处理 delete 命令，删除 CDK 或按 ID 批量删除
    private boolean handleDeleteCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        if (!checkPermission(sender, "cdk.delete", prefix, langConfig)) return true;

        if (args.length < 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("delete_usage")));
            return true;
        }

        String deleteType = args[1].toLowerCase(); // 删除类型
        String content = args[2]; // CDK 或 ID

        FileConfiguration cdkConfig = plugin.getCDKConfig();
        FileConfiguration usedCodesConfig = plugin.getUsedCodesConfig();

        if (deleteType.equals("cdk")) {
            if (cdkConfig.contains(content)) {
                cdkConfig.set(content, null);
                for (String player : usedCodesConfig.getKeys(false)) {
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
            int deletedCount = 0;
            for (String cdkCode : cdkConfig.getKeys(false)) {
                if (cdkCode.startsWith(content)) {
                    cdkConfig.set(cdkCode, null);
                    for (String player : usedCodesConfig.getKeys(false)) {
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

    // 处理 list 命令，列出所有 CDK 信息
    private boolean handleListCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        if (!checkPermission(sender, "cdk.list", prefix, langConfig)) return true;

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
                        .replace("%cdk%", cdkCode).replace("%id%", id).replace("%commands%", commandsDisplay)
                        .replace("%expiration%", expiration).replace("%remainingUses", String.valueOf(remainingUses))));
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_footer")));
        return true;
    }

    // 处理 reload 命令，重载插件配置
    private boolean handleReloadCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        if (!checkPermission(sender, "cdk.reload", prefix, langConfig)) return true;

        plugin.reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("reload_success")));
        return true;
    }

    // 处理 export 命令，导出所有 CDK 到文本文件
    private boolean handleExportCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        if (!checkPermission(sender, "cdk.export", prefix, langConfig)) return true;

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
                    for (String cmd : commands) writer.println("  - " + cmd);
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

    // 处理 use 命令，玩家使用 CDK
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

