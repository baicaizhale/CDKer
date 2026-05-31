package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.bukkit.command.CommandSender;

public class DeleteCommandExecutor extends AbstractSubCommand {

    public DeleteCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getMsg("command.del.usage"));
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];

        try {
            CdkRecord record;
            if ("id".equals(identifierType)) {
                int id = Integer.parseInt(identifier);
                record = plugin.getCdkRecordDao().getCdkById(id);
            } else if ("cdk".equals(identifierType)) {
                record = plugin.getCdkRecordDao().getCdkByCode(identifier);
            } else {
                sender.sendMessage(getMsg("command.common.invalid_identifier"));
                return true;
            }

            if (record == null) {
                sender.sendMessage(getMsg("command.common.not_found"));
                return true;
            }

            if ("id".equals(identifierType)) {
                plugin.getCdkRecordDao().deleteCdkById(record.getId());
            } else {
                plugin.getCdkRecordDao().deleteCdk(record.getCdkCode());
            }

            sender.sendMessage(getMsg("command.del.success", record.getCdkCode()));

        } catch (NumberFormatException e) {
            sender.sendMessage(getMsg("command.common.invalid_number"));
        } catch (Exception e) {
            sender.sendMessage(getMsg("command.del.error", e.getMessage()));
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.del.usage");
    }
}