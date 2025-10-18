package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteCommandExecutor extends AbstractSubCommand {

    public DeleteCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /cdk del <id/cdk> <标识符>");
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
                sender.sendMessage("§c无效的标识符，必须是 'id' 或 'cdk'。");
                return true;
            }

            if (record == null) {
                sender.sendMessage("§cCDK不存在。");
                return true;
            }

            if ("id".equals(identifierType)) {
                plugin.getCdkRecordDao().deleteCdkById(record.getId());
            } else {
                plugin.getCdkRecordDao().deleteCdk(record.getCdkCode());
            }

            sender.sendMessage("§a成功删除CDK码: " + record.getCdkCode());

        } catch (NumberFormatException e) {
            sender.sendMessage("§c无效的数字格式。");
        } catch (Exception e) {
            sender.sendMessage("§c删除CDK时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk del <id/cdk> <标识符>";
    }
}