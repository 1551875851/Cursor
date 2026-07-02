package com.mailcursor.config.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Comparator;

@Component
@Order(0)
public class DatabaseSchemaInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaInitializer.class);

    private final DataSource dataSource;
    private final DatabaseProperties properties;
    private final DatabaseDialect dialect;

    public DatabaseSchemaInitializer(DataSource dataSource,
                                     DatabaseProperties properties,
                                     DatabaseDialect dialect) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.dialect = dialect;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!properties.isSchemaInitEnabled()) {
            log.info("数据库脚本初始化已关闭");
            return;
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(dialect.ddlLocationPattern());
        if (resources.length == 0) {
            log.warn("未找到数据库脚本：{}", dialect.ddlLocationPattern());
            return;
        }
        Arrays.sort(resources, Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareToIgnoreCase)));
        log.info("开始执行 {} 数据库脚本，共 {} 个文件", dialect.getType().getCode(), resources.length);
        try (Connection connection = dataSource.getConnection()) {
            for (Resource resource : resources) {
                log.info("执行脚本：{}", resource.getFilename());
                ScriptUtils.executeSqlScript(connection, new EncodedResource(resource), true, true,
                        ScriptUtils.DEFAULT_COMMENT_PREFIX, ScriptUtils.DEFAULT_STATEMENT_SEPARATOR,
                        ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER,
                        ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
            }
        }
        log.info("数据库脚本执行完成");
    }
}
