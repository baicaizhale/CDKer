package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.database.CdkLogDao;
import org.baicaizhale.CDKer.model.CdkLog;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LogCommandExecutor extends AbstractSubCommand {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 5;

    public LogCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!CommandUtils.hasPermission(sender, "cdk.log")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission_log"));
            return true;
        }

        try {
            int page = 1;
            String filterField = null;
            String filterValue = null;

            if (args.length == 1) {
                if (isInteger(args[0])) {
                    page = Integer.parseInt(args[0]);
                } else if ("filter".equalsIgnoreCase(args[0])) {
                    CommandUtils.sendMessage(sender, getMsg("command.log.usage"));
                    return true;
                } else {
                    CommandUtils.sendMessage(sender, getMsg("command.log.usage"));
                    return true;
                }
            } else if (args.length >= 2) {
                if ("filter".equalsIgnoreCase(args[0])) {
                    if (args.length < 3) {
                        CommandUtils.sendMessage(sender, getMsg("command.log.usage"));
                        return true;
                    }
                    filterField = args[1].toLowerCase();
                    filterValue = args[2];
                    if (args.length >= 4 && isInteger(args[3])) {
                        page = Integer.parseInt(args[3]);
                    }
                } else {
                    // 可能是页码
                    if (isInteger(args[0])) {
                        page = Integer.parseInt(args[0]);
                    }
                }
            }

            CdkLogDao logDao = plugin.getCdkLogDao();

            if (args.length >= 1 && "view".equalsIgnoreCase(args[0])) {
                if (args.length < 2 || !isInteger(args[1])) {
                    CommandUtils.sendMessage(sender, getMsg("command.log.view_usage"));
                    return true;
                }
                int viewId = Integer.parseInt(args[1]);
                // 通过直接 SQL 或 DAO 获取指定 id 的日志
                CdkLog record = null;
                try {
                    java.sql.Connection conn = plugin.getDatabaseManager().getConnection();
                    String sql = String.format("SELECT * FROM %slogs WHERE id = ?", plugin.getDatabaseManager().getTablePrefix());
                    java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, viewId);
                    java.sql.ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        record = new CdkLog();
                        record.setId(rs.getInt("id"));
                        record.setPlayerName(rs.getString("player_name"));
                        record.setPlayerUUID(rs.getString("player_uuid"));
                        record.setCdkCode(rs.getString("cdk_code"));
                        record.setCdkType(rs.getString("cdk_type"));
                        record.setCommandsExecuted(rs.getString("commands_executed"));
                        record.setUseTime(rs.getTimestamp("use_time"));
                    }
                    rs.close(); ps.close(); conn.close();
                } catch (Exception ex) {
                    plugin.getLogger().severe("读取CDK日志时出错: " + ex.getMessage());
                    ex.printStackTrace();
                    CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
                    return true;
                }

                if (record == null) {
                    CommandUtils.sendMessage(sender, getMsg("command.log.view_not_found"));
                    return true;
                }

                // 显示详细记录（纯文本或带 hover）
                List<String> commands = CommandUtils.parseCommands(record.getCommandsExecuted() == null ? "" : record.getCommandsExecuted());
                String timeDisplay = record.getUseTime() == null ? "" : DATE_FORMAT.format(record.getUseTime());
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    TextComponent header = new TextComponent("§6=== CDK兑换记录 详情 (ID: " + record.getId() + ") ===");
                    p.spigot().sendMessage(header);

                    TextComponent line1 = new TextComponent("§f[" + record.getId() + "] " + "§f[" + record.getPlayerName() + "] ");
                    String uuidHover = "UUID: " + (record.getPlayerUUID() == null ? "未知" : record.getPlayerUUID());
                    line1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(uuidHover)));
                    p.spigot().sendMessage(line1);

                    TextComponent cdkLine = new TextComponent("§7CDK: §f" + record.getCdkCode() + " §7类型: §f" + record.getCdkType());
                    p.spigot().sendMessage(cdkLine);

                    // 将命令数与时间分开：命令数有 hover，时间为纯文本
                    String commandsHover = "命令列表:\n";
                    if (commands.isEmpty()) commandsHover += "无";
                    else for (int i = 0; i < commands.size(); i++) commandsHover += String.format("%d. %s\n", i + 1, commands.get(i));

                    TextComponent cmdPart = new TextComponent("§7命令: §f[" + commands.size() + "条] ");
                    cmdPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandsHover)));
                    TextComponent timePart = new TextComponent("§7时间: §f" + timeDisplay);
                    p.spigot().sendMessage(new ComponentBuilder().append(cmdPart).append(timePart).create());
                } else {
                    CommandUtils.sendMessage(sender, String.format("§6=== CDK兑换记录 详情 (ID: %d) ===", record.getId()));
                    CommandUtils.sendMessage(sender, String.format("§f[%d] [%s]", record.getId(), record.getPlayerName()));
                    CommandUtils.sendMessage(sender, String.format("§7CDK: §f%s §7类型: §f%s", record.getCdkCode(), record.getCdkType()));
                    CommandUtils.sendMessage(sender, String.format("§7命令: §f[%d条] §7时间: §f%s", commands.size(), timeDisplay));
                    if (!commands.isEmpty()) {
                        CommandUtils.sendMessage(sender, "§f命令列表:");
                        for (int i = 0; i < commands.size(); i++) {
                            CommandUtils.sendMessage(sender, String.format("  §e%d. §f%s", i + 1, commands.get(i)));
                        }
                    }
                }

                return true;
            }

            // 校验筛选字段，按条件走 SQL 分页
            if (filterField != null
                    && !"player".equals(filterField) && !"uuid".equals(filterField) && !"type".equals(filterField)) {
                CommandUtils.sendMessage(sender, getMsg("command.log.unknown_filter", filterField));
                return true;
            }

            int totalItems = logDao.countLogs(filterField, filterValue);
            if (totalItems == 0) {
                CommandUtils.sendMessage(sender, getMsg("command.log.empty"));
                return true;
            }

            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            List<CdkLog> pageLogs = logDao.getLogsPage(page, ITEMS_PER_PAGE, filterField, filterValue);

            // 页顶部说明：提示 hover 与 view 命令
            CommandUtils.sendMessage(sender, getMsg("command.log.header", String.valueOf(page)));
            CommandUtils.sendMessage(sender, getMsg("command.log.hint"));
            for (CdkLog log : pageLogs) {
                String playerName = log.getPlayerName() == null ? "未知" : log.getPlayerName();
                String cdkCode = log.getCdkCode() == null ? "" : log.getCdkCode();
                String cdkType = log.getCdkType() == null ? "" : log.getCdkType();
                List<String> commands = CommandUtils.parseCommands(log.getCommandsExecuted() == null ? "" : log.getCommandsExecuted());
                int cmdCount = commands.size();

                // 构造显示内容
                String timeDisplay = log.getUseTime() == null ? "" : DATE_FORMAT.format(log.getUseTime());
                String uuidHover = "UUID: " + (log.getPlayerUUID() == null ? "未知" : log.getPlayerUUID());

                if (sender instanceof Player) {
                    Player playerSender = (Player) sender;

                    // ID + 玩家名段
                    TextComponent idAndPlayer = new TextComponent("§f[" + log.getId() + "] ");
                    TextComponent playerText = new TextComponent("§f[" + playerName + "] ");
                    playerText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(uuidHover)));

                    // CDK 段（不显示 UUID hover）
                    TextComponent cdkPart = new TextComponent(String.format("§7CDK: §f%s ", cdkCode));
                    TextComponent typePart = new TextComponent(String.format("§7类型: §f%s ", cdkType));

                    // 命令数量与时间（两者 hover 显示命令列表）
                    String commandsHover = "命令列表:\n";
                    if (commands.isEmpty()) commandsHover += "无";
                    else for (int i = 0; i < commands.size(); i++) commandsHover += String.format("%d. %s\n", i + 1, commands.get(i));

                    TextComponent cmdCountPart = new TextComponent(String.format("§7命令: §f[%d条] ", cmdCount));
                    cmdCountPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandsHover)));

                    TextComponent timePart = new TextComponent(String.format("§7时间: §f%s", timeDisplay));

                    BaseComponent[] comps = new ComponentBuilder()
                            .append(idAndPlayer).append(playerText).append(new TextComponent(" "))
                            .append(cdkPart).append(typePart).append(cmdCountPart).append(timePart)
                            .create();
                    playerSender.spigot().sendMessage(comps);
                } else {
                    // 控制台或非玩家，使用纯文本（简洁显示）
                    String line = String.format("§f[%d] [%s] §7CDK: §f%s §7类型: §f%s §7命令: §f[%d条] §7时间: §f%s",
                            log.getId(), playerName, cdkCode, cdkType, cmdCount, timeDisplay);
                    CommandUtils.sendMessage(sender, line);
                }
            }

            CommandUtils.sendMessage(sender, getMsg("command.log.page_info", String.valueOf(page), String.valueOf(totalPages), String.valueOf(totalItems)));
            CommandUtils.sendMessage(sender, getMsg("command.log.page_hint"));

        } catch (Exception e) {
            plugin.getLogger().severe("查询CDK日志时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.log.usage");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("1");
            completions.add("2");
            completions.add("filter");
        } else if (args.length == 2 && "filter".equalsIgnoreCase(args[0])) {
            completions.add("player");
            completions.add("uuid");
            completions.add("type");
        }
        return completions;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
