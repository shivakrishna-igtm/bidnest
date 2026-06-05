CREATE TABLE users (
    id            BIGSERIAL                PRIMARY KEY,
    username      TEXT                     NOT NULL,
    full_name     TEXT                     NOT NULL,
    email         TEXT                     NOT NULL,
    password_hash TEXT                     NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active     BOOLEAN                  NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email    UNIQUE (email)
);