package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

public class ReloadCommandExecutor extends AbstractSubCommand {

    public ReloadCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            plugin.getConfigurationManager().reloadAllConfigs();
            CommandUtils.sendMessage(sender, "§a配置已重新加载。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("重新加载配置时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c重新加载配置时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk reload";
    }
}