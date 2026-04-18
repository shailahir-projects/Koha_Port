--liquibase formatted sql

--changeset koha:007-1
CREATE TABLE IF NOT EXISTS additional_contents (
    id SERIAL PRIMARY KEY,
    category VARCHAR(20) NOT NULL,
    code VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    title VARCHAR(250),
    content TEXT,
    lang VARCHAR(25) DEFAULT '',
    published_on DATE,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expirationdate DATE,
    number INT DEFAULT 0
);

--changeset koha:007-2
CREATE TABLE IF NOT EXISTS advanced_editor_macros (
    id SERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    macro TEXT,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    shared BOOLEAN DEFAULT FALSE
);

--changeset koha:007-3
CREATE TABLE IF NOT EXISTS authorised_values_branches (
    av_id INT REFERENCES authorised_values(id) ON DELETE CASCADE,
    branchcode VARCHAR(10) REFERENCES branches(branchcode) ON DELETE CASCADE,
    PRIMARY KEY (av_id, branchcode)
);

--changeset koha:007-4
CREATE TABLE IF NOT EXISTS additional_field_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tablename VARCHAR(255) NOT NULL,
    authorised_value_category VARCHAR(32),
    marcfield VARCHAR(16),
    searchable BOOLEAN DEFAULT FALSE,
    repeatable BOOLEAN DEFAULT FALSE
);

--changeset koha:007-5
CREATE TABLE IF NOT EXISTS additional_field_values (
    id SERIAL PRIMARY KEY,
    field_id INT NOT NULL REFERENCES additional_field_types(id) ON DELETE CASCADE,
    record_id INT NOT NULL,
    value VARCHAR(255)
);

--changeset koha:007-6
CREATE TABLE IF NOT EXISTS tickets (
    id SERIAL PRIMARY KEY,
    reporter_id INT NOT NULL REFERENCES borrowers(borrowernumber),
    reported_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    resolver_id INT REFERENCES borrowers(borrowernumber),
    resolved_date TIMESTAMP,
    status VARCHAR(80) DEFAULT 'new',
    biblio_id INT REFERENCES biblio(biblionumber),
    source VARCHAR(80) DEFAULT 'catalog'
);

--changeset koha:007-7
CREATE TABLE IF NOT EXISTS ticket_updates (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES borrowers(borrowernumber),
    public BOOLEAN DEFAULT FALSE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message TEXT NOT NULL,
    status VARCHAR(80)
);

--changeset koha:007-8
CREATE TABLE IF NOT EXISTS identity_providers (
    identity_provider_id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255),
    protocol VARCHAR(20) NOT NULL,
    config TEXT NOT NULL,
    mapping TEXT,
    matchpoint VARCHAR(50),
    icon_url TEXT
);

--changeset koha:007-9
CREATE TABLE IF NOT EXISTS identity_provider_domains (
    identity_provider_domain_id SERIAL PRIMARY KEY,
    identity_provider_id INT NOT NULL REFERENCES identity_providers(identity_provider_id) ON DELETE CASCADE,
    domain VARCHAR(100),
    auto_register BOOLEAN DEFAULT FALSE,
    update_on_auth BOOLEAN DEFAULT FALSE,
    default_library_id VARCHAR(10) REFERENCES branches(branchcode),
    default_category_id VARCHAR(10) REFERENCES categories(categorycode),
    allow_opac BOOLEAN DEFAULT TRUE,
    allow_staff BOOLEAN DEFAULT TRUE
);

