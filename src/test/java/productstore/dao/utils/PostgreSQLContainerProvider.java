package productstore.dao.utils;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import productstore.db.DataBaseUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        String schemaFilePath = "docker/init/schema.sql";

        try (Connection connection = DataBaseUtil.getConnection();
             Statement stmt = connection.createStatement()) {

            String sqlScript = new String(Files.readAllBytes(Paths.get(schemaFilePath)));

            for (String sqlCommand : sqlScript.split(";")) {
                if (!sqlCommand.trim().isEmpty()) {
                    stmt.execute(sqlCommand);
                }
            }

        } catch (IOException | SQLException e) {
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