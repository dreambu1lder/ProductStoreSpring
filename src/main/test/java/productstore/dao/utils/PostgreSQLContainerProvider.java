package productstore.dao.utils;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import productstore.db.DataBaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSQLContainerProvider {
    private static PostgreSQLContainer<?> postgreSQLContainer;

    public static void startContainer() {
        if (postgreSQLContainer == null) {
            postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass")
                    .waitingFor(Wait.forListeningPort())
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("PostgreSQLContainer")));

            
            postgreSQLContainer.start();

            
            String jdbcUrl = postgreSQLContainer.getJdbcUrl();
            String username = postgreSQLContainer.getUsername();
            String password = postgreSQLContainer.getPassword();

            
            System.out.println("PostgreSQL container started with JDBC URL: " + jdbcUrl);

            
            DataBaseUtil.initializeDataSource(jdbcUrl, username, password);

            
            initializeDatabase();
        }
    }

    
    public static void initializeDatabase() {
        try (Connection connection = DataBaseUtil.getConnection();
             Statement stmt = connection.createStatement()) {

            
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) UNIQUE NOT NULL CHECK (trim(name) <> ''), " +
                    "email VARCHAR(255) NOT NULL CHECK (trim(email) <> '')" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL CHECK (trim(name) <> ''), " +
                    "price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders_products (" +
                    "order_id BIGINT NOT NULL, " +
                    "product_id BIGINT NOT NULL, " +
                    "PRIMARY KEY (order_id, product_id), " +
                    "CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the database", e);
        }
    }

    public static PostgreSQLContainer<?> getContainer() {
        if (postgreSQLContainer == null) {
            throw new IllegalStateException("Контейнер не запущен. Сначала вызовите startContainer().");
        }
        return postgreSQLContainer;
    }

    public static void stopContainer() {
        if (postgreSQLContainer != null) {
            DataBaseUtil.closeDataSource();
            postgreSQLContainer.stop();
            postgreSQLContainer = null;
        }
    }
}