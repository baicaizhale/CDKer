package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.model.CDK;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.baicaizhale.cDKer.command.CommandUtils.checkPermission;

// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.util.Map;
// import java.util.logging.Level;

/**
 * 处理 /cdk use 命令的执行器。
 */
public class UseCommandExecutor {

    private final CDKer plugin;
    private final ConfigurationManager configManager;

    public UseCommandExecutor(CDKer plugin, ConfigurationManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * 处理使用 CDK 命令的逻辑。
     * @param sender 命令发送者
     * @param args 命令参数
     * @param prefix 插件前缀
     * @param langConfig 语言配置
     * @return 命令是否成功执行
     */
    public boolean handleUseCommand(CommandSender sender, String[] args, String prefix, LanguageConfig langConfig) {
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

        if (configManager.hasPlayerUsedCdk(player.getName(), cdkCode)) {
            String cdkAlreadyUsedMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_already_used").replace("%cdk%", cdkCode));
            player.sendMessage(cdkAlreadyUsedMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + cdkAlreadyUsedMessage);
            return true;
        }

        if (cdk.getRemainingUses() <= 0) {
            String cdkUsedUpMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("cdk_used_up").replace("%cdk%", cdkCode));
            player.sendMessage(cdkUsedUpMessage);
            plugin.getLogger().info("[To Player] " + player.getName() + ": " + cdkUsedUpMessage);
            return true;
        }

        // 执行命令
        for (String commandText : cdk.getCommands()) {
            String commandToExecute = commandText.replace("%player%", player.getName());
            // 检查命令是否被双引号包裹，并移除它们
            if (commandToExecute.startsWith("\"") && commandToExecute.endsWith("\"")) {
                commandToExecute = commandToExecute.substring(1, commandToExecute.length() - 1);
            }
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandToExecute);
            plugin.getLogger().info("Player " + player.getName() + " executed command: " + commandToExecute);
        }

        // 更新剩余使用次数
        cdk.setRemainingUses(cdk.getRemainingUses() - 1);
        configManager.saveCdkConfig();

        // 标记玩家已使用 CDK
        configManager.markPlayerUsedCdk(player.getName(), cdkCode);
        configManager.saveUsedCodesConfig();

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