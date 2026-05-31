package org.baicaizhale.CDKer.command;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.impl.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommandExecutor implements CommandExecutor, TabCompleter {

    private final CDKer plugin;

    public MainCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            new HelpCommandExecutor(plugin).onCommand(sender, new String[0]);
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
                    sender.sendMessage(new CreateCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "use":
                if (args.length > 1) {
                    return new UseCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(new UseCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "add":
                if (args.length > 1) {
                    return new AddCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(new AddCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "del":
            case "delete":
                if (args.length > 1) {
                    return new DeleteCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(new DeleteCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "list":
                return new ListCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "log":
                return new LogCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "reload":
                return new ReloadCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "export":
                return new ExportCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "import":
                if (args.length > 1) {
                    return new ImportCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(new ImportCommandExecutor(plugin).getUsage());
                    return true;
                }
            case "query":
                if (args.length > 1) {
                    return new QueryCommandExecutor(plugin).execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
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
                    sender.sendMessage(new SetCommandExecutor(plugin).getUsage());
                    return true;
                }
            default:
                HelpCommandExecutor helper = new HelpCommandExecutor(plugin);
                sender.sendMessage(helper.getMsg("command.common.unknown_command"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList(
                "help", "create", "use", "add", "del", "delete",
                "list", "log", "reload", "export", "import", "query", "set", "view"
            );
            List<String> completions = new ArrayList<>();
            for (String sub : subCommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        if (args.length >= 2) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            switch (args[0].toLowerCase()) {
                case "create": return new CreateCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "use":    return new UseCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "add":    return new AddCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "del":
                case "delete": return new DeleteCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "list":   return new ListCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "log":    return new LogCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "query":  return new QueryCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "set":    return new SetCommandExecutor(plugin).tabComplete(sender, subArgs);
                case "import": return new ImportCommandExecutor(plugin).tabComplete(sender, subArgs);
                default:       return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }
}
