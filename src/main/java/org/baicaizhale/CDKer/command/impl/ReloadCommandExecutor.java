package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

public class ReloadCommandExecutor extends AbstractSubCommand {

    public ReloadCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!CommandUtils.hasPermission(sender, "cdk.admin")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        try {
            plugin.getConfigurationManager().reloadAllConfigs();
            CommandUtils.sendMessage(sender, getMsg("command.reload.success"));
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("重新加载配置时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
            return true;
        }
    }

    @Override
    public String getUsage() {
        return getMsg("command.reload.usage");
    }
}