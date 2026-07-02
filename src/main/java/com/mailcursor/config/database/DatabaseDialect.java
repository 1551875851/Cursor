package com.mailcursor.config.database;

public class DatabaseDialect {

    private final DatabaseType type;

    private DatabaseDialect(DatabaseType type) {
        this.type = type;
    }

    public static DatabaseDialect of(DatabaseType type) {
        return new DatabaseDialect(type);
    }

    public DatabaseType getType() {
        return type;
    }

    public String[] getGeneratedKeyColumnNames() {
        return new String[]{"id"};
    }

    public String substring(String column, int start, int length) {
        switch (type) {
            case MYSQL:
                return "SUBSTRING(" + column + ", " + start + ", " + length + ")";
            case ORACLE:
                return "SUBSTR(" + column + ", " + start + ", " + length + ")";
            case POSTGRES:
            case SQLITE:
            default:
                return "SUBSTR(" + column + ", " + start + ", " + length + ")";
        }
    }

    public String paginationClause() {
        if (type == DatabaseType.ORACLE) {
            return " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        }
        return " LIMIT ? OFFSET ?";
    }

    public Object[] paginationParams(int limit, int offset) {
        if (type == DatabaseType.ORACLE) {
            return new Object[]{offset, limit};
        }
        return new Object[]{limit, offset};
    }

    public String tableExistsSql() {
        switch (type) {
            case MYSQL:
                return "SELECT COUNT(1) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name = ?";
            case POSTGRES:
                return "SELECT COUNT(1) FROM information_schema.tables "
                        + "WHERE table_schema = current_schema() AND table_name = ?";
            case ORACLE:
                return "SELECT COUNT(1) FROM user_tables WHERE UPPER(table_name) = UPPER(?)";
            case SQLITE:
            default:
                return "SELECT COUNT(1) FROM sqlite_master WHERE type = 'table' AND name = ?";
        }
    }

    public String ddlLocationPattern() {
        return "classpath:ddl/" + type.getCode() + "/*.sql";
    }
}
