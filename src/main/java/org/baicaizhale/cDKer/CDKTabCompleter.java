package org.baicaizhale.cDKer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CDKTabCompleter implements TabCompleter {

    private final CDKer plugin;

    public CDKTabCompleter(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        FileConfiguration cdkConfig = plugin.getCDKConfig();

        if ("cdk".equalsIgnoreCase(label)) {
            if (args.length == 1) {
                if (sender.hasPermission("cdk.help")) suggestions.add("help");
                if (sender.hasPermission("cdk.create")) suggestions.add("create");
                if (sender.hasPermission("cdk.add")) suggestions.add("add");
                if (sender.hasPermission("cdk.delete")) suggestions.add("delete");
                if (sender.hasPermission("cdk.list")) suggestions.add("list");
                if (sender.hasPermission("cdk.reload")) suggestions.add("reload");
                if (sender.hasPermission("cdk.export")) suggestions.add("export");

                return suggestions.stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());

            } else if (args.length > 1) {
                switch (args[0].toLowerCase()) {
                    case "create":
                        if (args.length == 2) {
                            suggestions.add("single");
                            suggestions.add("multiple");
                        } else if (args.length == 3) {
                            if (args[1].equalsIgnoreCase("multiple")) {
                                suggestions.add("random");
                            }
                        }
                        break;
                    case "add":
                        if (args.length == 2 && sender.hasPermission("cdk.add")) {
                            Set<String> cdkCodes = cdkConfig.getKeys(false);
                            suggestions.addAll(cdkCodes);
                        }
                        break;
                    case "use":
                        return new ArrayList<>();
                    case "delete":
                        if (args.length == 2 && sender.hasPermission("cdk.delete")) {
                            suggestions.add("cdk");
                            suggestions.add("id");
                        } else if (args.length == 3 && args[1].equalsIgnoreCase("cdk") && sender.hasPermission("cdk.delete")) {
                            Set<String> cdkCodes = cdkConfig.getKeys(false);
                            suggestions.addAll(cdkCodes);
                        }
                        break;
                }

                return suggestions.stream()
                        .filter(s -> s.startsWith(args[args.length - 1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return suggestions;
    }
}