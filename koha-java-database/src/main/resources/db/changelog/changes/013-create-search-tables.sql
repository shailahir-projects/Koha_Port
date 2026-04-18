--liquibase formatted sql

--changeset koha:013-1
CREATE TABLE IF NOT EXISTS search_filters (
    search_filter_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    query TEXT,
    limits TEXT,
    opac BOOLEAN DEFAULT FALSE,
    staff_client BOOLEAN DEFAULT TRUE
);

--changeset koha:013-2
CREATE TABLE IF NOT EXISTS marc_matchers (
    matcher_id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    description VARCHAR(255),
    record_type VARCHAR(10) DEFAULT 'biblio',
    threshold INT DEFAULT 1000
);

--changeset koha:013-3
CREATE TABLE IF NOT EXISTS saved_sql (
    id SERIAL PRIMARY KEY,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    savedsql TEXT,
    last_run TIMESTAMP,
    report_name VARCHAR(255),
    type VARCHAR(255),
    notes TEXT,
    cache_expiry INT DEFAULT 300,
    public BOOLEAN DEFAULT FALSE,
    report_area VARCHAR(6),
    report_group VARCHAR(80),
    report_subgroup VARCHAR(80),
    mana_id INT
);

