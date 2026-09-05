package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryCommandExecutor extends AbstractSubCommand {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public QueryCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!CommandUtils.hasPermission(sender, "cdk.query")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        if (args.length < 2) {
            CommandUtils.sendMessage(sender, getMsg("command.query.usage"));
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
                CommandUtils.sendMessage(sender, getMsg("command.common.invalid_identifier"));
                return true;
            }

            if (record == null) {
                CommandUtils.sendMessage(sender, getMsg("command.common.not_found"));
                return true;
            }

            StringBuilder info = new StringBuilder();
            info.append(getMsg("command.query.header")).append("\n");
            info.append(String.format("§fID: §e%d\n", record.getId()));
            info.append(String.format("§f代码: §e%s\n", record.getCdkCode()));
            info.append(String.format("§f类型: §e%s\n", record.getCdkType().isEmpty() ? "无" : record.getCdkType()));
            info.append(String.format("§f备注: §e%s\n", record.getNote().isEmpty() ? "无" : record.getNote()));
            info.append(String.format("§f剩余使用次数: §e%d\n", record.getRemainingUses()));
            info.append(String.format("§f过期时间: §e%s\n", record.getExpireTime()));
            info.append(String.format("§f创建时间: §e%s\n", DATE_FORMAT.format(record.getCreatedTime())));
            info.append(String.format("§f允许同一玩家多次使用: §e%s\n", record.isPerPlayerMultiple() ? "是" : "否"));
            info.append("§f命令列表:\n");

            List<String> commands = record.getCommands();
            for (int i = 0; i < commands.size(); i++) {
                info.append(String.format("  §e%d. §f%s\n", i + 1, commands.get(i)));
            }
            info.append(getMsg("command.query.footer"));

            CommandUtils.sendMessage(sender, info.toString());

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, getMsg("command.common.invalid_number"));
        } catch (Exception e) {
            plugin.getLogger().severe("查询CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.query.usage");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("id", "cdk");
        }
        return new ArrayList<>();
    }
}