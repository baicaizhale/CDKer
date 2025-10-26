package org.baicaizhale.CDKer.command;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommandExecutor implements CommandExecutor, TabCompleter {
    protected final CDKer plugin;

    public BaseCommandExecutor(CDKer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return execute(sender, args);
        } catch (Exception e) {
            plugin.getLogger().severe("执行命令时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c执行命令时发生错误: " + e.getMessage());
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        try {
            return tabComplete(sender, args);
        } catch (Exception e) {
            plugin.getLogger().severe("Tab补全时出错: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 执行命令
     * @param sender 命令发送者
     * @param args 命令参数
     * @return 是否执行成功
     */
    protected abstract boolean execute(CommandSender sender, String[] args) throws Exception;

    /**
     * 提供Tab补全
     * @param sender 命令发送者
     * @param args 当前参数
     * @return 补全列表
     */
    protected abstract List<String> tabComplete(CommandSender sender, String[] args) throws Exception;
}