CREATE TABLE sys_user_role (
    user_id NUMBER(19) NOT NULL,
    role_id NUMBER(19) NOT NULL,
    CONSTRAINT pk_sys_user_role PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_sys_user_role_role_id ON sys_user_role (role_id);
