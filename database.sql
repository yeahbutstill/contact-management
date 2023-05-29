DROP DATABASE IF EXISTS contact_management_db;
CREATE DATABASE contact_management_db;

CREATE TABLE users (
    username CHARACTER VARYING(100) NOT NULL,
    password CHARACTER VARYING(100) NOT NULL,
    name CHARACTER VARYING(100) NOT NULL,
    token CHARACTER VARYING(100),
    token_expired_at BIGINT,
    PRIMARY KEY(username),
    UNIQUE (token)
);

SELECT * FROM users;

CREATE TABLE contacts(
    id CHARACTER VARYING(100) NOT NULL,
    username CHARACTER VARYING(100) NOT NULL,
    first_name CHARACTER VARYING(100) NOT NULL,
    last_name CHARACTER VARYING(100),
    phone CHARACTER VARYING(100),
    email CHARACTER VARYING(100),
    PRIMARY KEY(id),
    CONSTRAINT fk_users_contacts
        FOREIGN KEY(username)
            REFERENCES users(username)
);

SELECT * FROM contacts;

CREATE TABLE addresses(
    id CHARACTER VARYING(100) NOT NULL,
    contact_id CHARACTER VARYING(100) NOT NULL,
    street CHARACTER VARYING(200),
    city CHARACTER VARYING(100),
    province CHARACTER VARYING(100),
    country CHARACTER VARYING(100) NOT NULL,
    postal_code CHARACTER VARYING(10),
    PRIMARY KEY(id),
    CONSTRAINT fk_contacts_addresses
        FOREIGN KEY(contact_id) REFERENCES contacts(id)
);

SELECT * FROM addresses;