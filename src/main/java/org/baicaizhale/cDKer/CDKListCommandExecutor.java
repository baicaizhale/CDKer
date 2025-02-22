package org.baicaizhale.cDKer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;

public class CDKListCommandExecutor implements CommandExecutor {

    private final CDKer plugin;

    public CDKListCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 如果没有提供子命令或输入不正确，则返回 false
        if (args.length != 1 || !args[0].equalsIgnoreCase("list")) {
            return false;  // 触发 usage
        }

        // 确保只有 OP 权限的玩家可以执行此命令
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage(ChatColor.RED + "您没有权限执行此命令！");
            return true;
        }

        // 获取 cdk.yml 配置文件
        FileConfiguration cdkConfig = plugin.getCDKConfig();

        // 获取所有兑换码的列表
        Set<String> cdkCodes = cdkConfig.getKeys(false);

        // 检查是否有兑换码
        if (cdkCodes.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "当前没有任何兑换码！");
            return true;
        }

        // 列出所有兑换码及其剩余次数
        sender.sendMessage(ChatColor.GREEN + "有效的兑换码列表:");
        for (String cdkCode : cdkCodes) {
            int remainingUses = cdkConfig.getInt(cdkCode + ".remainingUses", 0);
            String status = remainingUses > 0 ? ChatColor.GREEN + "可用" : ChatColor.RED + "已用完";
            sender.sendMessage(ChatColor.YELLOW + "兑换码: " + cdkCode + " | 剩余次数: " + remainingUses + " | 状态: " + status);
        }

        return true;
    }
}
