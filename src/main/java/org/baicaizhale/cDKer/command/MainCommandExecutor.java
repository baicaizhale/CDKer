package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.baicaizhale.cDKer.model.LanguageConfig;
import org.baicaizhale.cDKer.command.impl.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// ...existing code...

public class MainCommandExecutor implements CommandExecutor, TabCompleter {

    private final CDKer plugin;
    private final ConfigurationManager configManager;

    public MainCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigurationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 获取插件配置
        String prefix = ChatColor.translateAlternateColorCodes('&', configManager.getPluginConfig().getPrefix());
        LanguageConfig langConfig = configManager.getLanguageConfig(configManager.getPluginConfig().getLanguage());

        if (args.length == 0) {
            // 显示帮助信息
            sendHelpMessage(sender, prefix, langConfig);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                return new HelpCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "create":
                if (args.length > 1) {
                    return new CreateCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示create命令的帮助
                    sender.sendMessage(new CreateCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "use":
                if (args.length > 1) {
                    return new UseCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示use命令的帮助
                    sender.sendMessage(new UseCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "add":
                if (args.length > 1) {
                    return new AddCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示add命令的帮助
                    sender.sendMessage(new AddCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "del":
            case "delete":
                if (args.length > 1) {
                    return new DeleteCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示delete命令的帮助
                    sender.sendMessage(new DeleteCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "list":
                return new ListCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "log":
                return new org.baicaizhale.cDKer.command.impl.LogCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "reload":
                return new ReloadCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "export":
                if (args.length > 1) {
                    return new ExportCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示export命令的帮助
                    sender.sendMessage(new ExportCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "import":
                if (args.length > 1) {
                    return new ImportCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示import命令的帮助
                    sender.sendMessage(new ImportCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "query":
                if (args.length > 1) {
                    return new QueryCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示query命令的帮助
                    sender.sendMessage(new QueryCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "view":
                if (args.length > 1) {
                    return new ViewCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(new ViewCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "set":
                if (args.length > 1) {
                    return new SetCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    // 显示set命令的帮助
                    sender.sendMessage(new SetCommandExecutor(plugin).getUsage());
                    return true;
                }
            default:
                sender.sendMessage(prefix + langConfig.getMessage("unknown-command"));
                return true;
        }
    }

    private void sendHelpMessage(CommandSender sender, String prefix, LanguageConfig langConfig) {
        // 使用HelpCommandExecutor来显示帮助信息，保持一致性
        new HelpCommandExecutor(plugin).onCommand(sender, new String[0]);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList(
                "help", "create", "use", "add", "del", "delete", 
                "list", "reload", "export", "import", "query", "set"
            );
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "use":
                    if (args.length == 2) {
                        // 可以在这里添加已存在的CDK码作为补全建议
                        return new ArrayList<>(); // 暂时返回空列表
                    }
                    break;
                case "del":
                case "delete":
                    if (args.length == 2) {
                        completions.add("cdk");
                        completions.add("id");
                    }
                    break;
                case "import":
                case "export":
                    if (args.length == 2) {
                        // 可以在这里添加文件名补全
                        return new ArrayList<>(); // 暂时返回空列表
                    }
                    break;
            }
        }
        
        return completions;
    }
}