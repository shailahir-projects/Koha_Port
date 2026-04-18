--liquibase formatted sql

--changeset koha:001-1
CREATE TABLE IF NOT EXISTS branches (
    branchcode VARCHAR(10) PRIMARY KEY,
    branchname VARCHAR(255) NOT NULL,
    branchaddress1 TEXT,
    branchaddress2 TEXT,
    branchaddress3 TEXT,
    branchzip VARCHAR(25),
    branchcity TEXT,
    branchstate TEXT,
    branchcountry TEXT,
    branchphone VARCHAR(25),
    branchfax VARCHAR(25),
    branchemail VARCHAR(100),
    branchreplyto VARCHAR(100),
    branchreturnpath VARCHAR(100),
    branchurl VARCHAR(200),
    branchnotes TEXT,
    opac_info TEXT,
    branchip VARCHAR(200),
    pickup_location BOOLEAN DEFAULT TRUE,
    public BOOLEAN DEFAULT TRUE,
    geolocation VARCHAR(255),
    marcorgcode VARCHAR(16),
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:001-2
CREATE TABLE IF NOT EXISTS categories (
    categorycode VARCHAR(10) PRIMARY KEY,
    description VARCHAR(255),
    enrolmentperiod INT,
    enrolmentperioddate DATE,
    upperagelimit INT,
    dateofbirthrequired INT,
    finetype VARCHAR(30),
    bulk BOOLEAN DEFAULT FALSE,
    enrolmentfee DECIMAL(28,6),
    overduenoticerequired BOOLEAN DEFAULT TRUE,
    reservefee DECIMAL(28,6),
    hidelostitems BOOLEAN DEFAULT FALSE,
    category_type VARCHAR(1) DEFAULT 'A',
    blockedadultcontent BOOLEAN DEFAULT FALSE,
    checkprevcheckout VARCHAR(7) DEFAULT 'inherit',
    default_privacy VARCHAR(10) DEFAULT 'default',
    reset_password BOOLEAN,
    change_password BOOLEAN,
    min_password_length INT,
    require_strong_password BOOLEAN,
    exclude_from_local_holds_priority BOOLEAN DEFAULT FALSE,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:001-3
CREATE TABLE IF NOT EXISTS currency (
    currency VARCHAR(10) PRIMARY KEY,
    symbol VARCHAR(5),
    isocode VARCHAR(5),
    rate DECIMAL(28,6),
    active BOOLEAN DEFAULT FALSE,
    archived BOOLEAN DEFAULT FALSE,
    p_sep_by_space BOOLEAN DEFAULT TRUE
);

--changeset koha:001-4
CREATE TABLE IF NOT EXISTS itemtypes (
    itemtype VARCHAR(10) PRIMARY KEY,
    description TEXT,
    rentalcharge DECIMAL(28,6),
    rentalcharge_daily DECIMAL(28,6),
    rentalcharge_hourly DECIMAL(28,6),
    defaultreplacecost DECIMAL(28,6),
    processfee DECIMAL(28,6),
    notforloan BOOLEAN DEFAULT FALSE,
    imageurl VARCHAR(200),
    summary TEXT,
    checkinmsg VARCHAR(255),
    checkinmsgtype VARCHAR(16) DEFAULT 'message',
    sip_media_type VARCHAR(3),
    hideinopac BOOLEAN DEFAULT FALSE,
    searchcategory VARCHAR(80),
    automatic_checkin BOOLEAN DEFAULT FALSE,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:001-5
CREATE TABLE IF NOT EXISTS cities (
    cityid SERIAL PRIMARY KEY,
    city_name VARCHAR(100) NOT NULL,
    city_state VARCHAR(100),
    city_zipcode VARCHAR(20),
    city_country VARCHAR(100)
);

--changeset koha:001-6
CREATE TABLE IF NOT EXISTS authorised_values (
    id SERIAL PRIMARY KEY,
    category VARCHAR(32) NOT NULL,
    authorised_value VARCHAR(80) NOT NULL,
    lib VARCHAR(200),
    lib_opac VARCHAR(200),
    imageurl VARCHAR(200)
);

--changeset koha:001-7
CREATE TABLE IF NOT EXISTS desks (
    desk_id SERIAL PRIMARY KEY,
    desk_name VARCHAR(100) NOT NULL,
    branchcode VARCHAR(10) REFERENCES branches(branchcode)
);

