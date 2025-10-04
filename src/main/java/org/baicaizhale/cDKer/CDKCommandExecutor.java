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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * CDK 命令执行器，处理所有与 CDK 相关的命令。
 */
public class CDKCommandExecutor implements CommandExecutor {

    private final CDKer plugin;
    private final ConfigurationManager configManager;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 生成一个指定长度的随机字符串作为CDK代码。
     * @param length 随机字符串的长度
     * @return 生成的随机CDK代码
     */
    private String generateRandomCdkCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

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
            String message = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("no_permission"));
            sender.sendMessage(message);
            // 将发送给玩家的消息也输出到控制台，方便调试
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + message);
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
                String message = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("unknown_command"));
                sender.sendMessage(message);
                // 将发送给玩家的消息也输出到控制台，方便调试
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + message);
                return true;
        }
    }

    /**
     * 处理 help 命令。
     */
    private boolean handleHelpCommand(CommandSender sender, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.help", prefix, langConfig)) return true;

        String helpHeaderMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_header"));
        sender.sendMessage(helpHeaderMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpHeaderMessage);

        String helpCreateMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_create"));
        sender.sendMessage(helpCreateMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpCreateMessage);

        String helpCreateMultipleMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_create_multiple"));
        sender.sendMessage(helpCreateMultipleMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpCreateMultipleMessage);

        String helpAddMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_add"));
        sender.sendMessage(helpAddMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpAddMessage);

        String helpDeleteMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_delete"));
        sender.sendMessage(helpDeleteMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpDeleteMessage);

        String helpListMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_list"));
        sender.sendMessage(helpListMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpListMessage);

        String helpReloadMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_reload"));
        sender.sendMessage(helpReloadMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpReloadMessage);

        String helpExportMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_export"));
        sender.sendMessage(helpExportMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpExportMessage);

        String helpUseMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_use"));
        sender.sendMessage(helpUseMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpUseMessage);

        String helpFooterMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("help_footer"));
        sender.sendMessage(helpFooterMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpFooterMessage);
        return true;
    }

    /**
     * 处理 create 命令。
     */
    private boolean handleCreateCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.create", prefix, langConfig)) return true;

        if (args.length < 5) {
            String createUsageSingleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_single"));
            sender.sendMessage(createUsageSingleMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageSingleMessage);

            String createUsageMultipleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple"));
            sender.sendMessage(createUsageMultipleMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageMultipleMessage);
            return true;
        }

        String cdkType = args[1].toLowerCase();
        String cdkCode;
        int quantity;

        if (cdkType.equals("multiple")) {
            if (args.length < 6) { // create multiple <name|random> <id> <数量> "<命令1|命令2|...>" [有效时间]
                String createUsageMultipleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple"));
                sender.sendMessage(createUsageMultipleMessage);
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageMultipleMessage);
                return true;
            }
            String nameOrRandomValue = args[2]; // 使用不同的变量名
            cdkCode = args[3];
            try {
                quantity = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                String invalidQuantityMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity"));
                sender.sendMessage(invalidQuantityMessage);
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidQuantityMessage);
                return true;
            }
        } else if (cdkType.equals("single")) {
            if (args.length < 5) { // create single <id> <数量> "<命令1|命令2|...>" [有效时间]
                String createUsageSingleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_single"));
                sender.sendMessage(createUsageSingleMessage);
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageSingleMessage);
                return true;
            }
            cdkCode = args[2];
            try {
                quantity = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                String invalidQuantityMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity"));
                sender.sendMessage(invalidQuantityMessage);
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidQuantityMessage);
                return true;
            }
        } else {
            String invalidCdkTypeMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_cdk_type"));
            sender.sendMessage(invalidCdkTypeMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidCdkTypeMessage);
            return true;
        }

        List<String> commands;
        String expiration = null;

        // Determine the command string and optional expiration
        StringBuilder commandStringBuilder = new StringBuilder();
        String potentialExpiration = null;
        int commandStartIndex;
        int commandEndIndex;

        if (cdkType.equals("multiple")) {
            commandStartIndex = 5; // args[5] is the start of commands for 'multiple'
        } else { // single
            commandStartIndex = 4; // args[4] is the start of commands for 'single'
        }
        commandEndIndex = args.length - 1;

        // Check if the last argument could be an expiration date
        if (args.length > commandStartIndex + 1) { // Only consider expiration if there are enough arguments after commands
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
        for (int i = commandStartIndex; i <= commandEndIndex; i++) {
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
                String invalidDateFormatMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_date_format"));
                sender.sendMessage(invalidDateFormatMessage);
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidDateFormatMessage);
                return true;
            }
        }

        if (configManager.getCdkMap().containsKey(cdkCode)) {
            String cdkAlreadyExistsMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_already_exists").replace("%cdk%", cdkCode));
            sender.sendMessage(cdkAlreadyExistsMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + cdkAlreadyExistsMessage);
            return true;
        }

        CDK newCdk = new CDK(cdkType, commands, quantity, expiration);
        configManager.getCdkMap().put(cdkCode, newCdk);
        configManager.saveCdkConfig();

        if (cdkType.equals("single")) {
            String createSuccessSingleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_success_single")
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", cdkCode));
            sender.sendMessage(createSuccessSingleMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createSuccessSingleMessage);
        } else if (cdkType.equals("multiple")) {
            String createSuccessMultipleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_success_multiple")
                    .replace("%cdk%", cdkCode)
                    .replace("%quantity%", String.valueOf(quantity))
                    .replace("%id%", cdkCode));
            sender.sendMessage(createSuccessMultipleMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createSuccessMultipleMessage);
        } else {
            String invalidCdkTypeMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_cdk_type"));
            sender.sendMessage(invalidCdkTypeMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidCdkTypeMessage);
        }
        return true;
    }

    /**
     * 处理 add 命令。
     */
    private boolean handleAddCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.add", prefix, langConfig)) return true;

        if (args.length < 3) {
            String addUsageMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("add_usage"));
            sender.sendMessage(addUsageMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + addUsageMessage);
            return true;
        }

        String cdkCode = args[1];
        int quantityToAdd;
        try {
            quantityToAdd = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            String invalidQuantityMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity"));
            sender.sendMessage(invalidQuantityMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidQuantityMessage);
            return true;
        }

        CDK cdk = configManager.getCdkMap().get(cdkCode);
        if (cdk == null) {
            String cdkNotFoundMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode));
            sender.sendMessage(cdkNotFoundMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + cdkNotFoundMessage);
            return true;
        }

        cdk.setRemainingUses(cdk.getRemainingUses() + quantityToAdd);
        configManager.saveCdkConfig();

        String addSuccessMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("add_success")
                .replace("%id%", cdkCode)
                .replace("%quantity%", String.valueOf(quantityToAdd)));
        sender.sendMessage(addSuccessMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + addSuccessMessage);
        return true;
    }

    /**
     * 处理 delete 命令。
     */
    private boolean handleDeleteCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.delete", prefix, langConfig)) return true;

        if (args.length < 2) {
            String deleteUsageMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("delete_usage"));
            sender.sendMessage(deleteUsageMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + deleteUsageMessage);
            return true;
        }

        String cdkCode = args[1];
        if (configManager.getCdkMap().remove(cdkCode) != null) {
            configManager.saveCdkConfig();
            String deleteSuccessMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("delete_success").replace("%cdk%", cdkCode));
            sender.sendMessage(deleteSuccessMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + deleteSuccessMessage);
        } else {
            String cdkNotFoundMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode));
            sender.sendMessage(cdkNotFoundMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + cdkNotFoundMessage);
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
            String listEmptyMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("list_empty"));
            sender.sendMessage(listEmptyMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + listEmptyMessage);
            return true;
        }

        String listHeaderMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("list_header"));
        sender.sendMessage(listHeaderMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + listHeaderMessage);

        for (Map.Entry<String, CDK> entry : cdkMap.entrySet()) {
            String cdkCode = entry.getKey();
            CDK cdk = entry.getValue();
            String commandsStr = String.join(", ", cdk.getCommands());
            String expirationStr = cdk.getExpiration() != null ? cdk.getExpiration() : "永不";
            String listItemMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("list_item")
                    .replace("%cdk%", cdkCode)
                    .replace("%id%", cdkCode) // Assuming id is the same as cdkCode for now
                    .replace("%commands%", commandsStr)
                    .replace("%expiration%", expirationStr));
            sender.sendMessage(listItemMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + listItemMessage);
        }
        String listFooterMessage = ChatColor.translateAlternateColorCodes('&', langConfig.getMessage("list_footer"));
        sender.sendMessage(listFooterMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + listFooterMessage);
        return true;
    }

    /**
     * 处理 reload 命令。
     */
    private boolean handleReloadCommand(CommandSender sender, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.reload", prefix, langConfig)) return true;

        configManager.reloadAllConfigs();
        String reloadSuccessMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("reload_success"));
        sender.sendMessage(reloadSuccessMessage);
        plugin.getLogger().info("[To Player] " + sender.getName() + ": " + reloadSuccessMessage);
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
            String exportSuccessMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("export_success").replace("%file%", exportFile.getName()));
            sender.sendMessage(exportSuccessMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + exportSuccessMessage);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "导出CDK时出错: " + e.getMessage(), e);
            String exportFailedMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("export_failed"));
            sender.sendMessage(exportFailedMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + exportFailedMessage);
        }
        return true;
    }

    /**
     * 处理 use 命令。
     */
    private boolean handleUseCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!(sender instanceof Player)) {
            String usePlayerOnlyMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("use_player_only"));
            sender.sendMessage(usePlayerOnlyMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + usePlayerOnlyMessage);
            return true;
        }
        Player player = (Player) sender;

        if (!checkPermission(player, "cdk.use", prefix, langConfig)) return true;

        if (args.length < 2) {
            String useUsageMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("use_usage"));
            player.sendMessage(useUsageMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + useUsageMessage);
            return true;
        }

        String cdkCode = args[1];
        CDK cdk = configManager.getCdkMap().get(cdkCode);

        if (cdk == null) {
            String cdkNotFoundMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_not_found").replace("%cdk%", cdkCode));
            player.sendMessage(cdkNotFoundMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + cdkNotFoundMessage);
            return true;
        }

        if (configManager.isCdkExpired(cdk)) {
            String cdkExpiredMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_expired").replace("%cdk%", cdkCode));
            player.sendMessage(cdkExpiredMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + cdkExpiredMessage);
            // 不再删除过期CDK，仅提示过期
            return true;
        }

        if (cdk.getType().equalsIgnoreCase("single") && configManager.hasPlayerUsedCdk(player.getName(), cdkCode)) {
            String cdkAlreadyUsedMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_already_used").replace("%cdk%", cdkCode));
            player.sendMessage(cdkAlreadyUsedMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + cdkAlreadyUsedMessage);
            return true;
        }

        if (cdk.getRemainingUses() <= 0) {
            String maxUsageMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("max_usage"));
            player.sendMessage(maxUsageMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + maxUsageMessage);
            return true;
        }

        // 执行命令
        for (String commandText : cdk.getCommands()) {
            String commandToExecute = commandText.replace("%player%", player.getName());
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandToExecute);
            plugin.getLogger().info("Player " + player.getName() + " executed command: " + commandToExecute);
        }

        // 更新剩余使用次数
        cdk.setRemainingUses(cdk.getRemainingUses() - 1);
        configManager.saveCdkConfig();

        // 标记玩家已使用一次性 CDK
        if (cdk.getType().equalsIgnoreCase("single")) {
            configManager.markPlayerUsedCdk(player.getName(), cdkCode);
            configManager.saveUsedCodesConfig();
        }

        String useSuccessMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("use_success").replace("%cdk%", cdkCode));
        player.sendMessage(useSuccessMessage);
        plugin.getLogger().info("[To Player] " + player.getName() + ": " + useSuccessMessage);

        if (cdk.getRemainingUses() == 0) {
            String maxUsageMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("max_usage"));
            player.sendMessage(maxUsageMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + maxUsageMessage);
        }
        return true;
    }
}