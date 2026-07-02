-- PostgreSQL 机构表
CREATE TABLE IF NOT EXISTS sys_org (
    id          BIGSERIAL PRIMARY KEY,
    parent_id   BIGINT NOT NULL DEFAULT 0,
    org_name    VARCHAR(128) NOT NULL,
    org_code    VARCHAR(64) NOT NULL UNIQUE,
    sort_order  INT NOT NULL DEFAULT 0,
    status      INT NOT NULL DEFAULT 1,
    created_at  VARCHAR(32) NOT NULL,
    updated_at  VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sys_org_parent_id ON sys_org (parent_id);
