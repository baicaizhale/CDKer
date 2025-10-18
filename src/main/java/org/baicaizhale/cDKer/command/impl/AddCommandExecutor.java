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
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        try {
            String identifier = args[0];
            String target = args[1];
            int amount = Integer.parseInt(args[2]);

            if (amount <= 0) {
                CommandUtils.sendMessage(sender, "§c增加的数量必须大于0。");
                return true;
            }

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

            record.setRemainingUses(record.getRemainingUses() + amount);
            plugin.getCdkRecordDao().updateCdk(record);
            
            CommandUtils.sendMessage(sender, String.format("§a已为CDK码 %s 增加 %d 次使用次数。当前剩余: %d",
                record.getCdkCode(), amount, record.getRemainingUses()));
            return true;
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("增加CDK使用次数时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c增加CDK使用次数时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk add <id/cdk> <标识符> <数量> §7- 增加使用次数";
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
            return Arrays.asList("1", "5", "10");
        }
        return new ArrayList<>();
    }
}