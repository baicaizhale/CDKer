package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandExecutor extends AbstractSubCommand {

    public HelpCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        CommandUtils.sendMessage(sender, "§6CDKer §f插件帮助：");
        CommandUtils.sendMessage(sender, "§f/cdk help §7- 显示此帮助");
        CommandUtils.sendMessage(sender, "§f/cdk create [兑换码] [类型] §7- 创建兑换码");
        CommandUtils.sendMessage(sender, "§f/cdk use [兑换码] §7- 使用兑换码");
        CommandUtils.sendMessage(sender, "§f/cdk list [页码] §7- 查看兑换码列表");
        CommandUtils.sendMessage(sender, "§f/cdk delete [兑换码] §7- 删除兑换码");
        CommandUtils.sendMessage(sender, "§f/cdk reload §7- 重载配置");
        return true;
    }

    @Override
    public String getUsage() {
        return "§f/cdk help §7- 显示帮助信息";
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