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
    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            int page = 1;
            String typeFilter = null;
            
            // 解析参数
            if (args.length > 0) {
                // 尝试解析参数
                String firstArg = args[0];
                String secondArg = args.length > 1 ? args[1] : null;
                
                // 判断第一个参数是否为数字（页码）
                boolean firstIsNumber = isInteger(firstArg);
                boolean secondIsNumber = secondArg != null && isInteger(secondArg);
                
                if (firstIsNumber) {
                    // 第一个参数是页码
                    page = Integer.parseInt(firstArg);
                    if (secondArg != null && !secondIsNumber) {
                        // 第二个参数是类型过滤器
                        typeFilter = secondArg;
                    }
                } else if (!firstIsNumber) {
                    // 第一个参数是类型过滤器
                    typeFilter = firstArg;
                    if (secondArg != null && secondIsNumber) {
                        // 第二个参数是页码
                        page = Integer.parseInt(secondArg);
                    }
                }
            }

            List<CdkRecord> records = plugin.getCdkRecordDao().getAllCdks();
            
            // 应用类型过滤器
            if (typeFilter != null && !typeFilter.isEmpty()) {
                List<CdkRecord> filteredRecords = new ArrayList<>();
                for (CdkRecord record : records) {
                    if (typeFilter.equals(record.getCdkType())) {
                        filteredRecords.add(record);
                    }
                }
                records = filteredRecords;
            }
            
            if (records.isEmpty()) {
                sender.sendMessage("§c暂无CDK记录。");
                return true;
            }

            // 计算分页
            int totalItems = records.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;
            
            int startIndex = (page - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
            
            List<CdkRecord> pageRecords = records.subList(startIndex, endIndex);

            sender.sendMessage("§6=== CDK列表 ===");
            sender.sendMessage(String.format("§7页码: §f%d/%d §7(总计: §f%d§7)", page, totalPages, totalItems));
            if (typeFilter != null && !typeFilter.isEmpty()) {
                sender.sendMessage(String.format("§7类型过滤: §f%s", typeFilter));
            }
            
            for (CdkRecord record : pageRecords) {
                String note = record.getNote();
                if (note == null || note.isEmpty()) {
                    note = "无备注";
                } else if (note.length() > 10) {
                    note = note.substring(0, 10) + "...";
                }
                
                String typeDisplay = record.getCdkType();
                if (typeDisplay == null || typeDisplay.isEmpty()) {
                    typeDisplay = "无类型";
                }
                
                sender.sendMessage(String.format("§f%d. %s §7(%d次) [%s] §8[%s] §7备注: §f%s",
                        record.getId(), record.getCdkCode(), record.getRemainingUses(),
                        typeDisplay,
                        record.isExpired() || record.getRemainingUses() == 0 ? "§c无效§8" : "§a有效§8", note));
            }
            sender.sendMessage("§6================");

        } catch (Exception e) {
            sender.sendMessage("§c获取CDK列表时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk list [页码] [类型] 或 /cdk list [类型] [页码]";
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // 可以返回页码或类型
            List<String> completions = new ArrayList<>();
            completions.add("1");
            completions.add("2");
            // 可以添加一些常见的类型
            completions.add("vip");
            completions.add("event");
            return completions;
        }
        if (args.length == 2) {
            // 如果第一个参数是数字，则第二个参数可能是类型，反之亦然
            List<String> completions = new ArrayList<>();
            completions.add("1");
            completions.add("2");
            completions.add("vip");
            completions.add("event");
            return completions;
        }
        return new ArrayList<>();
    }
    
    private boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}