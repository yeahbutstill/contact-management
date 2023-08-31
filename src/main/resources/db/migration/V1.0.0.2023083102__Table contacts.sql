CREATE TABLE contacts (
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