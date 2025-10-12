package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommandExecutor extends AbstractSubCommand {

    public ReloadCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        try {
            plugin.reloadConfig();
            plugin.getConfigurationManager().reloadAllConfigs();
            CommandUtils.sendMessage(sender, "§a配置已重新加载。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("重载配置时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c重载配置时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk reload §7- 重载配置";
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