package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCommandExecutor extends AbstractSubCommand {

    public AddCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            CommandUtils.sendMessage(sender, getMsg("command.add.usage"));
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                CommandUtils.sendMessage(sender, getMsg("command.add.amount_positive"));
                return true;
            }
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, getMsg("command.common.invalid_number"));
            return true;
        }

        try {
            CdkRecord record;
            if ("id".equals(identifierType)) {
                int id = Integer.parseInt(identifier);
                record = plugin.getCdkRecordDao().getCdkById(id);
            } else if ("cdk".equals(identifierType)) {
                record = plugin.getCdkRecordDao().getCdkByCode(identifier);
            } else {
                CommandUtils.sendMessage(sender, getMsg("command.common.invalid_identifier"));
                return true;
            }

            if (record == null) {
                CommandUtils.sendMessage(sender, getMsg("command.common.not_found"));
                return true;
            }

            record.setRemainingUses(record.getRemainingUses() + amount);
            plugin.getCdkRecordDao().updateCdk(record);

            CommandUtils.sendMessage(sender, getMsg("command.add.success",
                    record.getCdkCode(), String.valueOf(amount), String.valueOf(record.getRemainingUses())));

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, getMsg("command.common.invalid_number"));
        } catch (Exception e) {
            CommandUtils.sendMessage(sender, getMsg("command.add.error", e.getMessage()));
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.add.usage");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("id", "cdk");
        }
        if (args.length == 3) {
            return Arrays.asList("1", "5", "10");
        }
        return new ArrayList<>();
    }
}