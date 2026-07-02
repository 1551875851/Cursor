CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGSERIAL PRIMARY KEY,
    role_code   VARCHAR(64) NOT NULL UNIQUE,
    role_name   VARCHAR(64) NOT NULL,
    status      INT NOT NULL DEFAULT 1,
    remark      VARCHAR(255),
    created_at  VARCHAR(32) NOT NULL,
    updated_at  VARCHAR(32) NOT NULL
);
