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

        // 只处理 /cdk 命令
        if ("cdk".equalsIgnoreCase(label)) {
            if (args.length == 1) {
                // 第一个参数是子命令
                if (sender.hasPermission("cdk.help")) suggestions.add("help");
                if (sender.hasPermission("cdk.create")) suggestions.add("create");
                if (sender.hasPermission("cdk.add")) suggestions.add("add");
                if (sender.hasPermission("cdk.delete")) suggestions.add("delete");
                if (sender.hasPermission("cdk.list")) suggestions.add("list");
                if (sender.hasPermission("cdk.reload")) suggestions.add("reload");
                if (sender.hasPermission("cdk.export")) suggestions.add("export");
                if (sender.hasPermission("cdk.use")) suggestions.add("use");

                // 根据用户输入过滤
                return suggestions.stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());

            } else if (args.length > 1) {
                // 处理子命令的后续参数
                switch (args[0].toLowerCase()) {
                    case "create":
                        if (args.length == 2) { // create <single|multiple>
                            suggestions.add("single");
                            suggestions.add("multiple");
                        } else if (args.length == 3) { // create <type> <id|name|random>
                            if (args[1].equalsIgnoreCase("multiple")) {
                                suggestions.add("random"); // 建议随机生成CDK码
                                // 如果有其他命名规则，可以在这里添加建议的名称
                                // suggestions.add("mycdkname");
                            }
                            // 对于 single 类型，这里通常是用户自定义的ID，所以不提供太多建议
                        }
                        // 对于命令字符串和日期，Tab补全通常不适用，或者需要更复杂的逻辑
                        break;
                    case "add":
                    case "use":
                        if (args.length == 2) { // add/use <cdkCode>
                            // 建议所有现有的CDK码
                            Set<String> cdkCodes = cdkConfig.getKeys(false);
                            suggestions.addAll(cdkCodes);
                        }
                        break;
                    case "delete":
                        if (args.length == 2) { // delete <cdk|id>
                            suggestions.add("cdk");
                            suggestions.add("id");
                        } else if (args.length == 3) { // delete <type> <content>
                            if (args[1].equalsIgnoreCase("cdk")) {
                                // 建议所有现有的CDK码
                                Set<String> cdkCodes = cdkConfig.getKeys(false);
                                suggestions.addAll(cdkCodes);
                            } else if (args[1].equalsIgnoreCase("id")) {
                                // 如果有ID的概念（例如CDK前缀），可以在这里建议已存在的ID
                                // 暂时不提供，因为ID的定义在代码中是前缀匹配
                            }
                        }
                        break;
                    // list, reload, export 命令没有后续参数，无需补全
                }

                // 根据用户输入过滤当前参数的建议
                return suggestions.stream()
                        .filter(s -> s.startsWith(args[args.length - 1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return suggestions;
    }
}
