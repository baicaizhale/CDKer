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
    private final HelpCommandExecutor helpExecutor;
    private final CreateCommandExecutor createExecutor;
    private final UseCommandExecutor useExecutor;
    private final AddCommandExecutor addExecutor;
    private final DeleteCommandExecutor deleteExecutor;
    private final ListCommandExecutor listExecutor;
    private final LogCommandExecutor logExecutor;
    private final ReloadCommandExecutor reloadExecutor;
    private final ExportCommandExecutor exportExecutor;
    private final ImportCommandExecutor importExecutor;
    private final QueryCommandExecutor queryExecutor;
    private final ViewCommandExecutor viewExecutor;
    private final SetCommandExecutor setExecutor;

    public MainCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
        this.helpExecutor = new HelpCommandExecutor(plugin);
        this.createExecutor = new CreateCommandExecutor(plugin);
        this.useExecutor = new UseCommandExecutor(plugin);
        this.addExecutor = new AddCommandExecutor(plugin);
        this.deleteExecutor = new DeleteCommandExecutor(plugin);
        this.listExecutor = new ListCommandExecutor(plugin);
        this.logExecutor = new LogCommandExecutor(plugin);
        this.reloadExecutor = new ReloadCommandExecutor(plugin);
        this.exportExecutor = new ExportCommandExecutor(plugin);
        this.importExecutor = new ImportCommandExecutor(plugin);
        this.queryExecutor = new QueryCommandExecutor(plugin);
        this.viewExecutor = new ViewCommandExecutor(plugin);
        this.setExecutor = new SetCommandExecutor(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            helpExecutor.onCommand(sender, new String[0]);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                return helpExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "create":
                if (args.length > 1) {
                    return createExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(createExecutor.getUsage());
                    return true;
                }
            case "use":
                if (args.length > 1) {
                    return useExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(useExecutor.getUsage());
                    return true;
                }
            case "add":
                if (args.length > 1) {
                    return addExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(addExecutor.getUsage());
                    return true;
                }
            case "del":
            case "delete":
                if (args.length > 1) {
                    return deleteExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(deleteExecutor.getUsage());
                    return true;
                }
            case "list":
                return listExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "log":
                return logExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "reload":
                return reloadExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "export":
                return exportExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            case "import":
                if (args.length > 1) {
                    return importExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(importExecutor.getUsage());
                    return true;
                }
            case "query":
                if (args.length > 1) {
                    return queryExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(queryExecutor.getUsage());
                    return true;
                }
            case "view":
                if (args.length > 1) {
                    return viewExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(viewExecutor.getUsage());
                    return true;
                }
            case "set":
                if (args.length > 1) {
                    return setExecutor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage(setExecutor.getUsage());
                    return true;
                }
            default:
                sender.sendMessage(helpExecutor.getMsg("command.common.unknown_command"));
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
                case "create": return createExecutor.tabComplete(sender, subArgs);
                case "use":    return useExecutor.tabComplete(sender, subArgs);
                case "add":    return addExecutor.tabComplete(sender, subArgs);
                case "del":
                case "delete": return deleteExecutor.tabComplete(sender, subArgs);
                case "list":   return listExecutor.tabComplete(sender, subArgs);
                case "log":    return logExecutor.tabComplete(sender, subArgs);
                case "query":  return queryExecutor.tabComplete(sender, subArgs);
                case "set":    return setExecutor.tabComplete(sender, subArgs);
                case "import": return importExecutor.tabComplete(sender, subArgs);
                default:       return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }
}
