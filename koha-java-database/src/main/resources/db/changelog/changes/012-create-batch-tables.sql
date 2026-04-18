--liquibase formatted sql

--changeset koha:012-1
CREATE TABLE IF NOT EXISTS background_jobs (
    id SERIAL PRIMARY KEY,
    status VARCHAR(20),
    progress INT,
    size INT,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    type VARCHAR(64),
    queue VARCHAR(191) DEFAULT 'default',
    data TEXT,
    context TEXT,
    enqueued_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_on TIMESTAMP,
    ended_on TIMESTAMP
);

--changeset koha:012-2
CREATE TABLE IF NOT EXISTS action_logs (
    action_id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user INT DEFAULT 0,
    module TEXT,
    action TEXT,
    object INT,
    info TEXT,
    interface VARCHAR(30),
    script TEXT
);

