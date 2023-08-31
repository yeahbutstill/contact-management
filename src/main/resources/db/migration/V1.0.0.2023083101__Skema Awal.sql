CREATE TABLE users (
    username CHARACTER VARYING(100) NOT NULL,
    password CHARACTER VARYING(100) NOT NULL,
    name CHARACTER VARYING(100) NOT NULL,
    token CHARACTER VARYING(100),
    token_expired_at BIGINT,
    PRIMARY KEY(username),
    UNIQUE (token)
);