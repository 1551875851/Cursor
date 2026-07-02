-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    parent_id   INTEGER NOT NULL DEFAULT 0,
    menu_name   TEXT    NOT NULL,
    menu_type   TEXT    NOT NULL,
    path        TEXT,
    component   TEXT,
    icon        TEXT,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    visible     INTEGER NOT NULL DEFAULT 1,
    status      INTEGER NOT NULL DEFAULT 1,
    created_at  TEXT    NOT NULL,
    updated_at  TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_menu_parent_id ON sys_menu (parent_id);
