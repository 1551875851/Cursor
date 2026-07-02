package com.mailcursor.config;

import com.mailcursor.config.database.DatabaseType;
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

    @Value("${mailcursor.database.type:}")
    private String databaseType;

    @PostConstruct
    public void ensureDataDirectory() throws IOException {
        if (shouldEnsureLocalDataDir()) {
            Files.createDirectories(Paths.get(dataDir));
        }
    }

    private boolean shouldEnsureLocalDataDir() {
        if (DatabaseType.SQLITE.getCode().equalsIgnoreCase(databaseType)) {
            return true;
        }
        return !org.springframework.util.StringUtils.hasText(databaseType);
    }
}
