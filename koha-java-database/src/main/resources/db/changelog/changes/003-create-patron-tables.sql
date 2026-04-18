--liquibase formatted sql

--changeset koha:003-1
CREATE TABLE IF NOT EXISTS borrowers (
    borrowernumber SERIAL PRIMARY KEY,
    cardnumber VARCHAR(32) UNIQUE,
    surname VARCHAR(255) NOT NULL,
    firstname VARCHAR(255),
    title VARCHAR(255),
    othernames VARCHAR(255),
    initials VARCHAR(255),
    streetnumber VARCHAR(10),
    streettype VARCHAR(50),
    address TEXT,
    address2 TEXT,
    city TEXT,
    state TEXT,
    zipcode VARCHAR(25),
    country VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    mobile VARCHAR(50),
    fax VARCHAR(50),
    emailpro VARCHAR(255),
    phonepro VARCHAR(50),
    B_streetnumber VARCHAR(10),
    B_streettype VARCHAR(50),
    B_address TEXT,
    B_address2 TEXT,
    B_city TEXT,
    B_state TEXT,
    B_zipcode VARCHAR(25),
    B_country VARCHAR(255),
    B_email VARCHAR(255),
    B_phone VARCHAR(50),
    dateofbirth DATE,
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    categorycode VARCHAR(10) REFERENCES categories(categorycode),
    dateenrolled DATE,
    dateexpiry DATE,
    date_renewed DATE,
    gonenoaddress BOOLEAN DEFAULT FALSE,
    lost BOOLEAN DEFAULT FALSE,
    debarred DATE,
    debarredcomment TEXT,
    contactname TEXT,
    contactfirstname TEXT,
    contacttitle VARCHAR(255),
    borrowernotes TEXT,
    relationship VARCHAR(100),
    sex VARCHAR(1),
    password VARCHAR(60),
    flags INT,
    userid VARCHAR(75) UNIQUE,
    opacnote TEXT,
    contactnote VARCHAR(255),
    sort1 VARCHAR(80),
    sort2 VARCHAR(80),
    altcontactfirstname TEXT,
    altcontactsurname TEXT,
    altcontactaddress1 TEXT,
    altcontactaddress2 TEXT,
    altcontactaddress3 TEXT,
    altcontactstate TEXT,
    altcontactzipcode TEXT,
    altcontactcountry TEXT,
    altcontactphone VARCHAR(50),
    smsalertnumber VARCHAR(50),
    sms_provider_id INT,
    privacy INT DEFAULT 1,
    privacy_guarantor_checkouts BOOLEAN DEFAULT FALSE,
    privacy_guarantor_fines BOOLEAN DEFAULT FALSE,
    checkprevcheckout VARCHAR(7) DEFAULT 'inherit',
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastseen TIMESTAMP,
    lang VARCHAR(25) DEFAULT 'default',
    login_attempts INT DEFAULT 0,
    overdrive_auth_token TEXT,
    anonymized BOOLEAN DEFAULT FALSE,
    autorenew_checkouts BOOLEAN DEFAULT TRUE,
    primary_contact_method VARCHAR(45)
);

--changeset koha:003-2
CREATE TABLE IF NOT EXISTS borrower_attribute_types (
    code VARCHAR(10) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    repeatable BOOLEAN DEFAULT FALSE,
    unique_id BOOLEAN DEFAULT FALSE,
    opac_display BOOLEAN DEFAULT FALSE,
    opac_editable BOOLEAN DEFAULT FALSE,
    staff_searchable BOOLEAN DEFAULT FALSE,
    authorised_value_category VARCHAR(32),
    display_checkout BOOLEAN DEFAULT FALSE,
    category_code VARCHAR(10),
    class VARCHAR(255) DEFAULT '',
    mandatory BOOLEAN DEFAULT FALSE
);

--changeset koha:003-3
CREATE TABLE IF NOT EXISTS borrower_attributes (
    id SERIAL PRIMARY KEY,
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    code VARCHAR(10) NOT NULL REFERENCES borrower_attribute_types(code),
    attribute VARCHAR(255)
);

--changeset koha:003-4
CREATE TABLE IF NOT EXISTS borrower_debarments (
    borrower_debarment_id SERIAL PRIMARY KEY,
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    expiration DATE,
    type VARCHAR(50) NOT NULL,
    comment TEXT,
    manager_id INT,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP
);

--changeset koha:003-5
CREATE TABLE IF NOT EXISTS borrower_modifications (
    verification_token VARCHAR(255) PRIMARY KEY,
    borrowernumber INT REFERENCES borrowers(borrowernumber),
    changed_fields TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:003-6
CREATE TABLE IF NOT EXISTS borrower_files (
    file_id SERIAL PRIMARY KEY,
    borrowernumber INT NOT NULL REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255) NOT NULL,
    file_description VARCHAR(255),
    file_content BYTEA NOT NULL,
    date_uploaded TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:003-7
CREATE TABLE IF NOT EXISTS patronimage (
    borrowernumber INT PRIMARY KEY REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    mimetype VARCHAR(15) NOT NULL,
    imagefile BYTEA NOT NULL
);

--changeset koha:003-8
CREATE TABLE IF NOT EXISTS club_holds (
    id SERIAL PRIMARY KEY,
    club_id INT NOT NULL,
    biblio_id INT NOT NULL REFERENCES biblio(biblionumber),
    item_id INT REFERENCES items(itemnumber),
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:003-9
CREATE TABLE IF NOT EXISTS discharges (
    discharge_id SERIAL PRIMARY KEY,
    borrower INT REFERENCES borrowers(borrowernumber),
    needed TIMESTAMP,
    validated TIMESTAMP
);

--changeset koha:003-10
CREATE TABLE IF NOT EXISTS virtualshelves (
    shelfnumber SERIAL PRIMARY KEY,
    shelfname VARCHAR(255),
    owner INT REFERENCES borrowers(borrowernumber),
    category INT DEFAULT 1,
    sortfield VARCHAR(16) DEFAULT 'title',
    lastmodified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    allow_change_from_owner BOOLEAN DEFAULT TRUE,
    allow_change_from_others BOOLEAN DEFAULT FALSE,
    allow_change_from_staff BOOLEAN DEFAULT FALSE,
    allow_change_from_permitted_staff BOOLEAN DEFAULT FALSE,
    public BOOLEAN DEFAULT FALSE
);

--changeset koha:003-11
CREATE TABLE IF NOT EXISTS housebound_profile (
    borrowernumber INT PRIMARY KEY REFERENCES borrowers(borrowernumber),
    day VARCHAR(10),
    frequency VARCHAR(10),
    fav_itemtypes TEXT,
    fav_subjects TEXT,
    fav_authors TEXT,
    referral TEXT,
    notes TEXT
);

