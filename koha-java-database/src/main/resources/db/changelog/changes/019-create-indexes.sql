--liquibase formatted sql

--changeset koha:019-1
CREATE INDEX IF NOT EXISTS idx_biblio_title ON biblio(title);
CREATE INDEX IF NOT EXISTS idx_biblio_author ON biblio(author);
CREATE INDEX IF NOT EXISTS idx_items_biblionumber ON items(biblionumber);
CREATE INDEX IF NOT EXISTS idx_items_homebranch ON items(homebranch);
CREATE INDEX IF NOT EXISTS idx_items_holdingbranch ON items(holdingbranch);
CREATE INDEX IF NOT EXISTS idx_items_itype ON items(itype);
CREATE INDEX IF NOT EXISTS idx_items_location ON items(location);
CREATE INDEX IF NOT EXISTS idx_borrowers_cardnumber ON borrowers(cardnumber);
CREATE INDEX IF NOT EXISTS idx_borrowers_surname ON borrowers(surname);
CREATE INDEX IF NOT EXISTS idx_borrowers_branchcode ON borrowers(branchcode);
CREATE INDEX IF NOT EXISTS idx_borrowers_categorycode ON borrowers(categorycode);
CREATE INDEX IF NOT EXISTS idx_borrowers_email ON borrowers(email);
CREATE INDEX IF NOT EXISTS idx_borrowers_userid ON borrowers(userid);
CREATE INDEX IF NOT EXISTS idx_issues_borrowernumber ON issues(borrowernumber);
CREATE INDEX IF NOT EXISTS idx_issues_itemnumber ON issues(itemnumber);
CREATE INDEX IF NOT EXISTS idx_issues_date_due ON issues(date_due);
CREATE INDEX IF NOT EXISTS idx_reserves_borrowernumber ON reserves(borrowernumber);
CREATE INDEX IF NOT EXISTS idx_reserves_biblionumber ON reserves(biblionumber);
CREATE INDEX IF NOT EXISTS idx_reserves_branchcode ON reserves(branchcode);
CREATE INDEX IF NOT EXISTS idx_aqorders_basketno ON aqorders(basketno);
CREATE INDEX IF NOT EXISTS idx_aqorders_biblionumber ON aqorders(biblionumber);
CREATE INDEX IF NOT EXISTS idx_aqorders_budget_id ON aqorders(budget_id);
CREATE INDEX IF NOT EXISTS idx_aqbasket_booksellerid ON aqbasket(booksellerid);
CREATE INDEX IF NOT EXISTS idx_accountlines_borrowernumber ON accountlines(borrowernumber);
CREATE INDEX IF NOT EXISTS idx_serial_subscriptionid ON serial(subscriptionid);
CREATE INDEX IF NOT EXISTS idx_serial_biblionumber ON serial(biblionumber);
CREATE INDEX IF NOT EXISTS idx_background_jobs_status ON background_jobs(status);
CREATE INDEX IF NOT EXISTS idx_message_queue_status ON message_queue(status);
CREATE INDEX IF NOT EXISTS idx_illrequests_borrowernumber ON illrequests(borrowernumber);
CREATE INDEX IF NOT EXISTS idx_illrequests_status ON illrequests(status);
CREATE INDEX IF NOT EXISTS idx_search_filters_name ON search_filters(name);
CREATE INDEX IF NOT EXISTS idx_auth_header_authtypecode ON auth_header(authtypecode);

