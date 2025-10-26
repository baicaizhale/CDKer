package org.baicaizhale.CDKer.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.baicaizhale.CDKer.CDKer;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private final CDKer plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(CDKer plugin) {
        this.plugin = plugin;
        setupDatabase();
    }

    private void setupDatabase() {
        HikariConfig config = new HikariConfig();
        String dbType = plugin.getConfig().getString("cdk.database.type", "sqlite");

        if ("mysql".equalsIgnoreCase(dbType)) {
            setupMysql(config);
        } else {
            setupSqlite(config);
        }

        // 通用配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        try {
            dataSource = new HikariDataSource(config);
            initializeTables();
        } catch (Exception e) {
            plugin.getLogger().severe("数据库连接失败: " + e.getMessage());
            plugin.getLogger().severe("插件将被禁用!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private void setupMysql(HikariConfig config) {
        String host = plugin.getConfig().getString("cdk.database.mysql.host", "localhost");
        int port = plugin.getConfig().getInt("cdk.database.mysql.port", 3306);
        String database = plugin.getConfig().getString("cdk.database.mysql.database", "cdk");
        String username = plugin.getConfig().getString("cdk.database.mysql.username", "root");
        String password = plugin.getConfig().getString("cdk.database.mysql.password", "");
        String prefix = plugin.getConfig().getString("cdk.database.mysql.table-prefix", "cdk_");

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", host, port, database));
        config.setUsername(username);
        config.setPassword(password);

        // 设置表前缀到插件实例中，以便其他类使用
        plugin.getConfig().set("table-prefix", prefix);
    }

    private void setupSqlite(HikariConfig config) {
        File dbFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("cdk.database.sqlite.file", "cdk.db"));
        if (!dbFile.exists()) {
            try {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            } catch (Exception e) {
                plugin.getLogger().severe("无法创建SQLite数据库文件: " + e.getMessage());
                return;
            }
        }

        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());

        // SQLite没有表前缀的概念，但为了统一处理，我们还是设置一个
        plugin.getConfig().set("table-prefix", "cdk_");
    }

    private void initializeTables() {
        String prefix = plugin.getConfig().getString("table-prefix", "cdk_");
        String autoIncrement = plugin.getConfig().getString("cdk.database.type", "sqlite").equalsIgnoreCase("mysql") ? 
            "AUTO_INCREMENT" : "AUTOINCREMENT";

        // CDK记录表
        String createCdkTable = String.format(
            "CREATE TABLE IF NOT EXISTS %srecords (" +
            "id INTEGER PRIMARY KEY %s," +
            "cdk_code VARCHAR(50) UNIQUE NOT NULL," +
            "remaining_uses INTEGER DEFAULT 1," +
            "commands TEXT NOT NULL," +
            "expire_time VARCHAR(20) DEFAULT 'forever'," +
            "note TEXT," +
            "cdk_type VARCHAR(50) DEFAULT ''," +
            "per_player_multiple BOOLEAN DEFAULT FALSE," +
            "created_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")", prefix, autoIncrement);

        // CDK使用日志表
        String createLogTable = String.format(
            "CREATE TABLE IF NOT EXISTS %slogs (" +
            "id INTEGER PRIMARY KEY %s," +
            "player_name VARCHAR(50) NOT NULL," +
            "player_uuid VARCHAR(36) NOT NULL," +
            "cdk_code VARCHAR(50) NOT NULL," +
            "cdk_type VARCHAR(50) DEFAULT ''," +
            "commands_executed TEXT NOT NULL," +
            "use_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (cdk_code) REFERENCES %srecords(cdk_code)" +
            ")", prefix, autoIncrement, prefix);

        try (Connection conn = getConnection()) {
            conn.createStatement().execute(createCdkTable);
            conn.createStatement().execute(createLogTable);
            plugin.getLogger().info("成功初始化数据库表!");
        } catch (SQLException e) {
            plugin.getLogger().severe("创建数据库表失败: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}