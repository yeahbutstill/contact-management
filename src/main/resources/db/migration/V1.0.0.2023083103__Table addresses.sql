CREATE TABLE addresses (
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