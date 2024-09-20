package productstore.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseUtil {
    private static HikariDataSource dataSource;

    private DataBaseUtil() {}

    public static void initializeDataSource(String jdbcUrl, String username, String password) {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setMaximumPoolSize(10);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(60000);
                config.setMaxLifetime(1800000);
                config.setConnectionTestQuery("SELECT 1");
                dataSource = new HikariDataSource(config);
            } catch (Exception e) {
                throw new ExceptionInInitializerError("Ошибка инициализации пула соединений: " + e.getMessage());
            }
        }
    }

    public static void initializeDefaultDataSource() {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig("/db.properties");
                dataSource = new HikariDataSource(config);
            } catch (Exception e) {
                throw new ExceptionInInitializerError("Ошибка инициализации пула соединений для основной базы данных: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource не инициализирован. Вызовите initializeDataSource() перед использованием.");
        }
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}