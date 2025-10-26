package org.baicaizhale.CDKer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DatabaseConfig {
    private String type = "sqlite";
    private SqliteConfig sqlite = new SqliteConfig();
    private MysqlConfig mysql = new MysqlConfig();

    @Data
    @NoArgsConstructor
    public static class SqliteConfig {
        private String file = "cdk.db";
    }

    @Data
    @NoArgsConstructor
    public static class MysqlConfig {
        private String host = "localhost";
        private int port = 3306;
        private String database = "cdk";
        private String username = "root";
        private String password = "";
        private String tablePrefix = "cdk_";
    }
}