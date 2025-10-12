package org.baicaizhale.cDKer.command;

import org.baicaizhale.cDKer.CDKer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSubCommand implements SubCommandExecutor {
    protected final CDKer plugin;

    public AbstractSubCommand(CDKer plugin) {
        this.plugin = plugin;
    }

    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected Player asPlayer(CommandSender sender) {
        return (Player) sender;
    }

    protected boolean requirePlayer(CommandSender sender) {
        if (!isPlayer(sender)) {
            sender.sendMessage("§c此命令只能由玩家执行。");
            return false;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}