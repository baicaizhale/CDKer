package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UseCDKCommand extends AbstractSubCommand {

    public UseCDKCommand(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }

        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§c你没有使用此命令的权限。");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getUsage());
            return true;
        }

        String code = args[0];
        Player player = (Player) sender;

        // TODO: 检查CDK是否存在
        // TODO: 检查CDK是否可用
        // TODO: 检查玩家是否已使用过此CDK
        // TODO: 使用CDK
        // TODO: 记录使用记录
        player.sendMessage("§a成功使用CDK：" + code);
        
        return true;
    }

    @Override
    public String getUsage() {
        return "§f/cdk use <兑换码> §7- 使用一个CDK";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.use";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}