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
            // 这里逐行拼接多条消息，统一使用不带前缀的原始消息，最后再给整段内容加一次前缀
            info.append(getRawMsg("command.query.header")).append("\n");
            info.append(getRawMsg("command.query.line_id", String.valueOf(record.getId()))).append("\n");
            info.append(getRawMsg("command.query.line_code", String.valueOf(record.getCdkCode()))).append("\n");
            info.append(getRawMsg("command.query.line_type", orNone(record.getCdkType()))).append("\n");
            info.append(getRawMsg("command.query.line_note", orNone(record.getNote()))).append("\n");
            info.append(getRawMsg("command.query.line_uses", record.getRemainingUses() == -1
                    ? getRawMsg("command.common.unlimited")
                    : String.valueOf(record.getRemainingUses()))).append("\n");
            info.append(getRawMsg("command.query.line_expire", orNone(record.getExpireTime()))).append("\n");
            info.append(getRawMsg("command.query.line_created",
                    record.getCreatedTime() == null ? getRawMsg("command.common.unknown")
                            : DATE_FORMAT.format(record.getCreatedTime()))).append("\n");
            info.append(getRawMsg("command.query.line_per_player_multiple",
                    record.isPerPlayerMultiple() ? getRawMsg("command.common.yes") : getRawMsg("command.common.no"))).append("\n");
            info.append(getRawMsg("command.common.command_list")).append("\n");

            List<String> commands = record.getCommands();
            for (int i = 0; i < commands.size(); i++) {
                info.append(getRawMsg("command.common.command_item", String.valueOf(i + 1), commands.get(i))).append("\n");
            }
            info.append(getRawMsg("command.query.footer"));

            CommandUtils.sendMessage(sender, withPrefix(info.toString()));

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, getMsg("command.common.invalid_number"));
        } catch (Exception e) {
            plugin.getLogger().severe("查询CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
        }

        return true;
    }

    /**
     * 空值/未设置时显示语言文件中的"无"
     */
    private String orNone(String value) {
        return value == null || value.isEmpty() ? getRawMsg("command.common.none") : value;
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