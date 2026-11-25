package com.budgetbuddies.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = setting("DB_URL",
            "jdbc:mysql://localhost:3306/budget_buddies?serverTimezone=Asia/Jakarta");
    private static final String USER = setting("DB_USER", "root");
    private static final String PASSWORD = setting("DB_PASSWORD", "");

    private static String setting(String key, String fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        return (value == null || value.isBlank()) ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL tidak ada di WEB-INF/lib. Build ulang dengan mvn clean package.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}