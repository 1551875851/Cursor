CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

CREATE INDEX IF NOT EXISTS idx_sys_role_menu_menu_id ON sys_role_menu (menu_id);
