-- MySQL 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code   VARCHAR(64) NOT NULL UNIQUE,
    role_name   VARCHAR(64) NOT NULL,
    status      INT NOT NULL DEFAULT 1,
    remark      VARCHAR(255),
    created_at  VARCHAR(32) NOT NULL,
    updated_at  VARCHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
