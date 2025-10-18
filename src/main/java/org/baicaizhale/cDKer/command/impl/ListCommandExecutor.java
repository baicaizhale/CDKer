package org.baicaizhale.cDKer.command.impl;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.command.AbstractSubCommand;
import org.baicaizhale.cDKer.model.CdkRecord;
import org.baicaizhale.cDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListCommandExecutor extends AbstractSubCommand {

    private static final int ITEMS_PER_PAGE = 10;

    public ListCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        try {
            int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
            if (page < 1) {
                CommandUtils.sendMessage(sender, "§c页码必须大于0。");
                return true;
            }

            List<CdkRecord> records = plugin.getCdkRecordDao().getAllCdks();
            int totalPages = (records.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;

            if (page > totalPages) {
                CommandUtils.sendMessage(sender, "§c页码超出范围。最大页数: " + totalPages);
                return true;
            }

            CommandUtils.sendMessage(sender, String.format(
                "§6=== CDK列表 (第 %d/%d 页) ===",
                page, Math.max(1, totalPages)));

            int start = (page - 1) * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, records.size());

            for (int i = start; i < end; i++) {
                CdkRecord record = records.get(i);
                String type = record.getCdkType().isEmpty() ? "普通" : record.getCdkType();
                String status = record.isExpired() ? "§c已过期" :
                              record.getRemainingUses() <= 0 ? "§c已用完" : 
                              "§a可用 (" + record.getRemainingUses() + ")";

                CommandUtils.sendMessage(sender, String.format(
                    "§7%d. §f%s §7- §e%s §7- %s",
                    i + 1, record.getCdkCode(), type, status));
            }

            if (totalPages > 1) {
                CommandUtils.sendMessage(sender, String.format(
                    "§7使用 §f/cdk list <页码> §7查看其他页 (1-%d)",
                    totalPages));
            }

            return true;
        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的页码。");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("列出CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, "§c列出CDK时出错: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "§f/cdk list [页码] §7- 列出所有CDK";
    }

    @Override
    public String getRequiredPermission() {
        return "cdk.admin";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}