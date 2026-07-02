-- 机构表
CREATE TABLE IF NOT EXISTS sys_org (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    parent_id   INTEGER NOT NULL DEFAULT 0,
    org_name    TEXT    NOT NULL,
    org_code    TEXT    NOT NULL UNIQUE,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    status      INTEGER NOT NULL DEFAULT 1,
    created_at  TEXT    NOT NULL,
    updated_at  TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_org_parent_id ON sys_org (parent_id);
