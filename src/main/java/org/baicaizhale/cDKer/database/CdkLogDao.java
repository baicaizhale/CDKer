package org.baicaizhale.cDKer.database;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CdkLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CdkLogDao {
    private final CDKer plugin;
    private final DatabaseManager databaseManager;
    private final String tablePrefix;

    public CdkLogDao(CDKer plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
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

    private CdkLog extractCdkLog(ResultSet rs) throws SQLException {
        CdkLog log = new CdkLog();
        log.setId(rs.getInt("id"));
        log.setPlayerName(rs.getString("player_name"));
        log.setPlayerUUID(rs.getString("player_uuid"));
        log.setCdkCode(rs.getString("cdk_code"));
        log.setCdkType(rs.getString("cdk_type"));
        log.setCommandsExecuted(rs.getString("commands_executed"));
        log.setUseTime(rs.getTimestamp("use_time"));
        return log;
    }
}