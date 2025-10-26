package org.baicaizhale.CDKer.command.impl;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.command.AbstractSubCommand;
import org.baicaizhale.CDKer.model.CdkLog;
import org.baicaizhale.CDKer.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.List;

public class ViewCommandExecutor extends AbstractSubCommand {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ViewCommandExecutor(CDKer plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            CommandUtils.sendMessage(sender, getUsage());
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];

        try {
            CdkLog record = null;
            if ("id".equals(identifierType)) {
                int id = Integer.parseInt(identifier);
                // 直接查询 logs 表
                java.sql.Connection conn = plugin.getDatabaseManager().getConnection();
                String sql = String.format("SELECT * FROM %slogs WHERE id = ?", plugin.getConfig().getString("table-prefix", "cdk_"));
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
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
            } else if ("cdk".equals(identifierType)) {
                record = plugin.getCdkLogDao().getLogsByCode(identifier).stream().findFirst().orElse(null);
            } else {
                CommandUtils.sendMessage(sender, "§c无效的标识符，必须是 'id' 或 'cdk'。");
                return true;
            }

            if (record == null) {
                CommandUtils.sendMessage(sender, "§c未找到记录。");
                return true;
            }

            CommandUtils.sendMessage(sender, String.format("§6=== CDK兑换记录 详情 (ID: %d) ===", record.getId()));
            CommandUtils.sendMessage(sender, String.format("§f[%d] [%s]", record.getId(), record.getPlayerName()));
            CommandUtils.sendMessage(sender, String.format("§7CDK: §f%s §7类型: §f%s", record.getCdkCode(), record.getCdkType()));

            List<String> commands = CommandUtils.parseCommands(record.getCommandsExecuted() == null ? "" : record.getCommandsExecuted());
            CommandUtils.sendMessage(sender, String.format("§7命令: §f[%d条] §7时间: §f%s", commands.size(), DATE_FORMAT.format(record.getUseTime())));
            if (!commands.isEmpty()) {
                CommandUtils.sendMessage(sender, "§f命令列表:");
                for (int i = 0; i < commands.size(); i++) {
                    CommandUtils.sendMessage(sender, String.format("  §e%d. §f%s", i + 1, commands.get(i)));
                }
            }

        } catch (NumberFormatException e) {
            CommandUtils.sendMessage(sender, "§c无效的数字格式。");
        } catch (Exception e) {
            CommandUtils.sendMessage(sender, "§c读取日志时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "§c用法: /cdk view <id/cdk> <标识符>";
    }
}
