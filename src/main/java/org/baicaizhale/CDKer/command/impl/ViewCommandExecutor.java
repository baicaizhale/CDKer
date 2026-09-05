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
        if (!CommandUtils.hasPermission(sender, "cdk.query")) {
            CommandUtils.sendMessage(sender, getMsg("command.common.no_permission"));
            return true;
        }

        if (args.length < 2) {
            CommandUtils.sendMessage(sender, getMsg("command.view.usage"));
            return true;
        }

        String identifierType = args[0].toLowerCase();
        String identifier = args[1];

        try {
            CdkLog record = null;
            if ("id".equals(identifierType)) {
                int id = Integer.parseInt(identifier);
                java.sql.Connection conn = plugin.getDatabaseManager().getConnection();
                String sql = String.format("SELECT * FROM %slogs WHERE id = ?", plugin.getDatabaseManager().getTablePrefix());
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
                CommandUtils.sendMessage(sender, getMsg("command.common.invalid_identifier"));
                return true;
            }

            if (record == null) {
                CommandUtils.sendMessage(sender, getMsg("command.view.not_found"));
                return true;
            }

            CommandUtils.sendMessage(sender, getMsg("command.view.header", String.valueOf(record.getId())));
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
            CommandUtils.sendMessage(sender, getMsg("command.common.invalid_number"));
        } catch (Exception e) {
            plugin.getLogger().severe("查看CDK日志时出错: " + e.getMessage());
            e.printStackTrace();
            CommandUtils.sendMessage(sender, getMsg("command.common.internal_error"));
        }

        return true;
    }

    @Override
    public String getUsage() {
        return getMsg("command.view.usage");
    }
}
