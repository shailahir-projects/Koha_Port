--liquibase formatted sql

--changeset koha:009-1
CREATE TABLE IF NOT EXISTS accountlines (
    accountlines_id SERIAL PRIMARY KEY,
    issue_id INT,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    itemnumber INT REFERENCES items(itemnumber),
    date DATE,
    amount DECIMAL(28,6) NOT NULL DEFAULT 0,
    description TEXT,
    accounttype VARCHAR(80),
    debit_type_code VARCHAR(80),
    credit_type_code VARCHAR(80),
    amountoutstanding DECIMAL(28,6) NOT NULL DEFAULT 0,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note TEXT,
    manager_id INT REFERENCES borrowers(borrowernumber),
    register_id INT,
    payment_type VARCHAR(80),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    status VARCHAR(16),
    interface VARCHAR(16)
);

--changeset koha:009-2
CREATE TABLE IF NOT EXISTS cash_registers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    branch VARCHAR(10) NOT NULL REFERENCES branches(branchcode),
    branch_default BOOLEAN DEFAULT FALSE,
    starting_float DECIMAL(28,6),
    archived BOOLEAN DEFAULT FALSE
);

--changeset koha:009-3
CREATE TABLE IF NOT EXISTS cash_register_actions (
    id SERIAL PRIMARY KEY,
    code VARCHAR(24) NOT NULL,
    register_id INT NOT NULL REFERENCES cash_registers(id),
    manager_id INT NOT NULL REFERENCES borrowers(borrowernumber),
    amount DECIMAL(28,6),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

