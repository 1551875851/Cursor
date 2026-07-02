-- MySQL 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id   BIGINT NOT NULL DEFAULT 0,
    menu_name   VARCHAR(64) NOT NULL,
    menu_type   VARCHAR(16) NOT NULL,
    path        VARCHAR(128),
    component   VARCHAR(64),
    icon        VARCHAR(64),
    sort_order  INT NOT NULL DEFAULT 0,
    visible     INT NOT NULL DEFAULT 1,
    status      INT NOT NULL DEFAULT 1,
    created_at  VARCHAR(32) NOT NULL,
    updated_at  VARCHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sys_menu_parent_id ON sys_menu (parent_id);
