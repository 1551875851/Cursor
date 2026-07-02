-- 用户操作行为日志
CREATE TABLE IF NOT EXISTS sys_oper_log (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER,
    username        TEXT,
    module_name     TEXT    NOT NULL,
    menu_name       TEXT,
    oper_type       TEXT    NOT NULL,
    oper_desc       TEXT    NOT NULL,
    request_uri     TEXT,
    request_method  TEXT,
    client_ip       TEXT,
    status          INTEGER NOT NULL DEFAULT 1,
    error_message   TEXT,
    created_at      TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_oper_log_created_at ON sys_oper_log (created_at);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_username ON sys_oper_log (username);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_oper_type ON sys_oper_log (oper_type);
