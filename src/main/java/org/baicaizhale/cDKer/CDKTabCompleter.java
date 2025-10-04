package org.baicaizhale.cDKer;

import org.baicaizhale.cDKer.manager.ConfigurationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CDK 命令的 Tab 补全器，用于自动补全子命令和参数。
 */
public class CDKTabCompleter implements TabCompleter {

    private final ConfigurationManager configManager;
    private final List<String> subCommands = Arrays.asList("help", "create", "add", "delete", "list", "reload", "export", "use");

    /**
     * 构造函数
     * @param configManager 配置管理器实例
     */
    public CDKTabCompleter(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * 处理 Tab 补全逻辑。
     * @param sender 命令发送者
     * @param command 命令对象
     * @param alias 命令别名
     * @param args 命令参数
     * @return 可能的补全选项列表
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 补全子命令
            completions.addAll(subCommands);
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "delete":
                case "add":
                case "use":
                    break;
                case "create":
                    // 补全 CDK 类型
                    completions.addAll(Arrays.asList("single", "multiple"));
                    break;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            if (args[1].equalsIgnoreCase("multiple")) {
                // 补全 <name|random>
                completions.addAll(Arrays.asList("name", "random"));
            } else {
                // 补全 CDK 码（创建时）
                completions.add("<CDK码>");
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            if (args[1].equalsIgnoreCase("multiple")) {
                if (args[2].equalsIgnoreCase("random")) {
                    // 补全数量
                    completions.add("<数量>");
                } else {
                    // 补全 CDK 码（创建时）
                    completions.add("<CDK码>");
                }
            } else {
                // 补全数量
                completions.add("<数量>");
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("create")) {
            if (args[1].equalsIgnoreCase("multiple")) {
                if (args[2].equalsIgnoreCase("random")) {
                    // 补全命令
                    completions.add("<命令1|命令2|...>");
                } else {
                    // 补全数量
                    completions.add("<数量>");
                }
            } else {
                // 补全命令
                completions.add("<命令1|命令2|...>");
            }
        } else if (args.length == 6 && args[0].equalsIgnoreCase("create")) {
            if (args[1].equalsIgnoreCase("multiple")) {
                if (args[2].equalsIgnoreCase("random")) {
                    // 补全过期时间
                    completions.add("<yyyy-MM-dd HH:mm>");
                } else {
                    // 补全命令
                    completions.add("<命令1|命令2|...>");
                }
            }
            else {
                // 补全过期时间
                completions.add("<yyyy-MM-dd HH:mm>");
            }
        } else if (args.length == 7 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("multiple") && !args[2].equalsIgnoreCase("random")) {
            // 补全过期时间
            completions.add("<yyyy-MM-dd HH:mm>");
        }

        // 过滤匹配当前输入的补全选项
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}