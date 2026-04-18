--liquibase formatted sql

--changeset koha:017-1
CREATE TABLE IF NOT EXISTS preservation_trains (
    train_id SERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    description TEXT,
    default_processing_id INT,
    not_for_loan INT DEFAULT 0,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_on TIMESTAMP,
    sent_on TIMESTAMP,
    received_on TIMESTAMP
);

--changeset koha:017-2
CREATE TABLE IF NOT EXISTS preservation_processings (
    processing_id SERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    letter_code VARCHAR(20)
);

--changeset koha:017-3
CREATE TABLE IF NOT EXISTS preservation_train_items (
    train_item_id SERIAL PRIMARY KEY,
    train_id INT NOT NULL REFERENCES preservation_trains(train_id) ON DELETE CASCADE,
    item_id INT NOT NULL REFERENCES items(itemnumber),
    processing_id INT REFERENCES preservation_processings(processing_id),
    user_train_item_id INT,
    added_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:017-4
CREATE TABLE IF NOT EXISTS preservation_waiting_list (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL REFERENCES items(itemnumber),
    added_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

