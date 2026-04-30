package com.comet.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        String dbUrl = System.getenv("COMET_DB_URL");
        String dbUser = System.getenv("COMET_DB_USER");
        String dbPass = System.getenv("COMET_DB_PASS");

        if (dbUrl == null || dbUrl.isBlank()
                || dbUser == null || dbUser.isBlank()
                || dbPass == null || dbPass.isBlank()) {
            throw new IllegalStateException("Missing required database environment variables");
        }

        System.out.println("\n=== DATABASE DIAGNOSTICS ===");
        System.out.println("Attempting connection to: " + dbUrl);
        System.out.println("User: " + dbUser);
        System.out.println(
            "Password length: " + (dbPass != null ? dbPass.length() : "NULL!!!")
        );
        System.out.println("============================\n");

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPass);

        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    private DatabaseManager() {
        // Prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
