package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCommandExecutor extends AbstractSubCommand {

    public CreateCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        try {
            int amount = Integer.parseInt(args[0]);
            if (amount <= 0 || amount > 100) {
                CommandUtils.sendMessage(sender, "§c数量必须在1-100之间。");
                return true;
            }

            List<String> commands = CommandUtils.parseCommands(args[1]);
            if (commands.isEmpty()) {
                CommandUtils.sendMessage(sender, "§c命令不能为空。");
                return true;
            }

            int uses = args.length > 2 ? Integer.parseInt(args[2]) : plugin.getConfig().getInt("cdk.default-uses", 1);
            String note = args.length > 3 ? args[3] : "";
            String expireTime = args.length > 4 ? args[4] : "forever";
            String type = args.length > 5 ? args[5] : "";

            // 生成CDK码
            String charset = plugin.getConfig().getString("cdk.charset", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            int length = plugin.getConfig().getInt("cdk.length", 12);

            for (int i = 0; i < amount; i++) {
                String code = CommandUtils.generateCdkCode(charset, length);
                CdkRecord record = new CdkRecord(code, uses, commands, expireTime, note, type);
                plugin.getCdkRecordDao().createCdk(record);
                
                if (amount == 1) {
                    CommandUtils.sendMessage(sender, String.format("§a成功创建CDK码: §f%s", code));
                }
            }

            if (amount > 1) {
                CommandUtils.sendMessage(sender, String.format("§a成功创建 %d 个CDK码。类型: %s", 
                    amount, type.isEmpty() ? "无" : type));
            }

            return true;
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("创建CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c创建CDK时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk create <数量> <命令(用|分隔)> [兑换次数] [备注] [失效时间] [CDK类型] §7- 创建CDK";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.create";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("1", "5", "10", "50", "100");
        }
        if (args.length == 2) {
            return Arrays.asList(
                "give {player} diamond 1",
                "eco give {player} 1000",
                "give {player} diamond 1|give {player} emerald 1"
            );
        }
        if (args.length == 3) {
            return Arrays.asList("1", "5", "10", "-1");
        }
        if (args.length == 5) {
            return Arrays.asList(
                "forever", 
                "2025-12-31 23:59"
            );
        }
        if (args.length == 6) {
            return Arrays.asList("newbie", "vip", "event", "daily");
        }
        return new ArrayList<>();
    }
}