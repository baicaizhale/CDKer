package org.baicaizhale.cDKer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    // 日期格式化器，用于解析和格式化过期时间
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // 构造器，初始化插件实例
    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 获取语言文件和消息前缀
        FileConfiguration langConfig = plugin.getLangConfig();
        String prefix = plugin.getPrefix();

        // 如果没有子命令，显示帮助信息
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender, prefix, langConfig);
            return true;
        }

        // 处理不同的子命令
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
                // 未知命令
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("unknown_command")));
                sendHelpMessage(sender, prefix, langConfig);
                return true;
        }
    }

    /**
     * 发送帮助信息给命令发送者
     * @param sender 命令发送者
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     */
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

    /**
     * 处理 'create' 子命令，用于创建新的CDK
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleCreateCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        // 检查权限
        if (!sender.hasPermission("cdk.create")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        // 检查参数数量
        if (args.length < 5) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_single")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_multiple")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_example_single")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_example_multiple")));
            return true;
        }

        String cdkType = args[1].toLowerCase(); // single 或 multiple
        String id;
        int quantity;
        String commandsString;
        String expirationTime = null;

        // 解析参数
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
                expirationTime = args[5] + " " + args[6]; // 日期和时间分开
            }
        } else if (cdkType.equals("multiple")) {
            if (args.length < 6) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_usage_multiple")));
                return true;
            }
            String nameOrRandom = args[2].toLowerCase();
            if (nameOrRandom.equals("random")) {
                id = UUID.randomUUID().toString().substring(0, 8); // 随机生成ID
            } else {
                id = args[3]; // 使用指定的ID
            }
            try {
                quantity = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_quantity")));
                return true;
            }
            commandsString = args[5];
            if (args.length > 6) {
                expirationTime = args[6] + " " + args[7]; // 日期和时间分开
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_cdk_type")));
            return true;
        }

        // 解析命令列表
        List<String> commands = new ArrayList<>();
        if (commandsString.startsWith("\"") && commandsString.endsWith("\"")) {
            String rawCommands = commandsString.substring(1, commandsString.length() - 1);
            String[] splitCommands = rawCommands.split("\\|");
            for (String cmd : splitCommands) {
                commands.add(cmd.trim());
            }
        } else {
            commands.add(commandsString); // 如果没有双引号，则认为是一个命令
        }


        // 解析过期时间
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

        // 检查CDK是否已存在
        if (cdkConfig.contains(id)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_already_exists").replace("%cdk%", id)));
            return true;
        }

        // 保存CDK信息
        cdkConfig.set(id + ".type", cdkType);
        cdkConfig.set(id + ".commands", commands);
        cdkConfig.set(id + ".remainingUses", quantity);
        if (expirationDate != null) {
            cdkConfig.set(id + ".expiration", DATE_FORMAT.format(expirationDate));
        }
        plugin.saveCDKConfig();

        // 发送成功消息
        if (cdkType.equals("single")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_success_single")
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", id)));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("create_success_multiple")
                    .replace("%cdk%", id) // 这里用id作为cdk名称
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", id)));
        }
        return true;
    }

    /**
     * 处理 'add' 子命令，用于批量生成/增加可使用次数
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleAddCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        // 检查权限
        if (!sender.hasPermission("cdk.add")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        // 检查参数数量
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

        // 检查CDK是否存在，如果存在则增加数量，否则创建
        if (cdkConfig.contains(cdkCode)) {
            int currentUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
            cdkConfig.set(cdkCode + ".remainingUses", currentUses + quantity);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_success")
                    .replace("%id%", cdkCode)
                    .replace("%quantity%", String.valueOf(currentUses + quantity))));
        } else {
            // 如果CDK不存在，则创建新的CDK
            // 这里的逻辑可以根据需求调整，是创建一次性还是多次使用，或者需要更多参数
            // 暂时简化为创建一个默认的单次使用CDK，命令为空
            cdkConfig.set(cdkCode + ".type", "single");
            cdkConfig.set(cdkCode + ".commands", new ArrayList<String>()); // 默认空命令
            cdkConfig.set(cdkCode + ".remainingUses", quantity);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("add_success")
                    .replace("%id%", cdkCode)
                    .replace("%quantity%", String.valueOf(quantity))));
        }

        plugin.saveCDKConfig();
        return true;
    }

    /**
     * 处理 'delete' 子命令，用于删除CDK或某个ID下的所有CDK
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleDeleteCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        // 检查权限
        if (!sender.hasPermission("cdk.delete")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        // 检查参数数量
        if (args.length < 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("delete_usage")));
            return true;
        }

        String deleteType = args[1].toLowerCase(); // cdk 或 id
        String content = args[2]; // CDK码 或 ID

        FileConfiguration cdkConfig = plugin.getCDKConfig();
        FileConfiguration usedCodesConfig = plugin.getUsedCodesConfig();

        if (deleteType.equals("cdk")) {
            // 删除单个CDK
            if (cdkConfig.contains(content)) {
                cdkConfig.set(content, null); // 删除CDK
                // 同时删除所有玩家使用该CDK的记录
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
            // 删除某个ID下的所有CDK (这里假设ID是CDK码的前缀或者某种分组)
            // 遍历所有CDK，删除匹配的
            Set<String> allCdkCodes = cdkConfig.getKeys(false);
            int deletedCount = 0;
            for (String cdkCode : allCdkCodes) {
                if (cdkCode.startsWith(content)) { // 假设ID是CDK码的前缀
                    cdkConfig.set(cdkCode, null);
                    // 删除所有玩家使用该CDK的记录
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

    /**
     * 处理 'list' 子命令，列出所有CDK
     * @param sender 命令发送者
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleListCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        // 检查权限
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
                String id = cdkConfig.getString(cdkCode + ".id", cdkCode); // 优先使用id字段，否则用cdkCode
                List<String> commands = cdkConfig.getStringList(cdkCode + ".commands");
                String expiration = cdkConfig.getString(cdkCode + ".expiration", langConfig.getString("never_expires", "永不"));
                int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);

                String commandsDisplay = commands.isEmpty() ? "无命令" : String.join(", ", commands);

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_item")
                        .replace("%cdk%", cdkCode)
                        .replace("%id%", id)
                        .replace("%commands%", commandsDisplay)
                        .replace("%expiration%", expiration)
                        .replace("%remainingUses%", String.valueOf(remainingUses)) // 添加剩余次数占位符
                ));
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("list_footer")));
        return true;
    }

    /**
     * 处理 'reload' 子命令，重新加载配置文件
     * @param sender 命令发送者
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleReloadCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        // 检查权限
        if (!sender.hasPermission("cdk.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission")));
            return true;
        }

        plugin.reloadConfig(); // 重新加载 config.yml
        // 重新加载 cdk.yml
        File cdkFile = new File(plugin.getDataFolder(), "cdk.yml");
        if (cdkFile.exists()) {
            FileConfiguration cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);
            plugin.setCDKConfig(cdkConfig);
        } else {
            plugin.getLogger().warning("cdk.yml 文件未找到，无法重载！");
        }
        // 重新加载 used_codes.yml
        File usedCodesFile = new File(plugin.getDataFolder(), "used_codes.yml");
        if (usedCodesFile.exists()) {
            FileConfiguration usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile);
            plugin.setUsedCodesConfig(usedCodesConfig); // 需要在CDKer中添加setUsedCodesConfig方法
        } else {
            plugin.getLogger().warning("used_codes.yml 文件未找到，无法重载！");
        }
        // 重新加载语言文件
        String language = plugin.getConfig().getString("language", "cn");
        File newLangFile = new File(plugin.getDataFolder(), "lang/lang_" + language + ".yml");
        if (newLangFile.exists()) {
            FileConfiguration newLangConfig = YamlConfiguration.loadConfiguration(newLangFile);
            plugin.setLangConfig(newLangConfig); // 需要在CDKer中添加setLangConfig方法
        } else {
            plugin.getLogger().warning("语言文件 lang_" + language + ".yml 未找到，无法重载！");
        }
        // 更新前缀
        plugin.setPrefix(plugin.getConfig().getString("prefix", "&bCDKer &7> &f")); // 需要在CDKer中添加setPrefix方法

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("reload_success")));
        return true;
    }

    /**
     * 处理 'export' 子命令，导出所有CDK到文件
     * @param sender 命令发送者
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleExportCommand(CommandSender sender, String prefix, FileConfiguration langConfig) {
        // 检查权限
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

    /**
     * 处理 'use' 子命令，用于玩家使用CDK
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 消息前缀
     * @param langConfig 语言配置文件
     * @return 命令是否成功执行
     */
    private boolean handleUseCommand(CommandSender sender, String[] args, String prefix, FileConfiguration langConfig) {
        // 检查是否是玩家
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("use_player_only")));
            return true;
        }
        Player player = (Player) sender;

        // 检查权限
        if (!player.hasPermission("cdk.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("no_permission_use")));
            return true;
        }

        // 检查参数数量
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("use_usage")));
            return true;
        }

        String cdkCode = args[1];
        FileConfiguration cdkConfig = plugin.getCDKConfig();
        FileConfiguration usedCodesConfig = plugin.getUsedCodesConfig();

        // 检查CDK是否存在
        if (!cdkConfig.contains(cdkCode)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("invalid_code")));
            return true;
        }

        // 检查是否已过期
        String expirationStr = cdkConfig.getString(cdkCode + ".expiration");
        if (expirationStr != null) {
            try {
                Date expirationDate = DATE_FORMAT.parse(expirationStr);
                if (new Date().after(expirationDate)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_expired").replace("%cdk%", cdkCode)));
                    // 标记为已过期并删除（可选，但推荐）
                    cdkConfig.set(cdkCode, null);
                    plugin.saveCDKConfig();
                    return true;
                }
            } catch (ParseException e) {
                plugin.getLogger().warning("CDK " + cdkCode + " 的过期时间格式错误: " + expirationStr);
                // 即使格式错误，也允许使用，或者根据需求选择阻止使用
            }
        }

        // 检查玩家是否已使用过此CDK (仅对单次CDK有效)
        String cdkType = cdkConfig.getString(cdkCode + ".type", "single"); // 默认为single
        if (cdkType.equalsIgnoreCase("single") && usedCodesConfig.contains(player.getName() + "." + cdkCode)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("cdk_already_used").replace("%cdk%", cdkCode)));
            return true;
        }

        // 检查剩余次数
        int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
        if (remainingUses <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("max_usage")));
            // 如果次数为0，也删除该CDK
            cdkConfig.set(cdkCode, null);
            plugin.saveCDKConfig();
            return true;
        }

        // 执行命令
        List<String> commands = cdkConfig.getStringList(cdkCode + ".commands");
        for (String commandText : commands) {
            String commandToExecute = commandText.replace("%player%", player.getName());
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandToExecute);
        }

        // 减少剩余次数
        cdkConfig.set(cdkCode + ".remainingUses", remainingUses - 1);

        // 记录玩家使用
        if (cdkType.equalsIgnoreCase("single")) {
            usedCodesConfig.set(player.getName() + "." + cdkCode, true);
        }

        // 保存配置
        plugin.saveCDKConfig();
        plugin.saveUsedCodesConfig();

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("use_success").replace("%cdk%", cdkCode)));

        // 如果剩余次数为0，删除CDK
        if (remainingUses - 1 == 0) {
            cdkConfig.set(cdkCode, null);
            plugin.saveCDKConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getString("max_usage"))); // 再次发送已用完消息
        }
        return true;
    }
}
