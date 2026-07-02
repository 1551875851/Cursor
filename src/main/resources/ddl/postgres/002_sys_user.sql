CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGSERIAL PRIMARY KEY,
    org_id          BIGINT,
    username        VARCHAR(64) NOT NULL UNIQUE,
    password        VARCHAR(128) NOT NULL,
    nickname        VARCHAR(64),
    status          INT NOT NULL DEFAULT 1,
    is_super_admin  INT NOT NULL DEFAULT 0,
    created_at      VARCHAR(32) NOT NULL,
    updated_at      VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_user_org_id ON sys_user (org_id);
