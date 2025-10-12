package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetCommandExecutor extends AbstractSubCommand {

    public SetCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 4) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        try {
            String identifier = args[0];
            String target = args[1];
            String type = args[2];
            String value = args[3];

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

            switch (type.toLowerCase()) {
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
                default:
                    CommandUtils.sendMessage(sender, "§c无效的属性类型。");
                    return true;
            }

            plugin.getCdkRecordDao().updateCdk(record);
            CommandUtils.sendMessage(sender, "§a成功更新CDK码: §f" + record.getCdkCode());
            return true;
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("设置CDK属性时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c设置CDK属性时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk set <id/cdk> <标识符> <类型> <值> §7- 设置CDK属性";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.admin";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("id", "cdk");
        }
        if (args.length == 3) {
            return Arrays.asList("remaining_uses", "commands", "expire_time", "note", "cdk_type");
        }
        if (args.length == 4) {
            String type = args[2].toLowerCase();
            switch (type) {
                case "remaining_uses":
                    return Arrays.asList("1", "5", "10", "-1");
                case "expire_time":
                    return Arrays.asList("forever", "2025-12-31 23:59");
                case "cdk_type":
                    return Arrays.asList("newbie", "vip", "event", "daily");
            }
        }
        return new ArrayList<>();
    }
}