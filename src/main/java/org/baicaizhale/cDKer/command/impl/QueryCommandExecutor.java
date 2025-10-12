package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryCommandExecutor extends AbstractSubCommand {

    public QueryCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        try {
            String identifier = args[0];
            String target = args[1];

            CdkRecord record = null;
            if ("id".equalsIgnoreCase(identifier)) {
                record = plugin.getCdkRecordDao().getCdkById(Integer.parseInt(target));
            } else if ("cdk".equalsIgnoreCase(identifier)) {
                record = plugin.getCdkRecordDao().getCdkByCode(target);
            } else {
                CommandUtils.sendMessage(sender, "§c无效的标识符，必须是 'id' 或 'cdk'。");
                return true;
            }

            if (record == null) {
                CommandUtils.sendMessage(sender, "§cCDK不存在。");
                return true;
            }

            CommandUtils.sendMessage(sender, CommandUtils.formatCdkInfo(record));
            return true;
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("查询CDK信息时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c查询CDK信息时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk query <id/cdk> <标识符> §7- 查询CDK信息";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.query";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("id", "cdk");
        }
        return new ArrayList<>();
    }
}