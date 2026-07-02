package com.mailcursor.config.database;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class DatabaseConfiguration {

    @Bean
    public DatabaseType databaseType(DatabaseProperties properties, Environment environment) {
        if (StringUtils.hasText(properties.getType())) {
            return DatabaseType.fromCode(properties.getType());
        }
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        return DatabaseType.fromJdbcUrl(jdbcUrl);
    }

    @Bean
    public DatabaseDialect databaseDialect(DatabaseType databaseType) {
        return DatabaseDialect.of(databaseType);
    }
}
