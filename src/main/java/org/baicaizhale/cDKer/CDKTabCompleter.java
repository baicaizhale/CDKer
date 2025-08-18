package org.baicaizhale.cDKer;

// 导入 Bukkit 命令相关接口
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
// 导入 Bukkit 配置文件处理类
import org.bukkit.configuration.file.FileConfiguration;
// 导入 Bukkit 玩家类
import org.bukkit.entity.Player;

// 导入 Java 集合与流工具类
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// CDK 命令补全器类，实现 TabCompleter 接口
public class CDKTabCompleter implements TabCompleter {

    // 插件主类实例
    private final CDKer plugin;

    // 构造方法，注入插件主类
    public CDKTabCompleter(CDKer plugin) {
        this.plugin = plugin;
    }

    // 命令补全主入口
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        FileConfiguration cdkConfig = plugin.getCDKConfig();
        // 仅对 cdk 命令进行补全
        if ("cdk".equalsIgnoreCase(label)) {
            if (args.length == 1) {
                // 一级命令补全
                suggestions.addAll(getPrimarySuggestions(sender, args[0]));
                return filterSuggestions(suggestions, args[0]);
            } else if (args.length > 1) {
                // 二级及以上命令补全
                suggestions.addAll(getSubCommandSuggestions(sender, cdkConfig, args));
                return filterSuggestions(suggestions, args[args.length - 1]);
            }
        }
        return suggestions;
    }

    /**
     * 获取一级命令补全建议
     */
    private List<String> getPrimarySuggestions(CommandSender sender, String input) {
        List<String> list = new ArrayList<>();
        if (sender.hasPermission("cdk.help")) list.add("help");
        if (sender.hasPermission("cdk.create")) list.add("create");
        if (sender.hasPermission("cdk.add")) list.add("add");
        if (sender.hasPermission("cdk.delete")) list.add("delete");
        if (sender.hasPermission("cdk.list")) list.add("list");
        if (sender.hasPermission("cdk.reload")) list.add("reload");
        if (sender.hasPermission("cdk.export")) list.add("export");
        return list;
    }

    /**
     * 获取二级及以上命令补全建议
     */
    private List<String> getSubCommandSuggestions(CommandSender sender, FileConfiguration cdkConfig, String[] args) {
        List<String> list = new ArrayList<>();
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length == 2) {
                    list.add("single");
                    list.add("multiple");
                } else if (args.length == 3 && args[1].equalsIgnoreCase("multiple")) {
                    list.add("random");
                }
                break;
            case "add":
                if (args.length == 2 && sender.hasPermission("cdk.add")) {
                    Set<String> cdkCodes = cdkConfig.getKeys(false);
                    list.addAll(cdkCodes);
                }
                break;
            case "use":
                // use 命令不补全
                break;
            case "delete":
                if (args.length == 2 && sender.hasPermission("cdk.delete")) {
                    list.add("cdk");
                    list.add("id");
                } else if (args.length == 3 && args[1].equalsIgnoreCase("cdk") && sender.hasPermission("cdk.delete")) {
                    Set<String> cdkCodes = cdkConfig.getKeys(false);
                    list.addAll(cdkCodes);
                }
                break;
        }
        return list;
    }

    /**
     * 过滤建议词，仅显示以当前输入开头的建议
     */
    private List<String> filterSuggestions(List<String> suggestions, String input) {
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}