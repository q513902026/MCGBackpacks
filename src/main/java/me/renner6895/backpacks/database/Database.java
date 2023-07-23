package me.renner6895.backpacks.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    public Connection connect(HikariConfig config) {
        try {
            HikariDataSource hikariDataSource = new HikariDataSource(config);
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("数据库链接失败，请检查数据库配置");
        }
    }
}
