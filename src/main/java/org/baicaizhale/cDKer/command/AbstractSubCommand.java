package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSubCommand {
    protected final CDKer plugin;

    public AbstractSubCommand(CDKer plugin) {
        this.plugin = plugin;
    }

    /**
     * 执行子命令
     *
     * @param sender 命令发送者
     * @param args   命令参数
     * @return 是否执行成功
     */
    public boolean execute(CommandSender sender, String[] args) {
        return onCommand(sender, args);
    }

    /**
     * 处理命令逻辑
     *
     * @param sender 命令发送者
     * @param args   命令参数
     * @return 是否执行成功
     */
    public abstract boolean onCommand(CommandSender sender, String[] args);

    /**
     * 获取命令用法
     *
     * @return 命令用法字符串
     */
    public String getUsage() {
        return "§c用法不正确，请检查命令格式。";
    }
    
    /**
     * 检查命令发送者是否为玩家
     *
     * @param sender 命令发送者
     * @return 是否为玩家
     */
    protected boolean requirePlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * 将命令发送者转换为玩家对象
     *
     * @param sender 命令发送者
     * @return 玩家对象
     */
    protected Player asPlayer(CommandSender sender) {
        return (Player) sender;
    }
    
    /**
     * 命令自动补全
     *
     * @param sender 命令发送者
     * @param args   命令参数
     * @return 补全建议列表
     */
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}