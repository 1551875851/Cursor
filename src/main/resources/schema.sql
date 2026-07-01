CREATE TABLE IF NOT EXISTS access_log (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    client_ip       TEXT    NOT NULL,
    http_method     TEXT    NOT NULL,
    request_uri     TEXT    NOT NULL,
    response_status INTEGER,
    cost_ms         INTEGER,
    log_type        TEXT    NOT NULL,
    error_message   TEXT,
    created_at      TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_access_log_created_at ON access_log (created_at);

CREATE TABLE IF NOT EXISTS mail_send_log (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    client_ip     TEXT,
    sender        TEXT,
    recipient     TEXT    NOT NULL,
    subject       TEXT    NOT NULL,
    content       TEXT,
    status        TEXT    NOT NULL,
    error_message TEXT,
    created_at    TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mail_send_log_created_at ON mail_send_log (created_at);

CREATE TABLE IF NOT EXISTS ruankao_scan_log (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    client_ip       TEXT,
    trigger_type    TEXT    NOT NULL,
    matched         INTEGER NOT NULL,
    email_sent      INTEGER NOT NULL,
    message         TEXT,
    matched_titles  TEXT,
    status          TEXT    NOT NULL,
    error_message   TEXT,
    created_at      TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ruankao_scan_log_created_at ON ruankao_scan_log (created_at);

CREATE TABLE IF NOT EXISTS system_event_log (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    log_level  TEXT    NOT NULL,
    category   TEXT    NOT NULL,
    message    TEXT    NOT NULL,
    detail     TEXT,
    created_at TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_system_event_log_created_at ON system_event_log (created_at);
