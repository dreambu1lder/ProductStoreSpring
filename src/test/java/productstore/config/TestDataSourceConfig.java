package productstore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
@ActiveProfiles("test")
public class TestDataSourceConfig {

    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        container.start();
        return container;
    }

    @Bean
    public DataSource dataSource(PostgreSQLContainer<?> postgreSQLContainer) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        config.setUsername(postgreSQLContainer.getUsername());
        config.setPassword(postgreSQLContainer.getPassword());
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
