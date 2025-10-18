package org.baicaizhale.cDKer.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommandExecutor {
    /**
     * 执行子命令
     * @param sender 命令发送者
     * @param args 命令参数
     * @return 是否执行成功
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * 提供Tab补全
     * @param sender 命令发送者
     * @param args 当前参数
     * @return 补全列表
     */
    List<String> tabComplete(CommandSender sender, String[] args);

    /**
     * 获取命令用法说明
     * @return 用法说明
     */
    String getUsage();

    /**
     * 获取所需权限
     * @return 权限节点
     */
    String getRequiredPermission();
}