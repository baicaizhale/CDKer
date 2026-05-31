package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.bukkit.command.CommandSender;

public class HelpCommandExecutor extends AbstractSubCommand {

    public HelpCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sendHelpLine(sender, "command.help.header");
        sendHelpLine(sender, "command.help.create");
        sendHelpLines(sender, "command.help.create_params");
        sendHelpLine(sender, "command.help.use");
        sendHelpLine(sender, "command.help.use_example");
        sendHelpLine(sender, "command.help.add");
        sendHelpLine(sender, "command.help.add_example");
        sendHelpLine(sender, "command.help.del");
        sendHelpLine(sender, "command.help.del_example");
        sendHelpLine(sender, "command.help.list");
        sendHelpLine(sender, "command.help.list_example");
        sendHelpLine(sender, "command.help.log");
        sendHelpLine(sender, "command.help.log_example");
        sendHelpLine(sender, "command.help.log_hint");
        sendHelpLine(sender, "command.help.query");
        sendHelpLine(sender, "command.help.query_example");
        sendHelpLine(sender, "command.help.view");
        sendHelpLine(sender, "command.help.view_example");
        sendHelpLine(sender, "command.help.set");
        sendHelpLine(sender, "command.help.set_props");
        sendHelpLines(sender, "command.help.set_examples");
        sendHelpLine(sender, "command.help.reload");
        sendHelpLine(sender, "command.help.import");
        sendHelpLines(sender, "command.help.import_examples");
        sendHelpLine(sender, "command.help.export");
        sendHelpLine(sender, "command.help.export_example");
        sendHelpLine(sender, "command.help.footer");
        return true;
    }

    private void sendHelpLine(CommandSender sender, String key) {
        String msg = getMsg(key);
        if (msg != null && !msg.isEmpty()) {
            for (String line : msg.split("\n")) {
                sender.sendMessage(line);
            }
        }
    }

    private void sendHelpLines(CommandSender sender, String key) {
        sendHelpLine(sender, key);
    }

    @Override
    public String getUsage() {
        return getMsg("command.common.usage_invalid");
    }
}
