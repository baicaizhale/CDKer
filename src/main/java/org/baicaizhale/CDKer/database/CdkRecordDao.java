package org.baicaizhale.CDKer.database;

import org.baicaizhale.CDKer.model.CdkRecord;
import org.baicaizhale.CDKer.model.RedeemResult;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CdkRecordDao {
    private static final SimpleDateFormat EXPIRE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private final DatabaseManager databaseManager;
    private final String tablePrefix;

    public CdkRecordDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.tablePrefix = databaseManager.getTablePrefix();
    }

    public void createCdk(CdkRecord record) throws SQLException {
        String sql = String.format("INSERT INTO %srecords (cdk_code, remaining_uses, commands, expire_time, note, cdk_type, per_player_multiple) VALUES (?, ?, ?, ?, ?, ?, ?)",
                tablePrefix);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.getCdkCode());
            ps.setInt(2, record.getRemainingUses());
            ps.setString(3, String.join("|", record.getCommands()));
            ps.setString(4, record.getExpireTime());
            ps.setString(5, record.getNote());
            ps.setString(6, record.getCdkType());
            ps.setBoolean(7, record.isPerPlayerMultiple());
            ps.executeUpdate();
        }
    }

    public CdkRecord getCdkByCode(String code) throws SQLException {
        String sql = String.format("SELECT * FROM %srecords WHERE cdk_code = ?", tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCdkRecord(rs);
                }
            }
        }
        return null;
    }

    public CdkRecord getCdkById(int id) throws SQLException {
        String sql = String.format("SELECT * FROM %srecords WHERE id = ?", tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCdkRecord(rs);
                }
            }
        }
        return null;
    }

    public List<CdkRecord> getAllCdks() throws SQLException {
        String sql = String.format("SELECT * FROM %srecords", tablePrefix);
        List<CdkRecord> records = new ArrayList<>();
        
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(extractCdkRecord(rs));
            }
        }
        return records;
    }

    public int countCdks(String typeFilter) throws SQLException {
        boolean hasFilter = typeFilter != null && !typeFilter.isEmpty();
        String sql = String.format("SELECT COUNT(*) FROM %srecords%s",
                tablePrefix, hasFilter ? " WHERE cdk_type = ?" : "");
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasFilter) {
                ps.setString(1, typeFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<CdkRecord> getCdksPage(int page, int perPage, String typeFilter) throws SQLException {
        boolean hasFilter = typeFilter != null && !typeFilter.isEmpty();
        String sql = String.format("SELECT * FROM %srecords%s ORDER BY id LIMIT ? OFFSET ?",
                tablePrefix, hasFilter ? " WHERE cdk_type = ?" : "");
        List<CdkRecord> records = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (hasFilter) {
                ps.setString(idx++, typeFilter);
            }
            ps.setInt(idx++, perPage);
            ps.setInt(idx, (page - 1) * perPage);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(extractCdkRecord(rs));
                }
            }
        }
        return records;
    }

    public void updateCdk(CdkRecord record) throws SQLException {
        String sql = String.format("UPDATE %srecords SET remaining_uses = ?, commands = ?, expire_time = ?, note = ?, cdk_type = ?, per_player_multiple = ? WHERE cdk_code = ?",
                tablePrefix);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, record.getRemainingUses());
            ps.setString(2, String.join("|", record.getCommands()));
            ps.setString(3, record.getExpireTime());
            ps.setString(4, record.getNote());
            ps.setString(5, record.getCdkType());
            ps.setBoolean(6, record.isPerPlayerMultiple());
            ps.setString(7, record.getCdkCode());
            ps.executeUpdate();
        }
    }

    public void deleteCdk(String code) throws SQLException {
        String sql = String.format("DELETE FROM %srecords WHERE cdk_code = ?", tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    public void deleteCdkById(int id) throws SQLException {
        String sql = String.format("DELETE FROM %srecords WHERE id = ?", tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * 删除所有CDK记录
     */
    public void deleteAllCdks() throws SQLException {
        String sql = String.format("DELETE FROM %srecords", tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    /**
     * 在单个事务内原子地完成兑换：校验单人限用、扣减次数、写使用日志。
     * 全部成功才提交，失败即回滚，避免并发/崩溃导致的重复兑换或"奖励已发但码未扣"。
     */
    public RedeemResult redeem(CdkRecord record, String playerUuid, String playerName) throws SQLException {
        String code = record.getCdkCode();
        String now = EXPIRE_FORMAT.format(new Date());
        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!record.isPerPlayerMultiple()) {
                    String checkSql = String.format("SELECT COUNT(*) FROM %slogs WHERE player_uuid = ? AND cdk_code = ?", tablePrefix);
                    try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                        ps.setString(1, playerUuid);
                        ps.setString(2, code);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                conn.rollback();
                                return RedeemResult.ALREADY_USED;
                            }
                        }
                    }
                }

                // 条件扣减：仅当剩余次数 > 0 且未过期时才扣，防止并发重复兑换
                String updateSql = String.format(
                        "UPDATE %srecords SET remaining_uses = remaining_uses - 1 " +
                        "WHERE cdk_code = ? AND remaining_uses > 0 AND (expire_time = 'forever' OR expire_time > ?)",
                        tablePrefix);
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, code);
                    ps.setString(2, now);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return RedeemResult.USED_UP;
                    }
                }

                String insertSql = String.format("INSERT INTO %slogs (player_name, player_uuid, cdk_code, cdk_type, commands_executed) VALUES (?, ?, ?, ?, ?)",
                        tablePrefix);
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, playerName);
                    ps.setString(2, playerUuid);
                    ps.setString(3, code);
                    ps.setString(4, record.getCdkType());
                    ps.setString(5, String.join("|", record.getCommands()));
                    ps.executeUpdate();
                }

                conn.commit();
                return RedeemResult.SUCCESS;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private CdkRecord extractCdkRecord(ResultSet rs) throws SQLException {
        CdkRecord record = new CdkRecord();
        record.setId(rs.getInt("id"));
        record.setCdkCode(rs.getString("cdk_code"));
        record.setRemainingUses(rs.getInt("remaining_uses"));
        String commandsStr = rs.getString("commands");
        record.setCommands(commandsStr != null ?
            org.baicaizhale.CDKer.util.CommandUtils.parseCommands(commandsStr) :
            new java.util.ArrayList<>());
        record.setExpireTime(rs.getString("expire_time"));
        record.setNote(rs.getString("note"));
        record.setCdkType(rs.getString("cdk_type"));
        record.setCreatedTime(rs.getTimestamp("created_time"));
        try {
            record.setPerPlayerMultiple(rs.getBoolean("per_player_multiple"));
        } catch (SQLException | IllegalArgumentException e) {
            record.setPerPlayerMultiple(false);
        }
        return record;
    }
}
