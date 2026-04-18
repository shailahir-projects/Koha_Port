package com.shailahir.koha.acquisitions.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.LateOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JDBC repository for late order operations.
 * Mirrors Koha::Acquisition::Orders->filter_by_lates() and $order->claim().
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class LateOrderRepository {

    private final JdbcTemplate jdbc;

    // ── filter_by_lates ────────────────────────────────────────────────────────

    /**
     * Returns orders that are late, based on delay or estimated delivery date range.
     * Mirrors Koha::Acquisition::Orders->filter_by_lates().
     * <p>
     * An order is "late" when:
     * <ul>
     *   <li>The basket is closed (closedate IS NOT NULL)</li>
     *   <li>The order is not yet fully received (quantityreceived < quantity)</li>
     *   <li>The order is not cancelled</li>
     *   <li>One of the following time conditions is met:
     *       <ul>
     *         <li>When {@code delay} is given: basket closedate is at least
     *             {@code delay} days ago (TODAY - closedate >= delay)</li>
     *         <li>When {@code estimatedFrom} is given: estimated_delivery_date >= estimatedFrom</li>
     *         <li>When {@code estimatedTo}   is given: estimated_delivery_date <= estimatedTo</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * @param delay          minimum days since basket closure (0 = no delay filter)
     * @param estimatedFrom  optional lower bound on estimated_delivery_date
     * @param estimatedTo    optional upper bound on estimated_delivery_date
     * @param booksellerid   optional vendor filter
     * @param branch         optional branch filter
     */
    public List<LateOrderDto> filterByLates(int delay, LocalDate estimatedFrom,
                                            LocalDate estimatedTo, Long booksellerid,
                                            String branch) {
        log.debug("Entering filterByLates - {}, {}, {}, {}, {}", delay, estimatedFrom, estimatedTo, booksellerid, branch);
        StringBuilder sql = new StringBuilder("""
                SELECT o.ordernumber, o.basketno, o.biblionumber,
                       o.quantity, o.quantityreceived,
                       o.ecost_tax_included, o.budget_id,
                       o.estimated_delivery_date,
                       o.claimed_date, o.claims_count,
                       b.basketname, b.closedate, b.booksellerid, b.branch,
                       bib.title, bib.author,
                       bi.isbn,
                       v.name  AS vendor_name,
                       aq.budget_name,
                       DATEDIFF(CURRENT_DATE, COALESCE(o.estimated_delivery_date, b.closedate))
                                AS days_late
                  FROM aqorders o
                  JOIN aqbasket b        ON b.basketno = o.basketno
                  LEFT JOIN biblio bib   ON bib.biblionumber = o.biblionumber
                  LEFT JOIN biblioitems bi ON bi.biblionumber = o.biblionumber
                  LEFT JOIN aqbooksellers v ON v.id = b.booksellerid
                  LEFT JOIN aqbudgets aq   ON aq.budget_id = o.budget_id
                 WHERE b.closedate IS NOT NULL
                   AND o.orderstatus NOT IN ('cancelled','complete')
                   AND (o.quantity > COALESCE(o.quantityreceived, 0))
                """);

        List<Object> params = new ArrayList<>();

        // Delay filter: TODAY - closedate >= delay days
        if (delay > 0) {
            sql.append(" AND DATEDIFF(CURRENT_DATE, b.closedate) >= ?");
            params.add(delay);
        }
        // Estimated delivery date range filters
        if (estimatedFrom != null) {
            sql.append(" AND o.estimated_delivery_date >= ?"); params.add(estimatedFrom);
        }
        if (estimatedTo != null) {
            sql.append(" AND o.estimated_delivery_date <= ?"); params.add(estimatedTo);
        }
        // Vendor filter
        if (booksellerid != null) {
            sql.append(" AND b.booksellerid = ?"); params.add(booksellerid);
        }
        // Branch filter
        if (branch != null && !branch.isBlank()) {
            sql.append(" AND b.branch = ?"); params.add(branch);
        }

        sql.append(" ORDER BY b.closedate, o.ordernumber");

        RowMapper<LateOrderDto> mapper = (rs, rn) -> LateOrderDto.builder()
                .ordernumber(rs.getLong("ordernumber"))
                .basketno(rs.getLong("basketno"))
                .basketname(rs.getString("basketname"))
                .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .isbn(rs.getString("isbn"))
                .quantity(rs.getInt("quantity"))
                .quantityreceived(rs.getInt("quantityreceived"))
                .ecostTaxIncluded(rs.getBigDecimal("ecost_tax_included"))
                .budgetId(rs.getObject("budget_id") != null ? rs.getLong("budget_id") : null)
                .budgetName(rs.getString("budget_name"))
                .booksellerid(rs.getLong("booksellerid"))
                .vendorName(rs.getString("vendor_name"))
                .closedate(rs.getObject("closedate", LocalDate.class))
                .estimatedDeliveryDate(rs.getObject("estimated_delivery_date", LocalDate.class))
                .daysLate(rs.getObject("days_late") != null ? rs.getLong("days_late") : null)
                .claimedDate(rs.getObject("claimed_date", LocalDate.class))
                .claimsCount(rs.getObject("claims_count") != null ? rs.getInt("claims_count") : 0)
                .build();

        return jdbc.query(sql.toString(), mapper, params.toArray());
    }

    // ── $order->claim() ────────────────────────────────────────────────────────

    /**
     * Marks an order as claimed by updating claimed_date and incrementing claims_count.
     * Mirrors Koha::Acquisition::Order->claim():
     * {@code $self->claimed_date(dt_from_string()); $self->claims_count($self->claims_count + 1);}
     */
    public void claimOrder(Long ordernumber) {
        log.debug("Entering claimOrder - {}", ordernumber);
        jdbc.update("""
                UPDATE aqorders
                   SET claimed_date  = CURRENT_DATE,
                       claims_count  = COALESCE(claims_count, 0) + 1
                 WHERE ordernumber   = ?
                """, ordernumber);
    }

    // ── Export data (lateorders-export.pl) ────────────────────────────────────

    /**
     * Returns enriched order rows for CSV export of selected late orders.
     * Mirrors the GetOrder() data gathered in lateorders-export.pl.
     * Includes: orderdate, latesince (closedate), estimateddeliverydate,
     * supplier info, title/author/isbn/publisher, unitprice, quantities,
     * subtotal, budget name, basket info, claims count + last claimed date,
     * internal note, vendor note.
     */
    public List<Map<String, Object>> getOrdersForExport(List<Long> ordernumbers) {
        log.debug("Entering getOrdersForExport - {}", ordernumbers);
        if (ordernumbers == null || ordernumbers.isEmpty()) return List.of();
        String placeholders = String.join(",", ordernumbers.stream().map(x -> "?").toList());
        String sql = """
                SELECT o.ordernumber, o.basketno, o.biblionumber,
                       o.quantity, o.quantityreceived,
                       o.ecost_tax_included AS unitpricesupplier,
                       o.budget_id, o.estimated_delivery_date,
                       o.claimed_date, o.claims_count,
                       o.order_internalnote, o.order_vendornote,
                       b.basketname, b.closedate AS latesince,
                       b.booksellerid,
                       bib.title, bib.author,
                       bi.isbn,
                       bi.publishercode AS publisher,
                       v.name AS supplier,
                       v.id   AS supplierid,
                       aq.budget_name AS budget,
                       (o.quantity - COALESCE(o.quantityreceived,0)) AS quantity_to_receive,
                       (o.ecost_tax_included * (o.quantity - COALESCE(o.quantityreceived,0)))
                            AS subtotal
                  FROM aqorders o
                  JOIN aqbasket b       ON b.basketno     = o.basketno
                  LEFT JOIN biblio bib  ON bib.biblionumber = o.biblionumber
                  LEFT JOIN biblioitems bi ON bi.biblionumber = o.biblionumber
                  LEFT JOIN aqbooksellers v ON v.id = b.booksellerid
                  LEFT JOIN aqbudgets aq   ON aq.budget_id = o.budget_id
                 WHERE o.ordernumber IN (
                """ + placeholders + ") ORDER BY o.ordernumber";
        return jdbc.queryForList(sql, ordernumbers.toArray());
    }

    // ── Claim alert letters ────────────────────────────────────────────────────

    /**
     * Returns available claim letter codes for the claimacquisition module.
     * Mirrors GetLetters({ module => "claimacquisition" }).
     */
    public List<Map<String, Object>> getClaimLetters() {
        log.debug("Entering getClaimLetters");
        return jdbc.queryForList("""
                SELECT code, name
                  FROM letter
                 WHERE module = 'claimacquisition'
                 ORDER BY name
                """);
    }

    // ── Vendor email lookup (for SendAlerts) ──────────────────────────────────

    /**
     * Returns the email address of the vendor for a given order.
     * Needed to validate that claim alerts can be sent (mirrors "no_email" error).
     */
    public String getVendorEmail(Long ordernumber) {
        log.debug("Entering getVendorEmail - {}", ordernumber);
        try {
            return jdbc.queryForObject("""
                    SELECT v.booksellerfax
                      FROM aqorders o
                      JOIN aqbasket b ON b.basketno = o.basketno
                      JOIN aqbooksellers v ON v.id = b.booksellerid
                     WHERE o.ordernumber = ?
                    """, String.class, ordernumber);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Logs a claim action in the action_logs table.
     */
    public void logClaim(Long ordernumber) {
        log.debug("Entering logClaim - {}", ordernumber);
        jdbc.update("""
                INSERT INTO action_logs (timestamp, user, module, action, object, info)
                VALUES (NOW(), 0, 'ACQUISITIONS', 'CLAIM_ORDER', ?, '')
                """, ordernumber);
    }
}

