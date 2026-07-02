package com.mailcursor.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mailcursor.database")
public class DatabaseProperties {

    /**
     * 数据库类型：sqlite / mysql / postgres / oracle。
     * 留空时将根据 spring.datasource.url 自动识别。
     */
    private String type;

    /**
     * 是否在启动时执行 ddl/{type} 目录下的建表脚本。
     */
    private boolean schemaInitEnabled = true;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSchemaInitEnabled() {
        return schemaInitEnabled;
    }

    public void setSchemaInitEnabled(boolean schemaInitEnabled) {
        this.schemaInitEnabled = schemaInitEnabled;
    }
}
