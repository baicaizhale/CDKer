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