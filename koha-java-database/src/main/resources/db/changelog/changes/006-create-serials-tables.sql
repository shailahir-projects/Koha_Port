--liquibase formatted sql

--changeset koha:006-1
CREATE TABLE IF NOT EXISTS subscription (
    subscriptionid SERIAL PRIMARY KEY,
    librarian INT REFERENCES borrowers(borrowernumber),
    startdate DATE,
    enddate DATE,
    aqbooksellerid INT REFERENCES aqbooksellers(id),
    cost DECIMAL(28,6),
    aqbudgetid INT REFERENCES aqbudgets(budget_id),
    biblionumber INT REFERENCES biblio(biblionumber),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    notes TEXT,
    internalnotes TEXT,
    callnumber TEXT,
    location VARCHAR(80),
    status VARCHAR(100) DEFAULT 'active',
    closed BOOLEAN DEFAULT FALSE,
    reneweddate DATE,
    itemtype VARCHAR(10),
    previousitemtype VARCHAR(10),
    mana_id INT,
    ccode VARCHAR(80),
    published_on_template TEXT
);

--changeset koha:006-2
CREATE TABLE IF NOT EXISTS serial (
    serialid SERIAL PRIMARY KEY,
    serialseq VARCHAR(100) NOT NULL,
    serialseq_x VARCHAR(100),
    serialseq_y VARCHAR(100),
    serialseq_z VARCHAR(100),
    subscriptionid INT NOT NULL REFERENCES subscription(subscriptionid),
    biblionumber INT NOT NULL REFERENCES biblio(biblionumber),
    status INT DEFAULT 1,
    planneddate DATE,
    notes TEXT,
    publisheddate DATE,
    publisheddatetext VARCHAR(100),
    claimdate DATE,
    claims_count INT DEFAULT 0,
    routingnotes TEXT
);

--changeset koha:006-3
CREATE TABLE IF NOT EXISTS subscription_frequencies (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    displayorder INT,
    unit VARCHAR(20)
);

--changeset koha:006-4
CREATE TABLE IF NOT EXISTS subscription_numberpatterns (
    id SERIAL PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    displayorder INT,
    description TEXT,
    numberingmethod VARCHAR(255) NOT NULL,
    label1 VARCHAR(255),
    add1 INT,
    every1 INT,
    whenmorethan1 INT,
    setto1 INT,
    numbering1 VARCHAR(255),
    label2 VARCHAR(255),
    add2 INT,
    every2 INT,
    whenmorethan2 INT,
    setto2 INT,
    numbering2 VARCHAR(255),
    label3 VARCHAR(255),
    add3 INT,
    every3 INT,
    whenmorethan3 INT,
    setto3 INT,
    numbering3 VARCHAR(255)
);

--changeset koha:006-5
CREATE TABLE IF NOT EXISTS subscriptionroutinglist (
    routingid SERIAL PRIMARY KEY,
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber),
    ranking INT,
    subscriptionid INT NOT NULL REFERENCES subscription(subscriptionid)
);

