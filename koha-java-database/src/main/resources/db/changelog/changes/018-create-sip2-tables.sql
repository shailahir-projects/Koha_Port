--liquibase formatted sql

--changeset koha:018-1
CREATE TABLE IF NOT EXISTS sip_accounts (
    id SERIAL PRIMARY KEY,
    institution_id INT,
    userid VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    flags INT DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE
);

--changeset koha:018-2
CREATE TABLE IF NOT EXISTS sip_institutions (
    id SERIAL PRIMARY KEY,
    institution_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

--changeset koha:018-3
CREATE TABLE IF NOT EXISTS sip_preferences (
    id SERIAL PRIMARY KEY,
    sip_account_id INT REFERENCES sip_accounts(id) ON DELETE CASCADE,
    variable VARCHAR(255) NOT NULL,
    value TEXT
);

