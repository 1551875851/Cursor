CREATE TABLE IF NOT EXISTS access_log (
    id              BIGSERIAL PRIMARY KEY,
    client_ip       VARCHAR(64) NOT NULL,
    http_method     VARCHAR(16) NOT NULL,
    request_uri     VARCHAR(255) NOT NULL,
    response_status INT,
    cost_ms         BIGINT,
    log_type        VARCHAR(32) NOT NULL,
    error_message   VARCHAR(512),
    created_at      VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_access_log_created_at ON access_log (created_at);

CREATE TABLE IF NOT EXISTS mail_send_log (
    id            BIGSERIAL PRIMARY KEY,
    client_ip     VARCHAR(64),
    sender        VARCHAR(128),
    recipient     VARCHAR(128) NOT NULL,
    subject       VARCHAR(255) NOT NULL,
    content       TEXT,
    status        VARCHAR(32) NOT NULL,
    error_message VARCHAR(512),
    created_at    VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mail_send_log_created_at ON mail_send_log (created_at);

CREATE TABLE IF NOT EXISTS ruankao_scan_log (
    id              BIGSERIAL PRIMARY KEY,
    client_ip       VARCHAR(64),
    trigger_type    VARCHAR(32) NOT NULL,
    matched         INT NOT NULL,
    email_sent      INT NOT NULL,
    message         VARCHAR(512),
    matched_titles  TEXT,
    status          VARCHAR(32) NOT NULL,
    error_message   VARCHAR(512),
    created_at      VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ruankao_scan_log_created_at ON ruankao_scan_log (created_at);

CREATE TABLE IF NOT EXISTS system_event_log (
    id         BIGSERIAL PRIMARY KEY,
    log_level  VARCHAR(16) NOT NULL,
    category   VARCHAR(32) NOT NULL,
    message    VARCHAR(512) NOT NULL,
    detail     TEXT,
    created_at VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_system_event_log_created_at ON system_event_log (created_at);
