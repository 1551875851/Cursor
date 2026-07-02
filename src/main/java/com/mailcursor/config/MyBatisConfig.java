package com.mailcursor.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@MapperScan("com.mailcursor.mapper")
public class MyBatisConfig {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("SQLite", "sqlite");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("MariaDB", "mysql");
        properties.setProperty("PostgreSQL", "postgres");
        properties.setProperty("Oracle", "oracle");
        provider.setProperties(properties);
        return provider;
    }
}
