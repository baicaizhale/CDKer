package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CDKCommandExecutor 类处理所有与 CDK 插件相关的命令。
 * 它实现了 CommandExecutor 接口，用于注册和执行命令。
 */
public class CDKCommandExecutor implements CommandExecutor {

    private final CDKer plugin;
    private final ConfigurationManager configManager;

    public CDKCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigurationManager();
    }

    /**
     * 当一个命令被执行时调用。
     * @param sender 命令发送者
     * @param command 被执行的命令
     * @param label 命令的别名
     * @param args 命令参数
     * @return 如果命令被成功处理则返回 true，否则返回 false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', configManager.getPluginConfig().getPrefix());
        LanguageConfig langConfig = configManager.getLanguageConfig(configManager.getPluginConfig().getLanguage());

        if (args.length == 0) {
            String helpMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("help_message"));
            sender.sendMessage(helpMessage);
            plugin.getLogger().info("[To Player] " + sender.getName() + ": " + helpMessage);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                CreateCommandExecutor createExecutor = new CreateCommandExecutor(plugin, configManager);
                return createExecutor.handleCreateCommand(sender, args, prefix, langConfig);
            case "use":
                if (!(sender instanceof Player)) {
                    String playerOnlyMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("player_only_command"));
                    sender.sendMessage(playerOnlyMessage);
                    plugin.getLogger().info("[To Player] " + sender.getName() + ": " + playerOnlyMessage);
                    return true;
                }
                UseCommandExecutor useExecutor = new UseCommandExecutor(plugin, configManager);
                return useExecutor.handleUseCommand((Player) sender, args, prefix, langConfig);
            case "add":
                AddCommandExecutor addExecutor = new AddCommandExecutor(plugin, configManager);
                return addExecutor.handleAddCommand(sender, args, prefix, langConfig);
            // 其他子命令将在这里添加
            default:
                String unknownCommandMessage = ChatColor.translateAlternateColorCodes('&', prefix + langConfig.getMessage("unknown_command"));
                sender.sendMessage(unknownCommandMessage);
                plugin.getLogger().info("[To Player] " + sender.getName() + ": " + unknownCommandMessage);
                return true;
        }
    }
}