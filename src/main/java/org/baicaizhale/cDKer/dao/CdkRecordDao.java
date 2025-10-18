//
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;
// import java.util.logging.Level;
// import org.baicaizhale.cDKer.CDKer;
// import org.baicaizhale.cDKer.database.DatabaseManager;
//
// public class CdkRecordDao {
//     private final CDKer plugin;
//     private final DatabaseManager dbManager;
//     private final String recordTableName;
//
//     public CdkRecordDao(CDKer plugin, DatabaseManager dbManager, String recordTableName) {
//         this.plugin = plugin;
//         this.dbManager = dbManager;
//         this.recordTableName = recordTableName;
//     }
//
//     public void deleteCdkById(int id) {
//         String sql = "DELETE FROM " + recordTableName + " WHERE id = ?";
//         try (Connection conn = dbManager.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setInt(1, id);
//             pstmt.executeUpdate();
//         } catch (SQLException e) {
//             plugin.getLogger().log(Level.SEVERE, "Could not delete CDK record by id: " + id, e);
//         }
//     }
//
//     public void deleteCdkByCode(String code) {
//         String sql = "DELETE FROM " + recordTableName + " WHERE cdk_code = ?";
//         try (Connection conn = dbManager.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setString(1, code);
//             pstmt.executeUpdate();
//         } catch (SQLException e) {
//             plugin.getLogger().log(Level.SEVERE, "Could not delete CDK record by code: " + code, e);
//         }
//     }
//     public void deleteAllCdks() {
//         String sql = "DELETE FROM " + recordTableName;
//         try (Connection conn = dbManager.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.executeUpdate();
//         } catch (SQLException e) {
//             plugin.getLogger().log(Level.SEVERE, "Could not delete all CDK records", e);
//         }
//     }
//     public void createCdk(org.baicaizhale.cDKer.model.CdkRecord record) throws SQLException {
//         String sql = "INSERT INTO " + recordTableName + " (cdk_code, remaining_uses, commands, expire_time, note, cdk_type, per_player_multiple) VALUES (?, ?, ?, ?, ?, ?, ?)";
//         try (Connection conn = dbManager.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setString(1, record.getCdkCode());
//             pstmt.setInt(2, record.getRemainingUses());
//             pstmt.setString(3, String.join("|", record.getCommands()));
//             pstmt.setString(4, record.getExpireTime());
//             pstmt.setString(5, record.getNote());
//             pstmt.setString(6, record.getCdkType());
//             pstmt.setBoolean(7, record.isPerPlayerMultiple());
//             pstmt.executeUpdate();
//         }
//     }
// }
