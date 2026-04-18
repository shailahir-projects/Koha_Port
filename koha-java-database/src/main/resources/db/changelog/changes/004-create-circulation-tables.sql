--liquibase formatted sql

--changeset koha:004-1
CREATE TABLE IF NOT EXISTS issues (
    issue_id SERIAL PRIMARY KEY,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    itemnumber INT REFERENCES items(itemnumber),
    date_due TIMESTAMP,
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    returndate TIMESTAMP,
    lastreneweddate TIMESTAMP,
    renewals INT DEFAULT 0,
    auto_renew BOOLEAN DEFAULT FALSE,
    auto_renew_error VARCHAR(32),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    issuedate TIMESTAMP,
    onsite_checkout BOOLEAN DEFAULT FALSE,
    note TEXT,
    notedate TIMESTAMP,
    noteseen BOOLEAN
);

--changeset koha:004-2
CREATE TABLE IF NOT EXISTS old_issues (
    issue_id INT PRIMARY KEY,
    borrowernumber INT,
    itemnumber INT,
    date_due TIMESTAMP,
    branchcode VARCHAR(10),
    returndate TIMESTAMP,
    lastreneweddate TIMESTAMP,
    renewals INT DEFAULT 0,
    auto_renew BOOLEAN DEFAULT FALSE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    issuedate TIMESTAMP,
    onsite_checkout BOOLEAN DEFAULT FALSE,
    note TEXT
);

--changeset koha:004-3
CREATE TABLE IF NOT EXISTS reserves (
    reserve_id SERIAL PRIMARY KEY,
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber),
    reservedate DATE,
    biblionumber INT NOT NULL REFERENCES biblio(biblionumber),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    notificationdate DATE,
    reminderdate DATE,
    cancellationdate DATE,
    cancellation_reason VARCHAR(80),
    reservenotes TEXT,
    priority INT NOT NULL DEFAULT 1,
    found VARCHAR(1),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    itemnumber INT REFERENCES items(itemnumber),
    waitingdate DATE,
    expirationdate DATE,
    lowestpriority BOOLEAN DEFAULT FALSE,
    suspend BOOLEAN DEFAULT FALSE,
    suspend_until TIMESTAMP,
    itemtype VARCHAR(10),
    item_level_hold BOOLEAN DEFAULT FALSE,
    non_priority BOOLEAN DEFAULT FALSE,
    item_group_id INT REFERENCES item_groups(item_group_id)
);

--changeset koha:004-4
CREATE TABLE IF NOT EXISTS old_reserves (
    reserve_id INT PRIMARY KEY,
    borrowernumber INT,
    reservedate DATE,
    biblionumber INT,
    branchcode VARCHAR(10),
    cancellationdate DATE,
    reservenotes TEXT,
    priority INT DEFAULT 1,
    found VARCHAR(1),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    itemnumber INT,
    waitingdate DATE,
    expirationdate DATE
);

--changeset koha:004-5
CREATE TABLE IF NOT EXISTS reserve_groups (
    id SERIAL PRIMARY KEY,
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:004-6
CREATE TABLE IF NOT EXISTS bookings (
    booking_id SERIAL PRIMARY KEY,
    patron_id INT NOT NULL REFERENCES borrowers(borrowernumber),
    biblio_id INT NOT NULL REFERENCES biblio(biblionumber),
    item_id INT REFERENCES items(itemnumber),
    pickup_library_id VARCHAR(10) REFERENCES branches(branchcode),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'new',
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:004-7
CREATE TABLE IF NOT EXISTS return_claims (
    id SERIAL PRIMARY KEY,
    itemnumber INT NOT NULL REFERENCES items(itemnumber),
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber),
    notes TEXT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT REFERENCES borrowers(borrowernumber),
    updated_on TIMESTAMP,
    updated_by INT REFERENCES borrowers(borrowernumber),
    resolution VARCHAR(80),
    resolved_on TIMESTAMP,
    resolved_by INT REFERENCES borrowers(borrowernumber)
);

--changeset koha:004-8
CREATE TABLE IF NOT EXISTS recalls (
    recall_id SERIAL PRIMARY KEY,
    patron_id INT NOT NULL REFERENCES borrowers(borrowernumber),
    biblio_id INT NOT NULL REFERENCES biblio(biblionumber),
    item_id INT REFERENCES items(itemnumber),
    pickup_library_id VARCHAR(10) REFERENCES branches(branchcode),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP,
    completed BOOLEAN DEFAULT FALSE,
    completed_date TIMESTAMP,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'requested',
    item_level BOOLEAN DEFAULT FALSE,
    waiting_date TIMESTAMP
);

--changeset koha:004-9
CREATE TABLE IF NOT EXISTS issuingrules (
    categorycode VARCHAR(10),
    itemtype VARCHAR(10),
    branchcode VARCHAR(10),
    maxissueqty INT,
    maxonsiteissueqty INT,
    issuelength INT,
    lengthunit VARCHAR(10) DEFAULT 'days',
    renewalsallowed INT DEFAULT 0,
    renewalperiod INT,
    reservesallowed INT DEFAULT 0,
    holds_per_record INT DEFAULT 1,
    PRIMARY KEY (categorycode, itemtype, branchcode)
);

--changeset koha:004-10
CREATE TABLE IF NOT EXISTS article_requests (
    id SERIAL PRIMARY KEY,
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber),
    biblionumber INT NOT NULL REFERENCES biblio(biblionumber),
    itemnumber INT REFERENCES items(itemnumber),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    title TEXT,
    author TEXT,
    volume VARCHAR(255),
    issue VARCHAR(255),
    date VARCHAR(255),
    pages VARCHAR(255),
    chapters TEXT,
    patron_notes TEXT,
    status VARCHAR(80) DEFAULT 'PENDING',
    notes TEXT,
    format VARCHAR(30) DEFAULT 'PHOTOCOPY',
    urls TEXT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:004-11
CREATE TABLE IF NOT EXISTS stockrotation_rotas (
    rota_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    cyclical BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT FALSE
);

--changeset koha:004-12
CREATE TABLE IF NOT EXISTS stockrotation_stages (
    stage_id SERIAL PRIMARY KEY,
    rota_id INT NOT NULL REFERENCES stockrotation_rotas(rota_id) ON DELETE CASCADE,
    branchcode_id VARCHAR(10) REFERENCES branches(branchcode),
    duration INT DEFAULT 21,
    position INT NOT NULL
);

--changeset koha:004-13
CREATE TABLE IF NOT EXISTS branch_transfer_limits (
    limitid SERIAL PRIMARY KEY,
    tobranch VARCHAR(10) NOT NULL REFERENCES branches(branchcode),
    frombranch VARCHAR(10) NOT NULL REFERENCES branches(branchcode),
    itemtype VARCHAR(10),
    ccode VARCHAR(80)
);

--changeset koha:004-14
CREATE TABLE IF NOT EXISTS statistics (
    datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    branch VARCHAR(10),
    value DECIMAL(16,4),
    type VARCHAR(16),
    other TEXT,
    itemnumber INT,
    itemtype VARCHAR(10),
    location VARCHAR(80),
    borrowernumber INT,
    ccode VARCHAR(80)
);

