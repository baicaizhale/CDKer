package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CreateCDKCommand extends AbstractSubCommand {

    public CreateCDKCommand(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§c你没有使用此命令的权限。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(getUsage());
            return true;
        }

        String code = args[0];
        String type = args[1];

        // TODO: 检查CDK是否已存在
        // TODO: 创建CDK
        // TODO: 保存CDK
        sender.sendMessage("§a成功创建CDK：" + code + " (类型: " + type + ")");
        
        return true;
    }

    @Override
    public String getUsage() {
        return "§f/cdk create <兑换码> <类型> §7- 创建一个新的CDK";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.admin";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            // TODO: 添加可用的CDK类型补全
            completions.add("vip");
            completions.add("money");
            completions.add("item");
        }
        return completions;
    }
}