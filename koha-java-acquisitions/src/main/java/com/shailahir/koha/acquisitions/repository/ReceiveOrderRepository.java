package com.shailahir.koha.acquisitions.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC repository for order-receive operations.
 * Mirrors ModReceiveOrder(), populate_with_prices_for_receiving(),
 * and the item/suggestion updates in finishreceive.pl.
 */
@Repository
@RequiredArgsConstructor
public class ReceiveOrderRepository {

    private final JdbcTemplate jdbc;

    // ── Order lookup ───────────────────────────────────────────────────────────

    public Optional<Map<String, Object>> findOrder(Long ordernumber) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqorders WHERE ordernumber = ?", ordernumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Map<String, Object>> findBasket(Long basketno) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqbasket WHERE basketno = ?", basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Map<String, Object>> findInvoice(Long invoiceid) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqinvoices WHERE invoiceid = ?", invoiceid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Price calculations (populate_with_prices_for_receiving) ───────────────

    /**
     * Computes unitprice_tax_excluded, unitprice_tax_included and tax_value_on_receiving.
     * Mirrors Koha::Acquisition::Order->populate_with_prices_for_receiving().
     * Uses the same logic as the ordering-side calculation but applied to the
     * receiving unitprice.
     */
    public BigDecimal[] calculateReceivingPrices(BigDecimal unitprice, BigDecimal taxRate, boolean listIncGst) {
        BigDecimal rate = taxRate != null ? taxRate : BigDecimal.ZERO;
        BigDecimal unitExcl, unitIncl, taxValue;

        if (listIncGst) {
            unitIncl = unitprice;
            unitExcl = rate.compareTo(BigDecimal.ZERO) > 0
                    ? unitprice.divide(BigDecimal.ONE.add(rate), 5, RoundingMode.HALF_UP)
                    : unitprice;
        } else {
            unitExcl = unitprice;
            unitIncl = unitprice.multiply(BigDecimal.ONE.add(rate)).setScale(5, RoundingMode.HALF_UP);
        }
        taxValue = unitIncl.subtract(unitExcl);
        return new BigDecimal[]{ unitExcl, unitIncl, taxValue };
    }

    // ── ModReceiveOrder ────────────────────────────────────────────────────────

    /**
     * Records the receipt of an order.
     * Mirrors C4::Acquisition::ModReceiveOrder().
     * <p>
     * When {@code quantityReceived < order.quantity} a new "remainder" order is
     * created for the unreceived portion and the original is closed as received.
     * When {@code quantityReceived == order.quantity} the original order is simply
     * updated to received status.
     *
     * @return new ordernumber (may differ from original if a split occurred)
     */
    public Long modReceiveOrder(Map<String, Object> order,
                                int quantityReceived,
                                Long invoiceid,
                                Long budgetId,
                                String datereceived,
                                BigDecimal unitpriceTaxExcluded,
                                BigDecimal unitpriceTaxIncluded,
                                BigDecimal taxValueOnReceiving,
                                BigDecimal taxRate,
                                BigDecimal unitprice,
                                BigDecimal replacementprice,
                                String internalnote,
                                BigDecimal invoiceUnitprice,
                                String invoiceCurrency) {

        Long ordernumber = toLong(order.get("ordernumber"));
        int orderedQty   = toInt(order.get("quantity"));
        Long returnOrdernumber = ordernumber;

        if (quantityReceived < orderedQty) {
            // ── Partial receipt: split the order ──────────────────────────────
            // Create a "remainder" order for the unreceived portion
            int remainderQty = orderedQty - quantityReceived;
            Long remainderOrdernumber = cloneOrderWithQty(order, remainderQty, budgetId);

            // Mark the original order as fully received (for the received qty)
            jdbc.update("""
                    UPDATE aqorders
                       SET orderstatus             = 'complete',
                           quantityreceived        = ?,
                           datereceived            = ?,
                           invoiceid               = ?,
                           budget_id               = COALESCE(?, budget_id),
                           unitprice               = ?,
                           unitprice_tax_excluded  = ?,
                           unitprice_tax_included  = ?,
                           tax_value_on_receiving  = ?,
                           tax_rate_on_receiving   = ?,
                           replacementprice        = ?,
                           order_internalnote      = COALESCE(?, order_internalnote),
                           invoice_unitprice       = ?,
                           invoice_currency        = ?
                     WHERE ordernumber = ?
                    """,
                    quantityReceived, datereceived, invoiceid, budgetId,
                    unitprice, unitpriceTaxExcluded, unitpriceTaxIncluded,
                    taxValueOnReceiving, taxRate, replacementprice,
                    internalnote, invoiceUnitprice, invoiceCurrency,
                    ordernumber);

            returnOrdernumber = remainderOrdernumber;
        } else {
            // ── Full receipt ──────────────────────────────────────────────────
            jdbc.update("""
                    UPDATE aqorders
                       SET orderstatus             = 'complete',
                           quantityreceived        = ?,
                           datereceived            = ?,
                           invoiceid               = ?,
                           budget_id               = COALESCE(?, budget_id),
                           unitprice               = ?,
                           unitprice_tax_excluded  = ?,
                           unitprice_tax_included  = ?,
                           tax_value_on_receiving  = ?,
                           tax_rate_on_receiving   = ?,
                           replacementprice        = ?,
                           order_internalnote      = COALESCE(?, order_internalnote),
                           invoice_unitprice       = ?,
                           invoice_currency        = ?
                     WHERE ordernumber = ?
                    """,
                    quantityReceived, datereceived, invoiceid, budgetId,
                    unitprice, unitpriceTaxExcluded, unitpriceTaxIncluded,
                    taxValueOnReceiving, taxRate, replacementprice,
                    internalnote, invoiceUnitprice, invoiceCurrency,
                    ordernumber);
        }

        return returnOrdernumber;
    }

    /** Creates a new "remainder" order by copying the original with a new quantity. */
    private Long cloneOrderWithQty(Map<String, Object> orig, int qty, Long budgetId) {
        String sql = """
                INSERT INTO aqorders
                    (basketno, biblionumber, quantity, listprice, ecost,
                     ecost_tax_excluded, ecost_tax_included,
                     rrp, rrp_tax_excluded, rrp_tax_included,
                     replacementprice, tax_rate_on_ordering,
                     uncertainprice, budget_id, currency,
                     order_internalnote, order_vendornote,
                     sort1, sort2, subscriptionid, orderstatus)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'ordered')
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1,  orig.get("basketno"));
            ps.setObject(2,  orig.get("biblionumber"));
            ps.setObject(3,  qty);
            ps.setObject(4,  orig.get("listprice"));
            ps.setObject(5,  orig.get("ecost"));
            ps.setObject(6,  orig.get("ecost_tax_excluded"));
            ps.setObject(7,  orig.get("ecost_tax_included"));
            ps.setObject(8,  orig.get("rrp"));
            ps.setObject(9,  orig.get("rrp_tax_excluded"));
            ps.setObject(10, orig.get("rrp_tax_included"));
            ps.setObject(11, orig.get("replacementprice"));
            ps.setObject(12, orig.get("tax_rate_on_ordering"));
            ps.setObject(13, orig.get("uncertainprice"));
            ps.setObject(14, budgetId != null ? budgetId : orig.get("budget_id"));
            ps.setObject(15, orig.get("currency"));
            ps.setObject(16, orig.get("order_internalnote"));
            ps.setObject(17, orig.get("order_vendornote"));
            ps.setObject(18, orig.get("sort1"));
            ps.setObject(19, orig.get("sort2"));
            ps.setObject(20, orig.get("subscriptionid"));
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("ordernumber")).longValue();
    }

    // ── Item updates ───────────────────────────────────────────────────────────

    /**
     * Updates items linked to the order with receipt-time metadata.
     * Mirrors the $item->update({...}) loop in finishreceive.pl.
     */
    public void updateOrderItems(Long ordernumber, Long booksellerid, String datereceived,
                                 BigDecimal unitprice, BigDecimal replacementprice) {
        List<Long> itemnumbers = jdbc.queryForList(
                "SELECT itemnumber FROM aqorders_items WHERE ordernumber = ?",
                Long.class, ordernumber);

        for (Long itemnumber : itemnumbers) {
            jdbc.update("""
                    UPDATE items
                       SET booksellerid         = ?,
                           dateaccessioned      = ?,
                           datelastseen         = ?,
                           price                = ?,
                           replacementprice     = ?,
                           replacementpricedate = ?
                     WHERE itemnumber = ?
                    """,
                    booksellerid, datereceived, datereceived,
                    unitprice, replacementprice, datereceived,
                    itemnumber);
        }
    }

    /**
     * Applies AcqItemSetSubfieldsWhenReceived preference field updates to items.
     * Mirrors the @affects loop in finishreceive.pl.
     * Field/subfield updates are stored as column name = value in items table.
     */
    public void applyReceiveSubfields(List<Long> itemnumbers, List<String[]> fieldValuePairs) {
        if (itemnumbers == null || itemnumbers.isEmpty() || fieldValuePairs == null) return;
        for (Long itemnumber : itemnumbers) {
            for (String[] fv : fieldValuePairs) {
                if (fv.length == 2 && fv[0] != null && !fv[0].isBlank()) {
                    try {
                        jdbc.update("UPDATE items SET " + fv[0] + " = ? WHERE itemnumber = ?",
                                fv[1], itemnumber);
                    } catch (Exception ignored) {
                        // Skip unknown columns
                    }
                }
            }
        }
    }

    // ── Suggestion update ──────────────────────────────────────────────────────

    public void updateSuggestionReason(Long suggestionId, String reason) {
        jdbc.update("UPDATE suggestions SET reason = ? WHERE suggestionid = ?",
                reason, suggestionId);
    }

    // ── Acquisition log ────────────────────────────────────────────────────────

    public void logReceipt(Long ordernumber, String info) {
        jdbc.update("""
                INSERT INTO action_logs (timestamp, user, module, action, object, info)
                VALUES (NOW(), 0, 'ACQUISITIONS', 'RECEIVE_ORDER', ?, ?)
                """, ordernumber, info);
    }

    // ── Vendor listincgst ──────────────────────────────────────────────────────

    public boolean vendorListIncGst(Long booksellerid) {
        try {
            Integer val = jdbc.queryForObject(
                    "SELECT listincgst FROM aqbooksellers WHERE id = ?", Integer.class, booksellerid);
            return val != null && val == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }

    private int toInt(Object v) {
        Long l = toLong(v);
        return l != null ? l.intValue() : 0;
    }
}

