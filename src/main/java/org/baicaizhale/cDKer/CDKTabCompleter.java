package org.baicaizhale.cDKer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CDKTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // 如果玩家没有输入任何内容，则补全 /mcdk list
        if (args.length == 1) {
            if ("mcdk".equalsIgnoreCase(label)) {
                suggestions.add("list");  // 只补全 list 子命令
            }
        }

        return suggestions;
    }
}
