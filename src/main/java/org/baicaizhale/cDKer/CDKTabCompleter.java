package org.baicaizhale.cDKer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CDKTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // 只处理 /mcdk 命令
        if ("mcdk".equalsIgnoreCase(label)) {
            // 如果用户输入了 "mcdk"，补全 "list" 和 "reload" 子命令
            if (args.length == 1) {
                suggestions.add("list");
                suggestions.add("reload");
            }
        }

        return suggestions;
    }
}
