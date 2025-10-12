package org.baicaizhale.cDKer.database;

import org.baicaizhale.cDKer.CDKer;
import org.baicaizhale.cDKer.model.CdkRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CdkRecordDao {
    private final CDKer plugin;
    private final DatabaseManager databaseManager;
    private final String tablePrefix;

    public CdkRecordDao(CDKer plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.tablePrefix = plugin.getConfig().getString("table-prefix", "cdk_");
    }

    public void createCdk(CdkRecord record) throws SQLException {
        String sql = String.format("INSERT INTO %srecords (cdk_code, remaining_uses, commands, expire_time, note, cdk_type) VALUES (?, ?, ?, ?, ?, ?)",
                tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.getCdkCode());
            ps.setInt(2, record.getRemainingUses());
            ps.setString(3, String.join("|", record.getCommands()));
            ps.setString(4, record.getExpireTime());
            ps.setString(5, record.getNote());
            ps.setString(6, record.getCdkType());
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

    public void updateCdk(CdkRecord record) throws SQLException {
        String sql = String.format("UPDATE %srecords SET remaining_uses = ?, commands = ?, expire_time = ?, note = ?, cdk_type = ? WHERE cdk_code = ?",
                tablePrefix);
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, record.getRemainingUses());
            ps.setString(2, String.join("|", record.getCommands()));
            ps.setString(3, record.getExpireTime());
            ps.setString(4, record.getNote());
            ps.setString(5, record.getCdkType());
            ps.setString(6, record.getCdkCode());
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

    private CdkRecord extractCdkRecord(ResultSet rs) throws SQLException {
        CdkRecord record = new CdkRecord();
        record.setId(rs.getInt("id"));
        record.setCdkCode(rs.getString("cdk_code"));
        record.setRemainingUses(rs.getInt("remaining_uses"));
        record.setCommands(Arrays.asList(rs.getString("commands").split("\\|")));
        record.setExpireTime(rs.getString("expire_time"));
        record.setNote(rs.getString("note"));
        record.setCdkType(rs.getString("cdk_type"));
        record.setCreatedTime(rs.getTimestamp("created_time"));
        return record;
    }
}