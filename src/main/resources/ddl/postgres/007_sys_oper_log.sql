CREATE TABLE IF NOT EXISTS sys_oper_log (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT,
    username        VARCHAR(64),
    module_name     VARCHAR(64) NOT NULL,
    menu_name       VARCHAR(64),
    oper_type       VARCHAR(32) NOT NULL,
    oper_desc       VARCHAR(512) NOT NULL,
    request_uri     VARCHAR(255),
    request_method  VARCHAR(16),
    client_ip       VARCHAR(64),
    status          INT NOT NULL DEFAULT 1,
    error_message   VARCHAR(512),
    created_at      VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_oper_log_created_at ON sys_oper_log (created_at);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_username ON sys_oper_log (username);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_oper_type ON sys_oper_log (oper_type);
