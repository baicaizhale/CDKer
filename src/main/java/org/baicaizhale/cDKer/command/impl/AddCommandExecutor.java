package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
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
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                CommandUtils.sendMessage(sender, "§c增加的数量必须大于0。");
                return true;
            }
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
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
                CommandUtils.sendMessage(sender, "§c无效的标识符，必须是 'id' 或 'cdk'。");
                return true;
            }

            if (record == null) {
                CommandUtils.sendMessage(sender, "§cCDK不存在。");
                return true;
            }

            record.setRemainingUses(record.getRemainingUses() + amount);
            plugin.getCdkRecordDao().updateCdk(record);

            CommandUtils.sendMessage(sender, String.format("§a已为CDK码 %s 增加 %d 次使用次数。当前剩余: %d",
                    record.getCdkCode(), amount, record.getRemainingUses()));

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
        } catch (Exception e) {
            CommandUtils.sendMessage(sender, "§c增加CDK使用次数时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk add <id/cdk> <标识符> <数量>";
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