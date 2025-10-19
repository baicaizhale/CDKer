package org.baicaizhale.cDKer.database;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CdkLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CdkLogDao {
    private final DatabaseManager databaseManager;
    private final String tablePrefix;

    public CdkLogDao(CDKer plugin, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.tablePrefix = plugin.getConfig().getString("table-prefix", "cdk_");
    }

    public void insertLog(CdkLog log) throws SQLException {
        String sql = String.format("INSERT INTO %slogs (player_name, player_uuid, cdk_code, cdk_type, commands_executed) VALUES (?, ?, ?, ?, ?)",
                tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getPlayerName());
            ps.setString(2, log.getPlayerUUID());
            ps.setString(3, log.getCdkCode());
            ps.setString(4, log.getCdkType());
            ps.setString(5, log.getCommandsExecuted());
            ps.executeUpdate();
        }
    }

    public List<CdkLog> getLogsByPlayer(String playerUUID) throws SQLException {
        String sql = String.format("SELECT * FROM %slogs WHERE player_uuid = ? ORDER BY use_time DESC", tablePrefix);
        List<CdkLog> logs = new ArrayList<>();
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractCdkLog(rs));
                }
            }
        }
        return logs;
    }

    public List<CdkLog> getLogsByCode(String cdkCode) throws SQLException {
        String sql = String.format("SELECT * FROM %slogs WHERE cdk_code = ? ORDER BY use_time DESC", tablePrefix);
        List<CdkLog> logs = new ArrayList<>();
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cdkCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractCdkLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * 检查玩家是否已经使用过指定的CDK码
     * @param playerUUID 玩家UUID
     * @param cdkCode CDK码
     * @return 是否已使用
     * @throws SQLException 数据库异常
     */
    public boolean hasPlayerUsedCode(String playerUUID, String cdkCode) throws SQLException {
        String sql = String.format("SELECT COUNT(*) FROM %slogs WHERE player_uuid = ? AND cdk_code = ?", tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);
            ps.setString(2, cdkCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * 记录CDK使用日志
     * @param playerName 玩家名
     * @param playerUUID 玩家UUID
     * @param cdkCode CDK码
     * @param cdkType CDK类型
     * @param commands 执行的命令列表
     * @throws SQLException 数据库异常
     */
    public void logCdkUsage(String playerName, String playerUUID, String cdkCode, String cdkType, List<String> commands) throws SQLException {
        String commandsExecuted = String.join("|", commands);
        CdkLog log = new CdkLog();
        log.setPlayerName(playerName);
        log.setPlayerUUID(playerUUID);
        log.setCdkCode(cdkCode);
        log.setCdkType(cdkType);
        log.setCommandsExecuted(commandsExecuted);
        insertLog(log);
    }

    private CdkLog extractCdkLog(ResultSet rs) throws SQLException {
        CdkLog log = new CdkLog();
        log.setId(rs.getInt("id"));
        log.setPlayerName(rs.getString("player_name"));
        log.setPlayerUUID(rs.getString("player_uuid"));
        log.setCdkCode(rs.getString("cdk_code"));
        log.setCdkType(rs.getString("cdk_type"));
        
        String commandsExecuted = rs.getString("commands_executed");
        log.setCommandsExecuted(commandsExecuted != null ? commandsExecuted : "");
        
        log.setUseTime(rs.getTimestamp("use_time"));
        return log;
    }
}