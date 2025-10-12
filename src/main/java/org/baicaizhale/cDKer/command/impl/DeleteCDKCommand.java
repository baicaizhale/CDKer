package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DeleteCDKCommand extends AbstractSubCommand {

    public DeleteCDKCommand(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§c你没有使用此命令的权限。");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getUsage());
            return true;
        }

        String code = args[0];

        // TODO: 检查CDK是否存在
        // TODO: 删除CDK
        sender.sendMessage("§a成功删除CDK：" + code);
        
        return true;
    }

    @Override
    public String getUsage() {
        return "§f/cdk delete <兑换码> §7- 删除一个CDK";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.admin";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // TODO: 获取所有CDK列表用于补全
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}