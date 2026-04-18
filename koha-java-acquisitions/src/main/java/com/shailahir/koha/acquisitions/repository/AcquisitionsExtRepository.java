package com.shailahir.koha.acquisitions.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for acquisitions operations not covered by existing repositories.
 * Ports: modordernotes.pl, neworderempty.pl, newordersubscription.pl,
 *        newordersuggestion.pl, ordered.pl, orderreceive.pl, parcel.pl,
 *        parcels.pl, showorder.pl, spent.pl, transferorder.pl,
 *        uncertainprice.pl, vendor_issues.pl, vendors.pl, z3950_search.pl
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AcquisitionsExtRepository {

    private final JdbcTemplate jdbc;

    // ── Vendor summary (vendors.pl) ────────────────────────────────────────────

    private final RowMapper<VendorSummaryDto> VENDOR_SUMMARY_MAPPER = (rs, rn) -> VendorSummaryDto.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .active(rs.getBoolean("active"))
            .url(rs.getString("url"))
            .phone(rs.getString("phone"))
            .accountnumber(rs.getString("accountnumber"))
            .address1(rs.getString("address1"))
            .postal(rs.getString("postal"))
            .currency(rs.getString("currency"))
            .discount(rs.getBigDecimal("discount"))
            .basketCount(rs.getLong("basket_count"))
            .subscriptionCount(rs.getLong("subscription_count"))
            .contractsCount(rs.getLong("contracts_count"))
            .build();

    public List<VendorSummaryDto> findVendors(String name, Boolean active) {
        log.debug("Entering findVendors - {}, {}", name, active);
        StringBuilder sql = new StringBuilder("""
                SELECT v.id, v.name, v.active, v.url, v.phone, v.accountnumber,
                       v.address1, v.postal, v.currency, v.discount,
                       (SELECT COUNT(*) FROM aqbasket b WHERE b.booksellerid = v.id) AS basket_count,
                       (SELECT COUNT(*) FROM subscription s WHERE s.aqbooksellerid = v.id) AS subscription_count,
                       (SELECT COUNT(*) FROM aqcontract c WHERE c.booksellerid = v.id) AS contracts_count
                  FROM aqbooksellers v
                 WHERE 1=1
                """);
        if (name != null && !name.isBlank()) {
            sql.append(" AND v.name LIKE ?");
        }
        if (active != null) {
            sql.append(" AND v.active = ").append(active ? "1" : "0");
        }
        sql.append(" ORDER BY v.name");
        if (name != null && !name.isBlank()) {
            return jdbc.query(sql.toString(), VENDOR_SUMMARY_MAPPER, "%" + name + "%");
        }
        return jdbc.query(sql.toString(), VENDOR_SUMMARY_MAPPER);
    }

    // ── Order notes (modordernotes.pl) ─────────────────────────────────────────

    public int updateOrderNotes(Long ordernumber, String internalnote, String vendornote) {
        log.debug("Entering updateOrderNotes - {}, {}, {}", ordernumber, internalnote, vendornote);
        return jdbc.update(
                "UPDATE aqorders SET order_internalnote = ?, order_vendornote = ? WHERE ordernumber = ?",
                internalnote, vendornote, ordernumber);
    }

    // ── New order seed data (neworderempty.pl) ─────────────────────────────────

    public Optional<NewOrderSeedDto> getNewOrderSeed(Long basketno, Long biblionumber) {
        log.debug("Entering getNewOrderSeed - {}, {}", basketno, biblionumber);
        try {
            String sql = """
                    SELECT b.basketno, b.basketname, v.id AS booksellerid, v.name AS booksellername,
                           bib.biblionumber, bib.title, bib.author, bi.isbn,
                           bib.publishercode, bib.publicationyear
                      FROM aqbasket b
                      JOIN aqbooksellers v ON v.id = b.booksellerid
                 LEFT JOIN biblio bib ON bib.biblionumber = ?
                 LEFT JOIN biblioitems bi ON bi.biblionumber = bib.biblionumber
                     WHERE b.basketno = ?
                    """;
            return Optional.ofNullable(jdbc.queryForObject(sql, (rs, rn) -> NewOrderSeedDto.builder()
                    .basketno(rs.getLong("basketno"))
                    .basketname(rs.getString("basketname"))
                    .booksellerid(rs.getLong("booksellerid"))
                    .booksellername(rs.getString("booksellername"))
                    .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                    .title(rs.getString("title"))
                    .author(rs.getString("author"))
                    .isbn(rs.getString("isbn"))
                    .publishercode(rs.getString("publishercode"))
                    .publicationyear(rs.getString("publicationyear"))
                    .build(), biblionumber, basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── New order from subscription (newordersubscription.pl) ─────────────────

    public Optional<NewOrderSeedDto> getNewOrderSeedFromSubscription(Long basketno, Long subscriptionid) {
        log.debug("Entering getNewOrderSeedFromSubscription - {}, {}", basketno, subscriptionid);
        try {
            String sql = """
                    SELECT b.basketno, b.basketname, v.id AS booksellerid, v.name AS booksellername,
                           s.subscriptionid, bib.title AS subscription_title
                      FROM aqbasket b
                      JOIN aqbooksellers v ON v.id = b.booksellerid
                      JOIN subscription s ON s.subscriptionid = ?
                      JOIN biblio bib ON bib.biblionumber = s.biblionumber
                     WHERE b.basketno = ?
                    """;
            return Optional.ofNullable(jdbc.queryForObject(sql, (rs, rn) -> NewOrderSeedDto.builder()
                    .basketno(rs.getLong("basketno"))
                    .basketname(rs.getString("basketname"))
                    .booksellerid(rs.getLong("booksellerid"))
                    .booksellername(rs.getString("booksellername"))
                    .subscriptionid(rs.getLong("subscriptionid"))
                    .subscriptionTitle(rs.getString("subscription_title"))
                    .build(), subscriptionid, basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── New order from suggestion (newordersuggestion.pl) ─────────────────────

    public Optional<NewOrderSeedDto> getNewOrderSeedFromSuggestion(Long basketno, Long suggestionid) {
        log.debug("Entering getNewOrderSeedFromSuggestion - {}, {}", basketno, suggestionid);
        try {
            String sql = """
                    SELECT b.basketno, b.basketname, v.id AS booksellerid, v.name AS booksellername,
                           sg.suggestionid, sg.title AS suggested_title, sg.author AS suggested_author,
                           sg.isbn AS suggested_isbn, sg.publishercode AS suggested_publishercode,
                           sg.quantity AS suggested_quantity, sg.price AS suggested_price,
                           sg.budgetid AS suggested_budget_id
                      FROM aqbasket b
                      JOIN aqbooksellers v ON v.id = b.booksellerid
                      JOIN suggestions sg ON sg.suggestionid = ?
                     WHERE b.basketno = ?
                    """;
            return Optional.ofNullable(jdbc.queryForObject(sql, (rs, rn) -> NewOrderSeedDto.builder()
                    .basketno(rs.getLong("basketno"))
                    .basketname(rs.getString("basketname"))
                    .booksellerid(rs.getLong("booksellerid"))
                    .booksellername(rs.getString("booksellername"))
                    .suggestionid(rs.getLong("suggestionid"))
                    .suggestedTitle(rs.getString("suggested_title"))
                    .suggestedAuthor(rs.getString("suggested_author"))
                    .suggestedIsbn(rs.getString("suggested_isbn"))
                    .suggestedPublishercode(rs.getString("suggested_publishercode"))
                    .suggestedQuantity(rs.getObject("suggested_quantity") != null ? rs.getInt("suggested_quantity") : null)
                    .suggestedPrice(rs.getBigDecimal("suggested_price"))
                    .suggestedBudgetId(rs.getObject("suggested_budget_id") != null ? rs.getLong("suggested_budget_id") : null)
                    .build(), suggestionid, basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Ordered items per fund (ordered.pl) ───────────────────────────────────

    public List<OrderedItemDto> findOrderedByBudget(Long budgetId) {
        log.debug("Entering findOrderedByBudget - {}", budgetId);
        String sql = """
                SELECT o.ordernumber, o.biblionumber, bib.title, bib.author,
                       o.quantity, o.quantityreceived, o.ecost,
                       (o.quantity * COALESCE(o.ecost, 0)) AS total_ecost,
                       o.datecreated, o.basketno, bk.basketname,
                       v.name AS booksellername, o.orderstatus
                  FROM aqorders o
                  JOIN aqbasket bk ON bk.basketno = o.basketno
                  JOIN aqbooksellers v ON v.id = bk.booksellerid
             LEFT JOIN biblio bib ON bib.biblionumber = o.biblionumber
                 WHERE o.budget_id = ?
                   AND o.orderstatus NOT IN ('cancelled','complete')
                 ORDER BY o.datecreated DESC
                """;
        return jdbc.query(sql, (rs, rn) -> OrderedItemDto.builder()
                .ordernumber(rs.getLong("ordernumber"))
                .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .quantity(rs.getObject("quantity") != null ? rs.getInt("quantity") : null)
                .quantityreceived(rs.getObject("quantityreceived") != null ? rs.getInt("quantityreceived") : null)
                .ecost(rs.getBigDecimal("ecost"))
                .totalEcost(rs.getBigDecimal("total_ecost"))
                .datecreated(rs.getObject("datecreated", LocalDate.class))
                .basketno(rs.getLong("basketno"))
                .basketname(rs.getString("basketname"))
                .booksellername(rs.getString("booksellername"))
                .orderstatus(rs.getString("orderstatus"))
                .build(), budgetId);
    }

    // ── Parcel list for vendor (parcels.pl) ───────────────────────────────────

    public List<ParcelSummaryDto> findParcelsByVendor(Long booksellerid, String fromDate, String toDate) {
        log.debug("Entering findParcelsByVendor - {}, {}, {}", booksellerid, fromDate, toDate);
        StringBuilder sql = new StringBuilder("""
                SELECT i.invoiceid, i.invoicenumber, i.shipmentdate, i.billingdate, i.closedate,
                       SUM(o.quantity) AS total_quantity,
                       SUM(o.quantityreceived) AS total_received,
                       SUM(o.quantity * COALESCE(o.ecost, 0)) AS total_ecost
                  FROM aqinvoices i
             LEFT JOIN aqorders o ON o.invoiceid = i.invoiceid
                 WHERE i.booksellerid = ?
                """);
        if (fromDate != null && !fromDate.isBlank()) sql.append(" AND i.shipmentdate >= '").append(fromDate).append("'");
        if (toDate != null && !toDate.isBlank()) sql.append(" AND i.shipmentdate <= '").append(toDate).append("'");
        sql.append(" GROUP BY i.invoiceid ORDER BY i.shipmentdate DESC");
        return jdbc.query(sql.toString(), (rs, rn) -> ParcelSummaryDto.builder()
                .invoiceid(rs.getLong("invoiceid"))
                .invoicenumber(rs.getString("invoicenumber"))
                .shipmentdate(rs.getObject("shipmentdate", LocalDate.class))
                .billingdate(rs.getObject("billingdate", LocalDate.class))
                .closedate(rs.getObject("closedate", LocalDate.class))
                .totalQuantity(rs.getObject("total_quantity") != null ? rs.getInt("total_quantity") : null)
                .totalReceived(rs.getObject("total_received") != null ? rs.getInt("total_received") : null)
                .totalEcost(rs.getBigDecimal("total_ecost"))
                .build(), booksellerid);
    }

    // ── Single parcel detail (parcel.pl) ──────────────────────────────────────

    public Optional<ParcelDetailDto> findParcelById(Long invoiceid) {
        log.debug("Entering findParcelById - {}", invoiceid);
        try {
            String headerSql = """
                    SELECT i.invoiceid, i.invoicenumber, i.booksellerid, v.name AS booksellername,
                           i.shipmentdate, i.billingdate, i.closedate,
                           i.shipmentcost, i.shipmentcost_budgetid
                      FROM aqinvoices i
                      JOIN aqbooksellers v ON v.id = i.booksellerid
                     WHERE i.invoiceid = ?
                    """;
            ParcelDetailDto header = jdbc.queryForObject(headerSql, (rs, rn) -> ParcelDetailDto.builder()
                    .invoiceid(rs.getLong("invoiceid"))
                    .invoicenumber(rs.getString("invoicenumber"))
                    .booksellerid(rs.getLong("booksellerid"))
                    .booksellername(rs.getString("booksellername"))
                    .shipmentdate(rs.getObject("shipmentdate", LocalDate.class))
                    .billingdate(rs.getObject("billingdate", LocalDate.class))
                    .closedate(rs.getObject("closedate", LocalDate.class))
                    .shipmentcost(rs.getBigDecimal("shipmentcost"))
                    .shipmentcostBudgetid(rs.getObject("shipmentcost_budgetid") != null ? rs.getLong("shipmentcost_budgetid") : null)
                    .build(), invoiceid);

            // Fetch totals + orders (reuse ORDER_ROW_MAPPER via inline)
            String ordersSql = """
                    SELECT o.ordernumber, o.basketno, o.biblionumber, o.invoiceid, o.budget_id,
                           o.quantity, o.quantityreceived, o.currency, o.listprice, o.uncertainprice,
                           o.tax_rate_on_ordering, o.tax_rate_on_receiving, o.discount,
                           o.rrp, o.rrp_tax_excluded, o.rrp_tax_included,
                           o.ecost, o.ecost_tax_excluded, o.ecost_tax_included,
                           o.unitprice, o.unitprice_tax_excluded, o.unitprice_tax_included,
                           o.replacementprice, o.order_internalnote, o.order_vendornote,
                           o.sort1, o.sort2, o.subscriptionid, o.estimated_delivery_date,
                           o.datecreated, o.datereceived, o.orderstatus,
                           b.basketname
                      FROM aqorders o
                      JOIN aqbasket b ON b.basketno = o.basketno
                     WHERE o.invoiceid = ?
                     ORDER BY o.ordernumber
                    """;
            List<OrderDto> orders = jdbc.query(ordersSql, (rs, rn) -> OrderDto.builder()
                    .ordernumber(rs.getLong("ordernumber"))
                    .basketno(rs.getLong("basketno"))
                    .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                    .invoiceid(rs.getObject("invoiceid") != null ? rs.getLong("invoiceid") : null)
                    .budgetId(rs.getLong("budget_id"))
                    .quantity(rs.getObject("quantity") != null ? rs.getInt("quantity") : null)
                    .quantityreceived(rs.getObject("quantityreceived") != null ? rs.getInt("quantityreceived") : null)
                    .currency(rs.getString("currency"))
                    .listprice(rs.getBigDecimal("listprice"))
                    .uncertainprice(rs.getBoolean("uncertainprice"))
                    .ecost(rs.getBigDecimal("ecost"))
                    .unitprice(rs.getBigDecimal("unitprice"))
                    .replacementprice(rs.getBigDecimal("replacementprice"))
                    .orderInternalnote(rs.getString("order_internalnote"))
                    .orderVendornote(rs.getString("order_vendornote"))
                    .estimatedDeliveryDate(rs.getObject("estimated_delivery_date", LocalDate.class))
                    .datecreated(rs.getObject("datecreated") != null ? rs.getTimestamp("datecreated").toLocalDateTime() : null)
                    .datereceived(rs.getObject("datereceived", LocalDate.class))
                    .orderstatus(rs.getString("orderstatus"))
                    .basketname(rs.getString("basketname"))
                    .build(), invoiceid);

            int totalQty = orders.stream().mapToInt(o -> o.getQuantity() != null ? o.getQuantity() : 0).sum();
            int totalRcv = orders.stream().mapToInt(o -> o.getQuantityreceived() != null ? o.getQuantityreceived() : 0).sum();
            BigDecimal totalCost = orders.stream()
                    .map(o -> o.getEcost() != null && o.getQuantity() != null
                            ? o.getEcost().multiply(BigDecimal.valueOf(o.getQuantity()))
                            : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assert header != null;
            header.setTotalQuantity(totalQty);
            header.setTotalReceived(totalRcv);
            header.setTotalEcost(totalCost);
            header.setOrders(orders);
            return Optional.of(header);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Spent amounts per fund (spent.pl) ─────────────────────────────────────

    public List<SpentDto> findSpentByPeriod(Long budgetPeriodId) {
        log.debug("Entering findSpentByPeriod - {}", budgetPeriodId);
        String sql = """
                SELECT b.budget_id, b.budget_code, b.budget_name, b.budget_amount,
                       bp.budget_period_id, bp.budget_period_description,
                       COALESCE((SELECT SUM(o.unitprice * o.quantityreceived)
                                   FROM aqorders o
                                  WHERE o.budget_id = b.budget_id
                                    AND o.orderstatus = 'complete'), 0) AS budget_spent,
                       COALESCE((SELECT SUM(o.ecost * o.quantity)
                                   FROM aqorders o
                                  WHERE o.budget_id = b.budget_id
                                    AND o.orderstatus NOT IN ('cancelled','complete')), 0) AS budget_ordered
                  FROM aqbudgets b
                  JOIN aqbudgetperiods bp ON bp.budget_period_id = b.budget_period_id
                 WHERE b.budget_period_id = ?
                 ORDER BY b.budget_code
                """;
        return jdbc.query(sql, (rs, rn) -> {
            BigDecimal amount = rs.getBigDecimal("budget_amount");
            BigDecimal spent = rs.getBigDecimal("budget_spent");
            BigDecimal ordered = rs.getBigDecimal("budget_ordered");
            BigDecimal remaining = amount != null
                    ? amount.subtract(spent != null ? spent : BigDecimal.ZERO)
                            .subtract(ordered != null ? ordered : BigDecimal.ZERO)
                    : BigDecimal.ZERO;
            return SpentDto.builder()
                    .budgetId(rs.getLong("budget_id"))
                    .budgetCode(rs.getString("budget_code"))
                    .budgetName(rs.getString("budget_name"))
                    .budgetAmount(amount)
                    .budgetSpent(spent)
                    .budgetOrdered(ordered)
                    .budgetRemaining(remaining)
                    .budgetPeriodId(rs.getLong("budget_period_id"))
                    .budgetPeriodDescription(rs.getString("budget_period_description"))
                    .build();
        }, budgetPeriodId);
    }

    // ── Transfer order (transferorder.pl) ─────────────────────────────────────

    public int transferOrder(Long ordernumber, Long toBasketno) {
        log.debug("Entering transferOrder - {}, {}", ordernumber, toBasketno);
        return jdbc.update(
                "UPDATE aqorders SET basketno = ? WHERE ordernumber = ?",
                toBasketno, ordernumber);
    }

    // ── Uncertain prices (uncertainprice.pl) ──────────────────────────────────

    public List<OrderDto> findUncertainPriceOrders(Long booksellerid) {
        log.debug("Entering findUncertainPriceOrders - {}", booksellerid);
        StringBuilder sql = new StringBuilder("""
                SELECT o.ordernumber, o.basketno, o.biblionumber, o.invoiceid, o.budget_id,
                       o.quantity, o.quantityreceived, o.currency, o.listprice, o.uncertainprice,
                       o.tax_rate_on_ordering, o.tax_rate_on_receiving, o.discount,
                       o.rrp, o.rrp_tax_excluded, o.rrp_tax_included,
                       o.ecost, o.ecost_tax_excluded, o.ecost_tax_included,
                       o.unitprice, o.unitprice_tax_excluded, o.unitprice_tax_included,
                       o.replacementprice, o.order_internalnote, o.order_vendornote,
                       o.sort1, o.sort2, o.subscriptionid, o.estimated_delivery_date,
                       o.datecreated, o.datereceived, o.orderstatus, b.basketname
                  FROM aqorders o
                  JOIN aqbasket b ON b.basketno = o.basketno
                 WHERE o.uncertainprice = 1
                   AND o.orderstatus NOT IN ('cancelled','complete')
                """);
        if (booksellerid != null) {
            sql.append(" AND b.booksellerid = ").append(booksellerid);
        }
        sql.append(" ORDER BY o.ordernumber DESC");
        return jdbc.query(sql.toString(), (rs, rn) -> OrderDto.builder()
                .ordernumber(rs.getLong("ordernumber"))
                .basketno(rs.getLong("basketno"))
                .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                .invoiceid(rs.getObject("invoiceid") != null ? rs.getLong("invoiceid") : null)
                .budgetId(rs.getLong("budget_id"))
                .quantity(rs.getObject("quantity") != null ? rs.getInt("quantity") : null)
                .currency(rs.getString("currency"))
                .listprice(rs.getBigDecimal("listprice"))
                .uncertainprice(rs.getBoolean("uncertainprice"))
                .ecost(rs.getBigDecimal("ecost"))
                .unitprice(rs.getBigDecimal("unitprice"))
                .estimatedDeliveryDate(rs.getObject("estimated_delivery_date", LocalDate.class))
                .orderstatus(rs.getString("orderstatus"))
                .basketname(rs.getString("basketname"))
                .build());
    }

    // ── Vendor issues / claims (vendor_issues.pl) ─────────────────────────────

    public List<VendorIssueDto> findVendorIssues(Long booksellerid) {
        log.debug("Entering findVendorIssues - {}", booksellerid);
        String sql = """
                SELECT vi.issue_id, vi.booksellerid, v.name AS booksellername,
                       vi.type, vi.title, vi.issue_date, vi.notes
                  FROM aqbookseller_issues vi
                  JOIN aqbooksellers v ON v.id = vi.booksellerid
                 WHERE vi.booksellerid = ?
                 ORDER BY vi.issue_date DESC
                """;
        return jdbc.query(sql, (rs, rn) -> VendorIssueDto.builder()
                .issueId(rs.getLong("issue_id"))
                .booksellerid(rs.getLong("booksellerid"))
                .booksellername(rs.getString("booksellername"))
                .type(rs.getString("type"))
                .title(rs.getString("title"))
                .issueDate(rs.getObject("issue_date", LocalDate.class))
                .notes(rs.getString("notes"))
                .build(), booksellerid);
    }

    // ── Order receive context (orderreceive.pl) ────────────────────────────────

    public Optional<OrderDto> findOrderForReceive(Long ordernumber) {
        log.debug("Entering findOrderForReceive - {}", ordernumber);
        try {
            String sql = """
                    SELECT o.ordernumber, o.basketno, o.biblionumber, o.invoiceid, o.budget_id,
                           o.quantity, o.quantityreceived, o.currency, o.listprice, o.uncertainprice,
                           o.tax_rate_on_ordering, o.tax_rate_on_receiving, o.discount,
                           o.rrp, o.rrp_tax_excluded, o.rrp_tax_included,
                           o.ecost, o.ecost_tax_excluded, o.ecost_tax_included,
                           o.unitprice, o.unitprice_tax_excluded, o.unitprice_tax_included,
                           o.replacementprice, o.order_internalnote, o.order_vendornote,
                           o.sort1, o.sort2, o.subscriptionid, o.estimated_delivery_date,
                           o.datecreated, o.datereceived, o.orderstatus, b.basketname
                      FROM aqorders o
                      JOIN aqbasket b ON b.basketno = o.basketno
                     WHERE o.ordernumber = ?
                    """;
            return Optional.ofNullable(jdbc.queryForObject(sql, (rs, rn) -> OrderDto.builder()
                    .ordernumber(rs.getLong("ordernumber"))
                    .basketno(rs.getLong("basketno"))
                    .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                    .invoiceid(rs.getObject("invoiceid") != null ? rs.getLong("invoiceid") : null)
                    .budgetId(rs.getLong("budget_id"))
                    .quantity(rs.getObject("quantity") != null ? rs.getInt("quantity") : null)
                    .quantityreceived(rs.getObject("quantityreceived") != null ? rs.getInt("quantityreceived") : null)
                    .currency(rs.getString("currency"))
                    .listprice(rs.getBigDecimal("listprice"))
                    .uncertainprice(rs.getBoolean("uncertainprice"))
                    .taxRateOnOrdering(rs.getBigDecimal("tax_rate_on_ordering"))
                    .taxRateOnReceiving(rs.getBigDecimal("tax_rate_on_receiving"))
                    .discount(rs.getBigDecimal("discount"))
                    .rrp(rs.getBigDecimal("rrp"))
                    .rrpTaxExcluded(rs.getBigDecimal("rrp_tax_excluded"))
                    .rrpTaxIncluded(rs.getBigDecimal("rrp_tax_included"))
                    .ecost(rs.getBigDecimal("ecost"))
                    .ecostTaxExcluded(rs.getBigDecimal("ecost_tax_excluded"))
                    .ecostTaxIncluded(rs.getBigDecimal("ecost_tax_included"))
                    .unitprice(rs.getBigDecimal("unitprice"))
                    .unitpriceTaxExcluded(rs.getBigDecimal("unitprice_tax_excluded"))
                    .unitpriceTaxIncluded(rs.getBigDecimal("unitprice_tax_included"))
                    .replacementprice(rs.getBigDecimal("replacementprice"))
                    .orderInternalnote(rs.getString("order_internalnote"))
                    .orderVendornote(rs.getString("order_vendornote"))
                    .subscriptionid(rs.getObject("subscriptionid") != null ? rs.getLong("subscriptionid") : null)
                    .estimatedDeliveryDate(rs.getObject("estimated_delivery_date", LocalDate.class))
                    .datecreated(rs.getObject("datecreated") != null ? rs.getTimestamp("datecreated").toLocalDateTime() : null)
                    .datereceived(rs.getObject("datereceived", LocalDate.class))
                    .orderstatus(rs.getString("orderstatus"))
                    .basketname(rs.getString("basketname"))
                    .build(), ordernumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

