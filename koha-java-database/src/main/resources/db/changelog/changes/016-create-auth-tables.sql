--liquibase formatted sql

--changeset koha:016-1
CREATE TABLE IF NOT EXISTS api_keys (
    client_id VARCHAR(191) PRIMARY KEY,
    secret VARCHAR(191) NOT NULL,
    description VARCHAR(255),
    patron_id INT NOT NULL REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    active BOOLEAN DEFAULT TRUE
);

--changeset koha:016-2
CREATE TABLE IF NOT EXISTS oauth_access_tokens (
    access_token VARCHAR(191) PRIMARY KEY,
    client_id VARCHAR(191) NOT NULL REFERENCES api_keys(client_id) ON DELETE CASCADE,
    expires TIMESTAMP NOT NULL
);

