-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    org_id          INTEGER,
    username        TEXT    NOT NULL UNIQUE,
    password        TEXT    NOT NULL,
    nickname        TEXT,
    status          INTEGER NOT NULL DEFAULT 1,
    is_super_admin  INTEGER NOT NULL DEFAULT 0,
    created_at      TEXT    NOT NULL,
    updated_at      TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_user_org_id ON sys_user (org_id);
