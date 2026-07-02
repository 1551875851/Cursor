-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    role_code   TEXT    NOT NULL UNIQUE,
    role_name   TEXT    NOT NULL,
    status      INTEGER NOT NULL DEFAULT 1,
    remark      TEXT,
    created_at  TEXT    NOT NULL,
    updated_at  TEXT    NOT NULL
);
