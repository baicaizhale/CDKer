package org.baicaizhale.cDKer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;
import java.util.Set;  // 导入 Set

public class CDKListCommandExecutor implements CommandExecutor {

    private final CDKer plugin;

    public CDKListCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 如果没有提供子命令或输入不正确，则返回 false
        if (args.length == 0) {
            return false;  // 触发 usage
        }

        // 处理 list 子命令
        if ("list".equalsIgnoreCase(args[0])) {
            return showCDKList(sender);
        }

        // 处理 reload 子命令
        if ("reload".equalsIgnoreCase(args[0])) {
            return reloadConfig(sender);
        }

        return false;  // 如果命令不符合要求，则返回 false
    }

    // 显示兑换码列表
    private boolean showCDKList(CommandSender sender) {
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

    // 重载配置文件
    private boolean reloadConfig(CommandSender sender) {
        // 确保玩家有权限
        if (!(sender instanceof Player) || !((Player) sender).hasPermission("cdk.reload")) {
            sender.sendMessage(ChatColor.RED + "您没有权限执行此命令！");
            return true;
        }

        // 重新加载 config.yml
        plugin.reloadConfig();  // 重新加载 config.yml
        plugin.saveConfig();    // 保存配置文件

        // 重新加载 cdk.yml 配置文件
        File cdkFile = new File(plugin.getDataFolder(), "cdk.yml");
        if (cdkFile.exists()) {
            plugin.getLogger().info("正在重载 cdk.yml 文件...");
            FileConfiguration cdkConfig = YamlConfiguration.loadConfiguration(cdkFile);
            plugin.setCDKConfig(cdkConfig);  // 更新内存中的配置

            sender.sendMessage(ChatColor.GREEN + "配置文件已成功重载！");
        } else {
            sender.sendMessage(ChatColor.RED + "未找到 cdk.yml 文件！");
        }

        return true;
    }
}
