package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetCommandExecutor extends AbstractSubCommand {

    public SetCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!CommandUtils.hasPermission(sender, "cdk.admin")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        if (args.length < 4) {
            CommandUtils.sendMessage(sender, getMsg("command.set.usage"));
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];
        String property = args[2];
        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

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

            switch (property.toLowerCase()) {
                case "remaining_uses":
                    record.setRemainingUses(Integer.parseInt(value));
                    break;
                case "commands":
                    record.setCommands(CommandUtils.parseCommands(value));
                    break;
                case "expire_time":
                    record.setExpireTime(value);
                    break;
                case "note":
                    record.setNote(value);
                    break;
                case "cdk_type":
                    record.setCdkType(value);
                    break;
                case "per_player_multiple":
                    record.setPerPlayerMultiple(Boolean.parseBoolean(value));
                    break;
                default:
                    CommandUtils.sendMessage(sender, getMsg("command.set.invalid_property"));
                    return true;
            }

            plugin.getCdkRecordDao().updateCdk(record);
            CommandUtils.sendMessage(sender, getMsg("command.set.success", record.getCdkCode()));

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, getMsg("command.common.invalid_number"));
        } catch (Exception e) {
            CommandUtils.sendMessage(sender, getMsg("command.set.error", e.getMessage()));
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.set.usage");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("id", "cdk");
        }
        if (args.length == 2) {
            // 根据第一个参数提供不同的补全
            String identifierType = args[0].toLowerCase();
            if ("id".equals(identifierType)) {
                // 返回一些示例ID
                return Arrays.asList("1", "2", "3");
            } else if ("cdk".equals(identifierType)) {
                // 返回一些示例CDK码
                return Arrays.asList("ABC123", "XYZ789");
            }
            return new ArrayList<>();
        }
        if (args.length == 3) {
            return Arrays.asList("remaining_uses", "commands", "expire_time", "note", "cdk_type", "per_player_multiple");
        }
        if (args.length == 4) {
            String property = args[2].toLowerCase();
            switch (property) {
                case "remaining_uses":
                    return Arrays.asList("1", "5", "10", "-1");
                case "expire_time":
                    return Arrays.asList("forever", "2025-12-31 23:59");
                case "cdk_type":
                    return Arrays.asList("newbie", "vip", "event", "daily");
                case "per_player_multiple":
                    return Arrays.asList("true", "false");
                default:
                    return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
}