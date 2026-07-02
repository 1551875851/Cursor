CREATE TABLE sys_role_menu (
    role_id NUMBER(19) NOT NULL,
    menu_id NUMBER(19) NOT NULL,
    CONSTRAINT pk_sys_role_menu PRIMARY KEY (role_id, menu_id)
);

CREATE INDEX idx_sys_role_menu_menu_id ON sys_role_menu (menu_id);
