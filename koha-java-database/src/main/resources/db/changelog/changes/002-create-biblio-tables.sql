--liquibase formatted sql

--changeset koha:002-1
CREATE TABLE IF NOT EXISTS biblio (
    biblionumber SERIAL PRIMARY KEY,
    frameworkcode VARCHAR(4) DEFAULT '',
    author TEXT,
    title TEXT,
    unititle TEXT,
    notes TEXT,
    serial BOOLEAN DEFAULT FALSE,
    seriestitle TEXT,
    copyrightdate INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    datecreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    abstract TEXT,
    medium VARCHAR(255),
    subtitle TEXT,
    part_number TEXT,
    part_name TEXT
);

--changeset koha:002-2
CREATE TABLE IF NOT EXISTS biblioitems (
    biblioitemnumber SERIAL PRIMARY KEY,
    biblionumber INT NOT NULL REFERENCES biblio(biblionumber),
    volume TEXT,
    number TEXT,
    itemtype VARCHAR(10) REFERENCES itemtypes(itemtype),
    isbn TEXT,
    issn TEXT,
    ean TEXT,
    publicationyear TEXT,
    publishercode VARCHAR(255),
    volumedate DATE,
    volumedesc TEXT,
    collectiontitle TEXT,
    collectionissn TEXT,
    collectionvolume TEXT,
    editionstatement TEXT,
    editionresponsibility TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    illus VARCHAR(255),
    pages VARCHAR(255),
    notes TEXT,
    size VARCHAR(255),
    place VARCHAR(255),
    lccn VARCHAR(25),
    url TEXT,
    cn_source VARCHAR(10),
    cn_class VARCHAR(30),
    cn_item VARCHAR(10),
    cn_suffix VARCHAR(10),
    cn_sort VARCHAR(255),
    agerestriction VARCHAR(255),
    totalissues INT
);

--changeset koha:002-3
CREATE TABLE IF NOT EXISTS deletedbiblio (
    biblionumber INT PRIMARY KEY,
    frameworkcode VARCHAR(4) DEFAULT '',
    author TEXT,
    title TEXT,
    notes TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    datecreated TIMESTAMP
);

--changeset koha:002-4
CREATE TABLE IF NOT EXISTS items (
    itemnumber SERIAL PRIMARY KEY,
    biblionumber INT NOT NULL REFERENCES biblio(biblionumber),
    biblioitemnumber INT REFERENCES biblioitems(biblioitemnumber),
    barcode VARCHAR(20) UNIQUE,
    dateaccessioned DATE,
    booksellerid TEXT,
    homebranch VARCHAR(10) REFERENCES branches(branchcode),
    holdingbranch VARCHAR(10) REFERENCES branches(branchcode),
    price DECIMAL(8,2),
    replacementprice DECIMAL(8,2),
    replacementpricedate DATE,
    datelastborrowed DATE,
    datelastseen DATE,
    stack BOOLEAN,
    notforloan INT DEFAULT 0,
    damaged INT DEFAULT 0,
    damaged_on TIMESTAMP,
    itemlost INT DEFAULT 0,
    itemlost_on TIMESTAMP,
    withdrawn INT DEFAULT 0,
    withdrawn_on TIMESTAMP,
    itemcallnumber VARCHAR(255),
    coded_location_qualifier VARCHAR(10),
    issues INT DEFAULT 0,
    renewals INT DEFAULT 0,
    reserves INT DEFAULT 0,
    restricted INT,
    itemnotes TEXT,
    itemnotes_nonpublic TEXT,
    holdingbranch_old VARCHAR(10),
    paidfor TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    location VARCHAR(80),
    permanent_location VARCHAR(80),
    onloan DATE,
    cn_source VARCHAR(10),
    cn_sort VARCHAR(255),
    ccode VARCHAR(80),
    materials TEXT,
    uri TEXT,
    itype VARCHAR(10) REFERENCES itemtypes(itemtype),
    more_subfields_xml TEXT,
    enumchron TEXT,
    copynumber VARCHAR(32),
    stocknumber VARCHAR(32),
    new_status VARCHAR(32),
    exclude_from_local_holds_priority BOOLEAN DEFAULT FALSE,
    bookable BOOLEAN DEFAULT FALSE
);

--changeset koha:002-5
CREATE TABLE IF NOT EXISTS auth_header (
    authid SERIAL PRIMARY KEY,
    authtypecode VARCHAR(10) NOT NULL,
    datecreated DATE,
    modification_date DATE,
    origincode VARCHAR(20),
    authtrees TEXT,
    marcxml TEXT,
    linkid INT,
    heading VARCHAR(255),
    heading_type VARCHAR(1),
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:002-6
CREATE TABLE IF NOT EXISTS record_sources (
    record_source_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    can_be_edited BOOLEAN DEFAULT TRUE
);

--changeset koha:002-7
CREATE TABLE IF NOT EXISTS item_groups (
    item_group_id SERIAL PRIMARY KEY,
    biblio_id INT NOT NULL REFERENCES biblio(biblionumber),
    display_title VARCHAR(255),
    description TEXT,
    display_order INT DEFAULT 0,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:002-8
CREATE TABLE IF NOT EXISTS item_group_items (
    item_group_id INT NOT NULL REFERENCES item_groups(item_group_id) ON DELETE CASCADE,
    item_id INT NOT NULL REFERENCES items(itemnumber) ON DELETE CASCADE,
    PRIMARY KEY (item_group_id, item_id)
);

--changeset koha:002-9
CREATE TABLE IF NOT EXISTS ratings (
    borrowernumber INT NOT NULL,
    biblionumber INT NOT NULL REFERENCES biblio(biblionumber),
    rating_value INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (borrowernumber, biblionumber)
);

--changeset koha:002-10
CREATE TABLE IF NOT EXISTS marc_import_batches (
    import_batch_id SERIAL PRIMARY KEY,
    matcher_id INT,
    template_id INT,
    branchcode VARCHAR(10),
    num_records INT DEFAULT 0,
    num_items INT DEFAULT 0,
    upload_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    overlay_action VARCHAR(50) DEFAULT 'create_new',
    nomatch_action VARCHAR(50) DEFAULT 'create_new',
    item_action VARCHAR(50) DEFAULT 'always_add',
    import_status VARCHAR(50) DEFAULT 'staging',
    batch_type VARCHAR(50) DEFAULT 'batch',
    record_type VARCHAR(50) DEFAULT 'biblio',
    file_name VARCHAR(100),
    comments TEXT,
    profile_id INT
);

--changeset koha:002-11
CREATE TABLE IF NOT EXISTS import_batches (
    import_batch_id SERIAL PRIMARY KEY,
    import_status VARCHAR(50) DEFAULT 'staging',
    file_name VARCHAR(100),
    comments TEXT,
    upload_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    num_records INT DEFAULT 0,
    profile_id INT
);

