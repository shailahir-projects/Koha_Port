--liquibase formatted sql

--changeset koha:011-1
CREATE TABLE IF NOT EXISTS illrequests (
    illrequest_id SERIAL PRIMARY KEY,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    biblio_id INT REFERENCES biblio(biblionumber),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    status VARCHAR(50),
    status_alias VARCHAR(80),
    placed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    replied TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed TIMESTAMP,
    medium VARCHAR(30),
    accessurl TEXT,
    cost TEXT,
    price_paid TEXT,
    notesopac TEXT,
    notesstaff TEXT,
    orderid VARCHAR(50),
    backend VARCHAR(20),
    batch_id INT
);

--changeset koha:011-2
CREATE TABLE IF NOT EXISTS illrequestattributes (
    illrequest_id INT NOT NULL REFERENCES illrequests(illrequest_id) ON DELETE CASCADE,
    type VARCHAR(200) NOT NULL,
    value TEXT NOT NULL,
    readonly BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (illrequest_id, type)
);

--changeset koha:011-3
CREATE TABLE IF NOT EXISTS illcomments (
    illcomment_id SERIAL PRIMARY KEY,
    illrequest_id INT NOT NULL REFERENCES illrequests(illrequest_id) ON DELETE CASCADE,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    comment TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:011-4
CREATE TABLE IF NOT EXISTS illbatches (
    ill_batch_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    backend VARCHAR(20) NOT NULL,
    patron_id INT REFERENCES borrowers(borrowernumber),
    library_id VARCHAR(10) REFERENCES branches(branchcode),
    status_code VARCHAR(20) DEFAULT 'NEW',
    cardnumber VARCHAR(32)
);

--changeset koha:011-5
CREATE TABLE IF NOT EXISTS illbatch_statuses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    is_system BOOLEAN DEFAULT FALSE
);

