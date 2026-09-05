package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.util.CommandUtils;
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
        if (!CommandUtils.hasPermission(sender, "cdk.admin")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        try {
            int page = 1;
            String typeFilter = null;

            if (args.length > 0) {
                String firstArg = args[0];
                String secondArg = args.length > 1 ? args[1] : null;

                boolean firstIsNumber = isInteger(firstArg);
                boolean secondIsNumber = secondArg != null && isInteger(secondArg);

                if (firstIsNumber) {
                    page = Integer.parseInt(firstArg);
                    if (secondArg != null && !secondIsNumber) {
                        typeFilter = secondArg;
                    }
                } else if (!firstIsNumber) {
                    typeFilter = firstArg;
                    if (secondArg != null && secondIsNumber) {
                        page = Integer.parseInt(secondArg);
                    }
                }
            }

            int totalItems = plugin.getCdkRecordDao().countCdks(typeFilter);

            if (totalItems == 0) {
                sender.sendMessage(getMsg("command.list.empty"));
                return true;
            }

            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            List<CdkRecord> pageRecords = plugin.getCdkRecordDao().getCdksPage(page, ITEMS_PER_PAGE, typeFilter);

            sender.sendMessage(getMsg("command.list.header"));
            sender.sendMessage(getMsg("command.list.page_info", String.valueOf(page), String.valueOf(totalPages), String.valueOf(totalItems)));
            if (typeFilter != null && !typeFilter.isEmpty()) {
                sender.sendMessage(getMsg("command.list.type_filter", typeFilter));
            }

            int displayIndex = (page - 1) * ITEMS_PER_PAGE + 1;
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

                String status = record.isExpired() || record.getRemainingUses() == 0 ? "§c无效§8" : "§a有效§8";
                sender.sendMessage(getMsg("command.list.item",
                        String.valueOf(displayIndex++), record.getCdkCode(),
                        String.valueOf(record.getRemainingUses()), typeDisplay, status, note));
            }
            sender.sendMessage(getMsg("command.list.footer"));

        } catch (Exception e) {
            plugin.getLogger().severe("列出CDK时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.list.usage");
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