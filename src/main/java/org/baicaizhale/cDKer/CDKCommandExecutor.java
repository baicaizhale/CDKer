package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.model.CDK;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.baicaizhale.cDKer.command.CreateCommandExecutor;
import org.baicaizhale.cDKer.command.UseCommandExecutor;
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
     * 构造函数
     * @param plugin 插件主类实例
     */
    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigurationManager();
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
                CreateCommandExecutor createExecutor = new CreateCommandExecutor(plugin, configManager);
                return createExecutor.handleCreateCommand(sender, args, prefix, langConfig);
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
                UseCommandExecutor useCommandExecutor = new UseCommandExecutor(plugin, configManager);
                return useCommandExecutor.handleUseCommand(sender, args, prefix, langConfig);
            default:
                String message = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("unknown_command"));
                sender.sendMessage(message);
                // 将发送给玩家的消息也输出到控制台，方便调试
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + message);
                return true;
        }
    }

    /**
     * 检查玩家是否有执行命令的权限。
     * @param sender 命令发送者
     * @param permission 所需权限
     * @param prefix 插件前缀
     * @param langConfig 语言配置
     * @return 如果玩家有权限则返回 true，否则返回 false
     */
    private boolean checkPermission(CommandSender sender, String permission, String prefix, LanguageConfig langConfig) {
        if (!sender.hasPermission(permission)) {
            String noPermissionMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("no_permission"));
            sender.sendMessage(noPermissionMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + noPermissionMessage);
            return false;
        }
        return true;
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

}