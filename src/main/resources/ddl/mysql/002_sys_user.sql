-- MySQL 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id          BIGINT,
    username        VARCHAR(64) NOT NULL UNIQUE,
    password        VARCHAR(128) NOT NULL,
    nickname        VARCHAR(64),
    status          INT NOT NULL DEFAULT 1,
    is_super_admin  INT NOT NULL DEFAULT 0,
    created_at      VARCHAR(32) NOT NULL,
    updated_at      VARCHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sys_user_org_id ON sys_user (org_id);
