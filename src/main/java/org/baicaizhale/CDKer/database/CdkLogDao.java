package org.baicaizhale.CDKer.database;

import org.baicaizhale.CDKer.CDKer;
import org.baicaizhale.CDKer.model.CdkLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CdkLogDao {
    private final DatabaseManager databaseManager;
    private final String tablePrefix;

    public CdkLogDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.tablePrefix = databaseManager.getTablePrefix();
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
     * 按筛选条件统计日志总数。
     *
     * @param filterField player / uuid / type，null 表示不筛选
     */
    public int countLogs(String filterField, String filterValue) throws SQLException {
        String where = buildWhere(filterField);
        String sql = String.format("SELECT COUNT(*) FROM %slogs%s", tablePrefix, where == null ? "" : " WHERE " + where);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if ("player".equals(filterField)) {
                ps.setString(idx++, filterValue);
                ps.setString(idx, filterValue);
            } else if (filterField != null) {
                ps.setString(idx, filterValue);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * 分页查询日志，按使用时间倒序。
     *
     * @param filterField player / uuid / type，null 表示不筛选
     */
    public List<CdkLog> getLogsPage(int page, int perPage, String filterField, String filterValue) throws SQLException {
        String where = buildWhere(filterField);
        String sql = String.format("SELECT * FROM %slogs%s ORDER BY use_time DESC LIMIT ? OFFSET ?",
                tablePrefix, where == null ? "" : " WHERE " + where);
        List<CdkLog> logs = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if ("player".equals(filterField)) {
                ps.setString(idx++, filterValue);
                ps.setString(idx++, filterValue);
            } else if (filterField != null) {
                ps.setString(idx++, filterValue);
            }
            ps.setInt(idx++, perPage);
            ps.setInt(idx, (page - 1) * perPage);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractCdkLog(rs));
                }
            }
        }
        return logs;
    }

    private String buildWhere(String filterField) {
        if (filterField == null) {
            return null;
        }
        switch (filterField) {
            case "player":
                return "player_name = ? OR player_uuid = ?";
            case "uuid":
                return "player_uuid = ?";
            case "type":
                return "cdk_type = ?";
            default:
                return null;
        }
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