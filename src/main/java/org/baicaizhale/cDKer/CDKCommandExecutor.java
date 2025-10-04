package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.model.CDK;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * CDK 命令执行器，处理所有与 CDK 相关的命令。
 */
public class CDKCommandExecutor implements CommandExecutor {

    private final CDKer plugin;
    private final ConfigurationManager configManager;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 构造函数
     * @param plugin 插件主类实例
     */
    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigurationManager();
    }

    /**
     * 检查命令发送者是否拥有指定权限。
     * @param sender 命令发送者
     * @param permission 所需权限
     * @param prefix 消息前缀
     * @param langConfig 语言配置
     * @return 如果拥有权限则返回 true，否则返回 false
     */
    private boolean checkPermission(CommandSender sender, String permission, String prefix, LanguageConfig langConfig) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("no_permission")));
            return false;
        }
        return true;
    }

    /**
     * 处理命令逻辑。
     * @param sender 命令发送者
     * @param command 命令对象
     * @param label 命令标签
     * @param args 命令参数
     * @return 如果命令被正确处理则返回 true，否则返回 false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = configManager.getPluginConfig().getPrefix();
        LanguageConfig langConfig = configManager.getLanguageConfig(configManager.getPluginConfig().getLanguage());

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return handleHelpCommand(sender, prefix, langConfig);
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("unknown_command")));
                return true;
        }
    }

    /**
     * 处理 help 命令。
     */
    private boolean handleHelpCommand(CommandSender sender, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.help", prefix, langConfig)) return true;

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_header")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_create")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_create_multiple")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_add")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_delete")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_list")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_reload")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_export")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_use")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_footer")));
        return true;
    }

    /**
     * 处理 create 命令。
     */
    private boolean handleCreateCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.create", prefix, langConfig)) return true;

        if (args.length < 5) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_single")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple")));
            return true;
        }

        String cdkType = args[1].toLowerCase();
        String cdkCode = args[2];
        int quantity;
        try {
            quantity = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity")));
            return true;
        }
        List<String> commands;
        String expiration = null;

        // Determine the command string and optional expiration
        StringBuilder commandStringBuilder = new StringBuilder();
        String potentialExpiration = null;
        int commandEndIndex = args.length - 1;

        // Check if the last argument could be an expiration date
        if (args.length > 5) { // Only consider expiration if there are enough arguments
            try {
                DATE_FORMAT.parse(args[args.length - 1]);
                // If parsing succeeds, it's likely an expiration date
                potentialExpiration = args[args.length - 1];
                commandEndIndex = args.length - 2; // Command string ends before the expiration
            } catch (ParseException ignored) {
                // Not a valid date format, so it's part of the command string
            }
        }

        // Reconstruct the command string
        for (int i = 4; i <= commandEndIndex; i++) {
            commandStringBuilder.append(args[i]);
            if (i < commandEndIndex) {
                commandStringBuilder.append(" ");
            }
        }
        commands = Arrays.asList(commandStringBuilder.toString().split("\\|"));

        expiration = potentialExpiration;
        if (expiration != null) {
            try {
                DATE_FORMAT.parse(expiration);
            } catch (ParseException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_date_format")));
                return true;
            }
        }

        if (configManager.getCdkMap().containsKey(cdkCode)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_already_exists").replace("%cdk%", cdkCode)));
            return true;
        }

        CDK newCdk = new CDK(cdkType, commands, quantity, expiration);
        configManager.getCdkMap().put(cdkCode, newCdk);
        configManager.saveCdkConfig();

        if (cdkType.equals("single")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_success_single")
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", cdkCode)));
        } else if (cdkType.equals("multiple")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_success_multiple")
                    .replace("%cdk%", cdkCode)
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", cdkCode)));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_cdk_type")));
        }
        return true;
    }

    /**
     * 处理 add 命令。
     */
    private boolean handleAddCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.add", prefix, langConfig)) return true;

        if (args.length < 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("add_usage")));
            return true;
        }

        String cdkCode = args[1];
        int quantityToAdd;
        try {
            quantityToAdd = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity")));
            return true;
        }

        CDK cdk = configManager.getCdkMap().get(cdkCode);
        if (cdk == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode)));
            return true;
        }

        cdk.setRemainingUses(cdk.getRemainingUses() + quantityToAdd);
        configManager.saveCdkConfig();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("add_success")
                .replace("%id%", cdkCode)
                .replace("%quantity%", String.valueOf(quantityToAdd))));
        return true;
    }

    /**
     * 处理 delete 命令。
     */
    private boolean handleDeleteCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.delete", prefix, langConfig)) return true;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("delete_usage")));
            return true;
        }

        String cdkCode = args[1];
        if (configManager.getCdkMap().remove(cdkCode) != null) {
            configManager.saveCdkConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("delete_success").replace("%cdk%", cdkCode)));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode)));
        }
        return true;
    }

    /**
     * 处理 list 命令。
     */
    private boolean handleListCommand(CommandSender sender, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.list", prefix, langConfig)) return true;

        Map<String, CDK> cdkMap = configManager.getCdkMap();
        if (cdkMap.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("list_empty")));
            return true;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("list_header")));
        for (Map.Entry<String, CDK> entry : cdkMap.entrySet()) {
            String cdkCode = entry.getKey();
            CDK cdk = entry.getValue();
            String commandsStr = String.join(", ", cdk.getCommands());
            String expirationStr = cdk.getExpiration() != null ? cdk.getExpiration() : "永不";
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("list_item")
                    .replace("%cdk%", cdkCode)
                    .replace("%id%", cdkCode) // Assuming id is the same as cdkCode for now
                    .replace("%commands%", commandsStr)
                    .replace("%expiration%", expirationStr)));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("list_footer")));
        return true;
    }

    /**
     * 处理 reload 命令。
     */
    private boolean handleReloadCommand(CommandSender sender, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.reload", prefix, langConfig)) return true;

        configManager.reloadAllConfigs();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("reload_success")));
        return true;
    }

    /**
     * 处理 export 命令。
     */
    private boolean handleExportCommand(CommandSender sender, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.export", prefix, langConfig)) return true;

        java.io.File exportFile = new java.io.File(plugin.getDataFolder(), "cdk_export.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
            Map<String, CDK> cdkMap = configManager.getCdkMap();
            if (cdkMap.isEmpty()) {
                writer.println("当前没有任何CDK可导出。");
            } else {
                writer.println("CDK 导出列表:");
                for (Map.Entry<String, CDK> entry : cdkMap.entrySet()) {
                    String cdkCode = entry.getKey();
                    CDK cdk = entry.getValue();
                    writer.println("--------------------");
                    writer.println("CDK码: " + cdkCode);
                    writer.println("类型: " + cdk.getType());
                    writer.println("剩余次数: " + cdk.getRemainingUses());
                    writer.println("过期时间: " + (cdk.getExpiration() != null ? cdk.getExpiration() : "永不"));
                    writer.println("命令:");
                    for (String cmd : cdk.getCommands()) writer.println("  - " + cmd);
                }
                writer.println("--------------------");
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("export_success").replace("%file%", exportFile.getName())));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "导出CDK时出错: " + e.getMessage(), e);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("export_failed")));
        }
        return true;
    }

    /**
     * 处理 use 命令。
     */
    private boolean handleUseCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("use_player_only")));
            return true;
        }
        Player player = (Player) sender;

        if (!checkPermission(player, "cdk.use", prefix, langConfig)) return true;

        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("use_usage")));
            return true;
        }

        String cdkCode = args[1];
        CDK cdk = configManager.getCdkMap().get(cdkCode);

        if (cdk == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode)));
            return true;
        }

        if (configManager.isCdkExpired(cdk)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_expired").replace("%cdk%", cdkCode)));
            // 不再删除过期CDK，仅提示过期
            return true;
        }

        if (cdk.getType().equalsIgnoreCase("single") && configManager.hasPlayerUsedCdk(player.getName(), cdkCode)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_already_used").replace("%cdk%", cdkCode)));
            return true;
        }

        if (cdk.getRemainingUses() <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("max_usage")));
            return true;
        }

        // 执行命令
        for (String commandText : cdk.getCommands()) {
            String commandToExecute = commandText.replace("%player%", player.getName());
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandToExecute);
        }

        // 更新剩余使用次数
        cdk.setRemainingUses(cdk.getRemainingUses() - 1);
        configManager.saveCdkConfig();

        // 标记玩家已使用一次性 CDK
        if (cdk.getType().equalsIgnoreCase("single")) {
            configManager.markPlayerUsedCdk(player.getName(), cdkCode);
            configManager.saveUsedCodesConfig();
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("use_success").replace("%cdk%", cdkCode)));

        if (cdk.getRemainingUses() == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("max_usage")));
        }
        return true;
    }
}