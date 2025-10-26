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
        if (args.length < 4) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];
        String property = args[2];
        // 重新组合参数，从第4个参数开始，处理可能包含空格的值
        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

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
                    CommandUtils.sendMessage(sender, "§c无效的属性类型。");
                    return true;
            }

            plugin.getCdkRecordDao().updateCdk(record);
            CommandUtils.sendMessage(sender, "§a成功更新CDK码: §f" + record.getCdkCode());

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
        } catch (Exception e) {
            CommandUtils.sendMessage(sender, "§c设置CDK属性时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk set <id/cdk> <标识符> <属性> <值>";
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