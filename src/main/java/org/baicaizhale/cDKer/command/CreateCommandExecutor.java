package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CDK;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import static org.baicaizhale.cDKer.command.CommandUtils.checkPermission;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 处理 /cdkadmin create 命令的执行器。
 */
public class CreateCommandExecutor {

    private final CDKer plugin;
    private final ConfigurationManager configManager;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public CreateCommandExecutor(CDKer plugin, ConfigurationManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * 处理创建 CDK 命令的逻辑。
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 插件前缀
     * @param langConfig 语言配置
     * @return 命令是否成功执行
     */
    public boolean handleCreateCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
        if (!checkPermission(sender, "cdk.create", prefix, langConfig)) return true;

        if (args.length < 2) { // 最少需要 /cdk create
            String createUsageSingleMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_single"));
            sender.sendMessage(createUsageSingleMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageSingleMessage);
            String createUsageMultipleNameMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple_name"));
            sender.sendMessage(createUsageMultipleNameMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageMultipleNameMessage);
            String createUsageMultipleRandomMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple_random"));
            sender.sendMessage(createUsageMultipleRandomMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageMultipleRandomMessage);
            return true;
        }

        String cdkType = args[1].toLowerCase();
        String cdkCode;
        int quantity;
        int commandStartIndex = 0;

        if (cdkType.equals("multiple")) {
            String nameOrRandomValue = args[2];
            if (nameOrRandomValue.equalsIgnoreCase("random")) {
                if (args.length < 5) { // create multiple random <数量> "<命令1|命令2|...>" [有效时间]
                    String createUsageMultipleRandomMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple_random"));
                    sender.sendMessage(createUsageMultipleRandomMessage);
                    plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageMultipleRandomMessage);
                    return true;
                }
                cdkCode = generateRandomCdkCode(8); // 生成8位随机CDK代码
                try {
                    quantity = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    String invalidQuantityMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity"));
                    sender.sendMessage(invalidQuantityMessage);
                    plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidQuantityMessage);
                    return true;
                }
                commandStartIndex = 4; // args[4] is the start of commands for 'multiple random'
            } else {
                if (args.length < 6) { // create multiple <name> <id> <数量> "<命令1|命令2|...>" [有效时间]
                    String createUsageMultipleNameMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("create_usage_multiple_name"));
                    sender.sendMessage(createUsageMultipleNameMessage);
                    plugin.getLogger().info("[To Player] " + sender.getName() + ": " + createUsageMultipleNameMessage);
                    return true;
                }
                cdkCode = args[3];
                try {
                    quantity = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    String invalidQuantityMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("invalid_quantity"));
                    sender.sendMessage(invalidQuantityMessage);
                    plugin.getLogger().info("[To Player] " + sender.getName() + ": " + invalidQuantityMessage);
                    return true;
                }
                commandStartIndex = 5; // args[5] is the start of commands for 'multiple <name>'
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
            commandStartIndex = 4; // args[4] is the start of commands for 'single'
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
        int commandEndIndex;

        // Check if the last argument could be an expiration date
        if (args.length > commandStartIndex + 1) { // Only consider expiration if there are enough arguments after commands
            try {
                DATE_FORMAT.parse(args[args.length - 1]);
                // If parsing succeeds, it's likely an expiration date
                potentialExpiration = args[args.length - 1];
                commandEndIndex = args.length - 2; // Command string ends before the expiration
            } catch (ParseException ignored) {
                // Not a valid date format, so it's part of the command string
                commandEndIndex = args.length - 1;
            }
        } else {
            commandEndIndex = args.length - 1;
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
     * 生成指定长度的随机 CDK 代码。
     * @param length CDK 代码长度
     * @return 随机生成的 CDK 代码
     */
    private String generateRandomCdkCode(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }
}