package com.mailcursor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class DatabaseConfig {

    @Value("${mailcursor.data-dir:./data}")
    private String dataDir;

    @PostConstruct
    public void ensureDataDirectory() throws IOException {
        Files.createDirectories(Paths.get(dataDir));
    }
}
