package com.iqmsoft.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
//@EnableMongoRepositories(basePackages = "com.iqmsoft")
//@EnableJpaRepositories(basePackages = "com.iqmsoft.repository")
@Profile({"dev2", "dev3", "stg", "prod"})
@ConfigurationProperties(prefix = "spring.datasource")
public class PooledDatasourceConfig extends HikariConfig {

    @Bean
    public DataSource dataSource() throws SQLException {
        return new HikariDataSource(this);
    }
}
