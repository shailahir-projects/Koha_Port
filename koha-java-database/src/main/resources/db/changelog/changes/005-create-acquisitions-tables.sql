--liquibase formatted sql

--changeset koha:005-1
CREATE TABLE IF NOT EXISTS aqbooksellers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address1 TEXT,
    address2 TEXT,
    address3 TEXT,
    address4 TEXT,
    phone VARCHAR(30),
    accountnumber TEXT,
    notes TEXT,
    postal TEXT,
    url TEXT,
    active BOOLEAN DEFAULT TRUE,
    listprice VARCHAR(10) REFERENCES currency(currency),
    invoiceprice VARCHAR(10) REFERENCES currency(currency),
    gstreg BOOLEAN DEFAULT FALSE,
    listincgst BOOLEAN DEFAULT FALSE,
    invoiceincgst BOOLEAN DEFAULT FALSE,
    tax_rate DECIMAL(6,4),
    discount DECIMAL(6,4),
    fax VARCHAR(50),
    deliverytime INT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:005-2
CREATE TABLE IF NOT EXISTS aqbasket (
    basketno SERIAL PRIMARY KEY,
    basketname VARCHAR(50),
    note TEXT,
    booksellernote TEXT,
    contractnumber INT,
    creationdate DATE,
    closedate DATE,
    booksellerid INT NOT NULL REFERENCES aqbooksellers(id),
    authorisedby INT REFERENCES borrowers(borrowernumber),
    booksellerinvoicenumber TEXT,
    basketgroupid INT,
    deliveryplace VARCHAR(10),
    billingplace VARCHAR(10),
    branch VARCHAR(10) REFERENCES branches(branchcode),
    is_standing BOOLEAN DEFAULT FALSE,
    create_items VARCHAR(10) DEFAULT 'ordering'
);

--changeset koha:005-3
CREATE TABLE IF NOT EXISTS aqbasketgroups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    closed BOOLEAN DEFAULT FALSE,
    booksellerid INT NOT NULL REFERENCES aqbooksellers(id),
    deliveryplace VARCHAR(10),
    freedeliveryplace TEXT,
    deliverycomment VARCHAR(255),
    billingplace VARCHAR(10)
);

--changeset koha:005-4
CREATE TABLE IF NOT EXISTS aqbudgetperiods (
    budget_period_id SERIAL PRIMARY KEY,
    budget_period_startdate DATE NOT NULL,
    budget_period_enddate DATE NOT NULL,
    budget_period_active BOOLEAN DEFAULT FALSE,
    budget_period_description VARCHAR(80),
    budget_period_total DECIMAL(28,6),
    budget_period_locked BOOLEAN DEFAULT FALSE,
    sort1_authcat VARCHAR(10),
    sort2_authcat VARCHAR(10)
);

--changeset koha:005-5
CREATE TABLE IF NOT EXISTS aqbudgets (
    budget_id SERIAL PRIMARY KEY,
    budget_parent_id INT,
    budget_code VARCHAR(30),
    budget_name VARCHAR(80),
    budget_branchcode VARCHAR(10) REFERENCES branches(branchcode),
    budget_amount DECIMAL(28,6) DEFAULT 0,
    budget_encumb DECIMAL(28,6) DEFAULT 0,
    budget_expend DECIMAL(28,6) DEFAULT 0,
    budget_notes TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    budget_period_id INT REFERENCES aqbudgetperiods(budget_period_id),
    sort1_authcat VARCHAR(80),
    sort2_authcat VARCHAR(80),
    budget_owner_id INT REFERENCES borrowers(borrowernumber),
    budget_permission INT DEFAULT 0
);

--changeset koha:005-6
CREATE TABLE IF NOT EXISTS aqorders (
    ordernumber SERIAL PRIMARY KEY,
    biblionumber INT REFERENCES biblio(biblionumber),
    entrydate DATE,
    quantity INT,
    listprice DECIMAL(28,6),
    datereceived DATE,
    invoiceid INT,
    freight DECIMAL(28,6),
    unitprice DECIMAL(28,6),
    unitprice_tax_excluded DECIMAL(28,6),
    unitprice_tax_included DECIMAL(28,6),
    quantityreceived INT DEFAULT 0,
    created_by INT REFERENCES borrowers(borrowernumber),
    datecancellationprinted TIMESTAMP,
    cancellationreason TEXT,
    order_internalnote TEXT,
    order_vendornote TEXT,
    purchaseordernumber TEXT,
    basketno INT REFERENCES aqbasket(basketno),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rrp DECIMAL(13,2),
    rrp_tax_excluded DECIMAL(28,6),
    rrp_tax_included DECIMAL(28,6),
    ecost DECIMAL(13,2),
    ecost_tax_excluded DECIMAL(28,6),
    ecost_tax_included DECIMAL(28,6),
    tax_rate_bak DECIMAL(6,4),
    tax_rate_on_ordering DECIMAL(6,4),
    tax_rate_on_receiving DECIMAL(6,4),
    tax_value_bak DECIMAL(28,6),
    tax_value_on_ordering DECIMAL(28,6),
    tax_value_on_receiving DECIMAL(28,6),
    discount DECIMAL(6,4),
    budget_id INT REFERENCES aqbudgets(budget_id),
    uncertainprice BOOLEAN DEFAULT FALSE,
    subscriptionid INT,
    orderstatus VARCHAR(16) DEFAULT 'new',
    line_item_id VARCHAR(35),
    suppliers_reference_number VARCHAR(35),
    suppliers_reference_qualifier VARCHAR(3),
    suppliers_report TEXT
);

--changeset koha:005-7
CREATE TABLE IF NOT EXISTS aqinvoices (
    invoiceid SERIAL PRIMARY KEY,
    invoicenumber TEXT NOT NULL,
    booksellerid INT NOT NULL REFERENCES aqbooksellers(id),
    shipmentdate DATE,
    billingdate DATE,
    closedate DATE,
    shipmentcost DECIMAL(28,6),
    shipmentcost_budgetid INT REFERENCES aqbudgets(budget_id),
    message_id INT
);

--changeset koha:005-8
CREATE TABLE IF NOT EXISTS aqinvoice_adjustments (
    adjustment_id SERIAL PRIMARY KEY,
    invoiceid INT NOT NULL REFERENCES aqinvoices(invoiceid) ON DELETE CASCADE,
    adjustment DECIMAL(28,6),
    reason VARCHAR(80),
    note TEXT,
    budget_id INT REFERENCES aqbudgets(budget_id),
    encumber_open BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset koha:005-9
CREATE TABLE IF NOT EXISTS aqcontract (
    contractnumber SERIAL PRIMARY KEY,
    contractstartdate DATE,
    contractenddate DATE,
    contractname VARCHAR(50),
    contractdescription TEXT,
    booksellerid INT NOT NULL REFERENCES aqbooksellers(id)
);

--changeset koha:005-10
CREATE TABLE IF NOT EXISTS aqorders_items (
    ordernumber INT REFERENCES aqorders(ordernumber) ON DELETE CASCADE,
    itemnumber INT REFERENCES items(itemnumber),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ordernumber, itemnumber)
);

--changeset koha:005-11
CREATE TABLE IF NOT EXISTS aqorder_users (
    ordernumber INT REFERENCES aqorders(ordernumber) ON DELETE CASCADE,
    borrowernumber INT REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    PRIMARY KEY (ordernumber, borrowernumber)
);

--changeset koha:005-12
CREATE TABLE IF NOT EXISTS aqbasketusers (
    basketno INT REFERENCES aqbasket(basketno) ON DELETE CASCADE,
    borrowernumber INT REFERENCES borrowers(borrowernumber) ON DELETE CASCADE,
    PRIMARY KEY (basketno, borrowernumber)
);

--changeset koha:005-13
CREATE TABLE IF NOT EXISTS aqbookseller_issues (
    issue_id SERIAL PRIMARY KEY,
    vendor_id INT NOT NULL REFERENCES aqbooksellers(id) ON DELETE CASCADE,
    type VARCHAR(20),
    started_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_on TIMESTAMP,
    notes TEXT
);

--changeset koha:005-14
CREATE TABLE IF NOT EXISTS edifact_messages (
    id SERIAL PRIMARY KEY,
    message_type VARCHAR(10) NOT NULL,
    transfer_date DATE,
    vendor_id INT REFERENCES aqbooksellers(id),
    edi_acct INT,
    status TEXT,
    basketno INT REFERENCES aqbasket(basketno),
    raw_msg TEXT,
    filename TEXT,
    deleted BOOLEAN DEFAULT FALSE
);

--changeset koha:005-15
CREATE TABLE IF NOT EXISTS vendor_edi_accounts (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    host VARCHAR(40),
    username VARCHAR(40),
    password VARCHAR(40),
    upload_directory TEXT,
    download_directory TEXT,
    last_activity DATE,
    vendor_id INT REFERENCES aqbooksellers(id),
    san VARCHAR(20),
    transport VARCHAR(6) DEFAULT 'FTP',
    quotes_enabled BOOLEAN DEFAULT FALSE,
    invoices_enabled BOOLEAN DEFAULT FALSE,
    orders_enabled BOOLEAN DEFAULT FALSE,
    responses_enabled BOOLEAN DEFAULT FALSE,
    auto_orders BOOLEAN DEFAULT FALSE,
    id_code_qualifier VARCHAR(3) DEFAULT '14',
    plugin VARCHAR(256)
);

--changeset koha:005-16
CREATE TABLE IF NOT EXISTS suggestions (
    suggestionid SERIAL PRIMARY KEY,
    suggestedby INT REFERENCES borrowers(borrowernumber),
    suggesteddate DATE DEFAULT CURRENT_DATE,
    managedby INT REFERENCES borrowers(borrowernumber),
    manageddate DATE,
    acceptedby INT REFERENCES borrowers(borrowernumber),
    accepteddate DATE,
    rejectedby INT REFERENCES borrowers(borrowernumber),
    rejecteddate DATE,
    lastmodificationby INT REFERENCES borrowers(borrowernumber),
    lastmodificationdate DATE,
    status VARCHAR(10) DEFAULT 'ASKED',
    archived BOOLEAN DEFAULT FALSE,
    note TEXT,
    author VARCHAR(80),
    title VARCHAR(255),
    copyrightdate INT,
    publishercode VARCHAR(255),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    volumedesc VARCHAR(255),
    publicationyear INT,
    place VARCHAR(255),
    isbn VARCHAR(30),
    biblionumber INT REFERENCES biblio(biblionumber),
    reason TEXT,
    patronreason TEXT,
    budgetid INT REFERENCES aqbudgets(budget_id),
    branchcode VARCHAR(10) REFERENCES branches(branchcode),
    collectiontitle TEXT,
    itemtype VARCHAR(30),
    quantity INT DEFAULT 1,
    currency VARCHAR(10),
    price DECIMAL(28,6),
    total DECIMAL(28,6)
);

--changeset koha:005-17
CREATE TABLE IF NOT EXISTS quotes (
    id SERIAL PRIMARY KEY,
    source TEXT,
    text TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

