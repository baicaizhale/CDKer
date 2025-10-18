package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListCDKCommand extends AbstractSubCommand {
    private static final int ITEMS_PER_PAGE = 10;

    public ListCDKCommand(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§c你没有使用此命令的权限。");
            return true;
        }

        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) {
                    sender.sendMessage("§c页码必须大于0。");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§c无效的页码。");
                return true;
            }
        }

        // TODO: 获取总CDK数量
        int totalCDKs = 0; // 从配置或数据库获取
        int totalPages = (totalCDKs + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;

        if (page > totalPages && totalPages > 0) {
            sender.sendMessage("§c页码超出范围。共有 " + totalPages + " 页。");
            return true;
        }

        sender.sendMessage("§6CDK列表 §7(第 " + page + "/" + Math.max(1, totalPages) + " 页)");
        
        // TODO: 获取当前页的CDK列表
        // TODO: 显示CDK信息

        if (totalPages > 1) {
            sender.sendMessage("§7使用 /cdk list <页码> 查看其他页");
        }
        
        return true;
    }

    @Override
    public String getUsage() {
        return "§f/cdk list [页码] §7- 查看CDK列表";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.admin";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}