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

            if ("id".equalsIgnoreCase(identifier)) {
                plugin.getCdkRecordDao().deleteCdkById(Integer.parseInt(target));
                CommandUtils.sendMessage(sender, String.format("§a已删除ID为 %s 的CDK码。", target));
            } else {
                plugin.getCdkRecordDao().deleteCdk(target);
                CommandUtils.sendMessage(sender, String.format("§a已删除CDK码: %s", target));
            }
            return true;
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("删除CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c删除CDK时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk del <id/cdk> <标识符> §7- 删除CDK";
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
        return new ArrayList<>();
    }
}