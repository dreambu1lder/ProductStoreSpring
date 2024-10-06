package productstore.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig {

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        System.out.println("spring.datasource.url: " + env.getProperty("spring.datasource.url"));
        System.out.println("spring.datasource.username: " + env.getProperty("spring.datasource.username"));
        System.out.println("spring.datasource.password: " + env.getProperty("spring.datasource.password"));
        System.out.println("spring.datasource.driver-class-name: " + env.getProperty("spring.datasource.driver-class-name"));
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));

        dataSource.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size", "10")));
        dataSource.setConnectionTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.connection-timeout", "30000")));
        dataSource.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.idle-timeout", "60000")));
        dataSource.setMaxLifetime(Long.parseLong(env.getProperty("spring.datasource.hikari.max-lifetime", "1800000")));

        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
