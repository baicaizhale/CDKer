package org.baicaizhale.CDKer.database;

import org.baicaizhale.CDKer.model.PendingSequence;
import org.baicaizhale.CDKer.util.CommandUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PendingSequenceDao {

    private final DatabaseManager databaseManager;

    public PendingSequenceDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * 写入一条挂起序列并返回自增 id（调用方凭 id 在恢复执行前删除，防止双跑）。
     */
    public int insert(UUID playerUuid, String cdkCode, String cdkType,
                      List<String> remainingCommands, boolean hadFailure, long resumeAt) throws SQLException {
        String sql = String.format(
                "INSERT INTO %spending_sequences (player_uuid, cdk_code, cdk_type, remaining_commands, had_failure, resume_at) VALUES (?, ?, ?, ?, ?, ?)",
                databaseManager.getTablePrefix());
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, cdkCode);
            ps.setString(3, cdkType == null ? "" : cdkType);
            ps.setString(4, String.join("|", remainingCommands));
            ps.setInt(5, hadFailure ? 1 : 0);
            ps.setLong(6, resumeAt);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        }
    }

    /**
     * 按 id 删除挂起序列。
     *
     * @return 是否真的删除了一行；false 表示该序列已被其他恢复路径接手，调用方应放弃执行
     */
    public boolean deleteById(int id) throws SQLException {
        String sql = String.format("DELETE FROM %spending_sequences WHERE id = ?",
                databaseManager.getTablePrefix());
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 查询某玩家的全部挂起序列（按写入顺序）。
     */
    public List<PendingSequence> findByPlayerUuid(String playerUuid) throws SQLException {
        String sql = String.format(
                "SELECT id, player_uuid, cdk_code, cdk_type, remaining_commands, had_failure, resume_at FROM %spending_sequences WHERE player_uuid = ? ORDER BY id",
                databaseManager.getTablePrefix());
        List<PendingSequence> result = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(extract(rs));
                }
            }
        }
        return result;
    }

    private PendingSequence extract(ResultSet rs) throws SQLException {
        PendingSequence pending = new PendingSequence();
        pending.setId(rs.getInt("id"));
        pending.setPlayerUuid(UUID.fromString(rs.getString("player_uuid")));
        pending.setCdkCode(rs.getString("cdk_code"));
        pending.setCdkType(rs.getString("cdk_type"));
        pending.setRemainingCommands(CommandUtils.parseCommands(rs.getString("remaining_commands")));
        pending.setHadFailure(rs.getInt("had_failure") != 0);
        pending.setResumeAt(rs.getLong("resume_at"));
        return pending;
    }
}
