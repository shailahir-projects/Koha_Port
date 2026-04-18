--liquibase formatted sql

--changeset koha:014-1
CREATE TABLE IF NOT EXISTS letter (
    id SERIAL PRIMARY KEY,
    module VARCHAR(20) NOT NULL,
    code VARCHAR(20) NOT NULL,
    branchcode VARCHAR(10) DEFAULT '',
    name VARCHAR(100) NOT NULL,
    is_html BOOLEAN DEFAULT FALSE,
    title VARCHAR(200),
    content TEXT,
    message_transport_type VARCHAR(20) DEFAULT 'email',
    lang VARCHAR(25) DEFAULT 'default',
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:014-2
CREATE TABLE IF NOT EXISTS message_queue (
    message_id SERIAL PRIMARY KEY,
    letter_id INT REFERENCES letter(id),
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    subject TEXT,
    content TEXT,
    metadata TEXT,
    to_address TEXT,
    from_address TEXT,
    reply_address TEXT,
    content_type TEXT,
    delivery_note TEXT,
    message_transport_type VARCHAR(20) NOT NULL DEFAULT 'email',
    status VARCHAR(10) NOT NULL DEFAULT 'pending',
    time_queued TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    failure_code TEXT
);

--changeset koha:014-3
CREATE TABLE IF NOT EXISTS message_preferences (
    borrower_message_preference_id SERIAL PRIMARY KEY,
    borrowernumber INT REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    categorycode VARCHAR(10),
    message_attribute_id INT,
    days_in_advance INT,
    wants_digest BOOLEAN DEFAULT FALSE
);

--changeset koha:014-4
CREATE TABLE IF NOT EXISTS message_transports (
    message_attribute_id INT NOT NULL,
    message_transport_type VARCHAR(20) NOT NULL,
    is_digest BOOLEAN DEFAULT FALSE,
    letter_module VARCHAR(20),
    letter_code VARCHAR(20),
    branchcode VARCHAR(10) DEFAULT '',
    PRIMARY KEY (message_attribute_id, message_transport_type, is_digest)
);

--changeset koha:014-5
CREATE TABLE IF NOT EXISTS message_preference_attributes (
    message_attribute_id SERIAL PRIMARY KEY,
    message_name VARCHAR(40) NOT NULL,
    takes_days BOOLEAN DEFAULT FALSE
);

