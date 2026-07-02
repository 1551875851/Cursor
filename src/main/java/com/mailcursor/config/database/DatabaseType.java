package com.mailcursor.config.database;

import org.springframework.util.StringUtils;

public enum DatabaseType {

    SQLITE("sqlite"),
    MYSQL("mysql"),
    POSTGRES("postgres"),
    ORACLE("oracle");

    private final String code;

    DatabaseType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DatabaseType fromCode(String code) {
        if (!StringUtils.hasText(code)) {
            return SQLITE;
        }
        String normalized = code.trim().toLowerCase();
        for (DatabaseType type : values()) {
            if (type.code.equals(normalized) || type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的数据库类型：" + code);
    }

    public static DatabaseType fromJdbcUrl(String jdbcUrl) {
        if (!StringUtils.hasText(jdbcUrl)) {
            return SQLITE;
        }
        String url = jdbcUrl.toLowerCase();
        if (url.startsWith("jdbc:sqlite:")) {
            return SQLITE;
        }
        if (url.startsWith("jdbc:mysql:") || url.startsWith("jdbc:mariadb:")) {
            return MYSQL;
        }
        if (url.startsWith("jdbc:postgresql:")) {
            return POSTGRES;
        }
        if (url.startsWith("jdbc:oracle:")) {
            return ORACLE;
        }
        throw new IllegalArgumentException("无法从 JDBC URL 识别数据库类型：" + jdbcUrl);
    }
}
