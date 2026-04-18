--liquibase formatted sql

--changeset koha:010-1
CREATE TABLE IF NOT EXISTS erm_agreements (
    agreement_id SERIAL PRIMARY KEY,
    vendor_id INT REFERENCES aqbooksellers(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(80),
    closure_reason VARCHAR(80),
    is_perpetual BOOLEAN DEFAULT FALSE,
    renewal_priority VARCHAR(80),
    license_info TEXT
);

--changeset koha:010-2
CREATE TABLE IF NOT EXISTS erm_licenses (
    license_id SERIAL PRIMARY KEY,
    vendor_id INT REFERENCES aqbooksellers(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(80),
    status VARCHAR(80),
    started_on DATE,
    ended_on DATE
);

--changeset koha:010-3
CREATE TABLE IF NOT EXISTS erm_eholdings_packages (
    package_id SERIAL PRIMARY KEY,
    vendor_id INT,
    name VARCHAR(255) NOT NULL,
    external_id VARCHAR(255),
    provider VARCHAR(80),
    package_type VARCHAR(80),
    content_type VARCHAR(80),
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:010-4
CREATE TABLE IF NOT EXISTS erm_eholdings_titles (
    title_id SERIAL PRIMARY KEY,
    vendor_id INT,
    publication_title TEXT,
    external_id VARCHAR(255),
    print_identifier VARCHAR(255),
    online_identifier VARCHAR(255),
    date_first_issue_online VARCHAR(255),
    date_last_issue_online VARCHAR(255),
    date_monograph_published_online VARCHAR(255),
    date_monograph_published_print VARCHAR(255),
    monograph_volume VARCHAR(255),
    monograph_edition VARCHAR(255),
    first_editor VARCHAR(255),
    parent_publication_title_id VARCHAR(255),
    access_type VARCHAR(255),
    biblionumber INT REFERENCES biblio(biblionumber)
);

--changeset koha:010-5
CREATE TABLE IF NOT EXISTS erm_usage_data_providers (
    erm_usage_data_provider_id SERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    method VARCHAR(80),
    aggregator VARCHAR(80),
    service_type VARCHAR(80),
    report_release VARCHAR(80),
    customer_id VARCHAR(80),
    requestor_id VARCHAR(80),
    api_key VARCHAR(80),
    requestor_name VARCHAR(80),
    requestor_email VARCHAR(80),
    report_types TEXT
);

--changeset koha:010-6
CREATE TABLE IF NOT EXISTS erm_documents (
    document_id SERIAL PRIMARY KEY,
    agreement_id INT REFERENCES erm_agreements(agreement_id) ON DELETE CASCADE,
    license_id INT REFERENCES erm_licenses(license_id) ON DELETE CASCADE,
    file_name VARCHAR(255),
    file_type VARCHAR(255),
    file_description TEXT,
    file_content BYTEA,
    uploaded_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    physical_location TEXT,
    uri TEXT,
    notes TEXT
);

--changeset koha:010-7
CREATE TABLE IF NOT EXISTS erm_user_roles (
    user_role_id SERIAL PRIMARY KEY,
    agreement_id INT REFERENCES erm_agreements(agreement_id) ON DELETE CASCADE,
    license_id INT REFERENCES erm_licenses(license_id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES borrowers(borrowernumber),
    role VARCHAR(80) NOT NULL
);

