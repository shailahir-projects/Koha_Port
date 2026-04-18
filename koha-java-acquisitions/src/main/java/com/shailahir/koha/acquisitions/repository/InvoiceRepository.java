package com.shailahir.koha.acquisitions.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;import java.time.LocalDate;
import java.util.*;

/**
 * JDBC repository for invoice operations.
 * Mirrors CloseInvoice, ReopenInvoice, ModInvoice, MergeInvoices, DelInvoice,
 * GetInvoiceDetails, and aqinvoice_adjustments CRUD from invoice.pl.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InvoiceRepository {

    private final JdbcTemplate jdbc;

    // ── GetInvoices (invoices.pl search) ─────────────────────────────────────

    /**
     * Searches invoices with optional filters.
     * Mirrors C4::Acquisition::GetInvoices().
     * All parameters are optional; when none supplied returns all invoices.
     *
     * @param invoicenumber     partial match on invoicenumber
     * @param supplierid        exact match on booksellerid
     * @param shipmentdatefrom  lower bound for shipmentdate
     * @param shipmentdateto    upper bound for shipmentdate
     * @param billingdatefrom   lower bound for billingdate
     * @param billingdateto     upper bound for billingdate
     * @param isbneanissn       partial match against isbn, ean, or issn of ordered items
     * @param title             partial match on biblio.title
     * @param author            partial match on biblio.author
     * @param publisher         partial match on biblioitems.publishercode
     * @param publicationyear   exact match on biblioitems.publicationyear
     * @param branchcode        filter by basket branch
     * @param messageId         exact match on aqinvoices.message_id
     * @param additionalFields  list of {id, value} maps for additional field filters
     */
    public List<Map<String, Object>> searchInvoices(
            String invoicenumber, Long supplierid,
            LocalDate shipmentdatefrom, LocalDate shipmentdateto,
            LocalDate billingdatefrom,  LocalDate billingdateto,
            String isbneanissn, String title, String author,
            String publisher,   String publicationyear,
            String branchcode,  Long messageId,
            List<Map<String, Object>> additionalFields) {
        log.debug("Entering searchInvoices - {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}", invoicenumber, supplierid, shipmentdatefrom, shipmentdateto, billingdatefrom, billingdateto, isbneanissn, title, author, publisher, publicationyear, branchcode, messageId, additionalFields);

        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT i.invoiceid, i.invoicenumber, i.booksellerid,
                       i.shipmentdate, i.billingdate, i.closedate,
                       i.shipmentcost, i.shipmentcost_budgetid, i.message_id,
                       v.name AS suppliername
                  FROM aqinvoices i
                  LEFT JOIN aqbooksellers v   ON v.id = i.booksellerid
                  LEFT JOIN aqorders o        ON o.invoiceid = i.invoiceid
                  LEFT JOIN biblio bib        ON bib.biblionumber = o.biblionumber
                  LEFT JOIN biblioitems bi    ON bi.biblionumber  = o.biblionumber
                  LEFT JOIN aqbasket b        ON b.basketno = o.basketno
                 WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (notBlank(invoicenumber)) {
            sql.append(" AND i.invoicenumber LIKE ?"); params.add("%" + invoicenumber + "%");
        }
        if (supplierid != null) {
            sql.append(" AND i.booksellerid = ?"); params.add(supplierid);
        }
        if (shipmentdatefrom != null) {
            sql.append(" AND i.shipmentdate >= ?"); params.add(shipmentdatefrom);
        }
        if (shipmentdateto != null) {
            sql.append(" AND i.shipmentdate <= ?"); params.add(shipmentdateto);
        }
        if (billingdatefrom != null) {
            sql.append(" AND i.billingdate >= ?"); params.add(billingdatefrom);
        }
        if (billingdateto != null) {
            sql.append(" AND i.billingdate <= ?"); params.add(billingdateto);
        }
        if (notBlank(isbneanissn)) {
            sql.append(" AND (bi.isbn LIKE ? OR bi.ean LIKE ? OR bi.issn LIKE ?)");
            String like = "%" + isbneanissn + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (notBlank(title)) {
            sql.append(" AND bib.title LIKE ?"); params.add("%" + title + "%");
        }
        if (notBlank(author)) {
            sql.append(" AND bib.author LIKE ?"); params.add("%" + author + "%");
        }
        if (notBlank(publisher)) {
            sql.append(" AND bi.publishercode LIKE ?"); params.add("%" + publisher + "%");
        }
        if (notBlank(publicationyear)) {
            sql.append(" AND bi.publicationyear = ?"); params.add(publicationyear);
        }
        if (notBlank(branchcode)) {
            sql.append(" AND b.branch = ?"); params.add(branchcode);
        }
        if (messageId != null) {
            sql.append(" AND i.message_id = ?"); params.add(messageId);
        }
        // Additional field filters (searchable aqinvoices additional fields)
        if (additionalFields != null) {
            for (Map<String, Object> af : additionalFields) {
                Object afId  = af.get("id");
                Object afVal = af.get("value");
                if (afId != null && afVal != null && !afVal.toString().isBlank()) {
                    sql.append("""
                             AND EXISTS (
                                SELECT 1 FROM additional_field_values afv
                                 WHERE afv.record_id = i.invoiceid
                                   AND afv.field_id  = ?
                                   AND afv.value LIKE ?
                             )
                            """);
                    params.add(afId);
                    params.add("%" + afVal + "%");
                }
            }
        }

        sql.append(" ORDER BY i.shipmentdate DESC, i.invoicenumber");
        return jdbc.queryForList(sql.toString(), params.toArray());
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    // ── Invoice lookup (GetInvoiceDetails) ────────────────────────────────────

    public Optional<Map<String, Object>> findInvoice(Long invoiceid) {
        log.debug("Entering findInvoice - {}", invoiceid);
        try {
            return Optional.ofNullable(jdbc.queryForMap("""
                    SELECT i.*, v.name AS suppliername, v.invoiceincgst
                      FROM aqinvoices i
                      LEFT JOIN aqbooksellers v ON v.id = i.booksellerid
                     WHERE i.invoiceid = ?
                    """, invoiceid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<InvoiceOrderLineDto> findOrdersByInvoice(Long invoiceid) {
        log.debug("Entering findOrdersByInvoice - {}", invoiceid);
        String sql = """
                SELECT o.ordernumber, o.parent_ordernumber, o.biblionumber,
                       bib.title, bib.author,
                       o.quantity, o.quantityreceived,
                       o.budget_id, aq.budget_name,
                       o.unitprice_tax_excluded, o.unitprice_tax_included,
                       o.tax_rate_on_receiving, o.tax_value_on_receiving,
                       o.invoice_unitprice, o.invoice_currency,
                       o.datereceived, o.orderstatus
                  FROM aqorders o
                  LEFT JOIN biblio bib ON bib.biblionumber = o.biblionumber
                  LEFT JOIN aqbudgets aq ON aq.budget_id = o.budget_id
                 WHERE o.invoiceid = ?
                   AND o.orderstatus != 'cancelled'
                 ORDER BY o.ordernumber
                """;

        RowMapper<InvoiceOrderLineDto> mapper = (rs, rn) -> {
            int qty     = rs.getInt("quantity");
            int qtyRec  = rs.getInt("quantityreceived");
            BigDecimal unitExcl = rs.getBigDecimal("unitprice_tax_excluded");
            BigDecimal unitIncl = rs.getBigDecimal("unitprice_tax_included");
            if (unitExcl == null) unitExcl = BigDecimal.ZERO;
            if (unitIncl == null) unitIncl = BigDecimal.ZERO;
            BigDecimal totalExcl = round(unitExcl).multiply(BigDecimal.valueOf(qty));
            BigDecimal totalIncl = round(unitIncl).multiply(BigDecimal.valueOf(qty));

            String title = rs.getString("title");
            // Append volume/seriestitle if present (mirrored from get_infos)
            // Those fields are on biblio; we skip for now as they need extra join

            return InvoiceOrderLineDto.builder()
                    .ordernumber(rs.getLong("ordernumber"))
                    .parentOrdernumber(rs.getObject("parent_ordernumber") != null
                            ? rs.getLong("parent_ordernumber") : null)
                    .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                    .title(title)
                    .author(rs.getString("author"))
                    .quantity(qty)
                    .quantityreceived(qtyRec)
                    .orderReceived(qty == qtyRec)
                    .budgetId(rs.getObject("budget_id") != null ? rs.getLong("budget_id") : null)
                    .budgetName(rs.getString("budget_name"))
                    .unitpriceTaxExcluded(unitExcl)
                    .unitpriceTaxIncluded(unitIncl)
                    .taxRateOnReceiving(rs.getBigDecimal("tax_rate_on_receiving"))
                    .taxValueOnReceiving(rs.getBigDecimal("tax_value_on_receiving"))
                    .totalTaxExcluded(totalExcl)
                    .totalTaxIncluded(totalIncl)
                    .invoiceUnitprice(rs.getBigDecimal("invoice_unitprice"))
                    .invoiceCurrency(rs.getString("invoice_currency"))
                    .datereceived(rs.getObject("datereceived", LocalDate.class))
                    .orderstatus(rs.getString("orderstatus"))
                    .build();
        };
        return jdbc.query(sql, mapper, invoiceid);
    }

    // ── Close / Reopen ─────────────────────────────────────────────────────────

    /** Mirrors CloseInvoice($invoiceid). */
    public void closeInvoice(Long invoiceid) {
        log.debug("Entering closeInvoice - {}", invoiceid);
        jdbc.update("UPDATE aqinvoices SET closedate = NOW() WHERE invoiceid = ?", invoiceid);
    }

    /** Mirrors ReopenInvoice($invoiceid). */
    public void reopenInvoice(Long invoiceid) {
        log.debug("Entering reopenInvoice - {}", invoiceid);
        jdbc.update("UPDATE aqinvoices SET closedate = NULL WHERE invoiceid = ?", invoiceid);
    }

    // ── Modify (ModInvoice) ────────────────────────────────────────────────────

    public void modifyInvoice(Long invoiceid, String invoicenumber, LocalDate shipmentdate,
                              LocalDate billingdate, BigDecimal shipmentcost, Long shipmentBudgetId) {
        log.debug("Entering modifyInvoice - {}, {}, {}, {}, {}, {}", invoiceid, invoicenumber, shipmentdate, billingdate, shipmentcost, shipmentBudgetId);
        jdbc.update("""
                UPDATE aqinvoices
                   SET invoicenumber         = ?,
                       shipmentdate          = ?,
                       billingdate           = ?,
                       shipmentcost          = ?,
                       shipmentcost_budgetid = ?
                 WHERE invoiceid = ?
                """,
                invoicenumber, shipmentdate, billingdate, shipmentcost, shipmentBudgetId, invoiceid);
    }

    // ── Merge (MergeInvoices) ──────────────────────────────────────────────────

    /**
     * Merges source invoices into the target invoice.
     * Mirrors MergeInvoices($invoiceid, \@sources).
     * Reassigns all orders from source invoices to the target, then deletes sources.
     */
    public void mergeInvoices(Long targetInvoiceid, List<Long> sourceIds) {
        log.debug("Entering mergeInvoices - {}, {}", targetInvoiceid, sourceIds);
        for (Long sourceId : sourceIds) {
            if (sourceId.equals(targetInvoiceid)) continue;
            jdbc.update("UPDATE aqorders SET invoiceid = ? WHERE invoiceid = ?", targetInvoiceid, sourceId);
            jdbc.update("UPDATE aqinvoice_adjustments SET invoiceid = ? WHERE invoiceid = ?",
                    targetInvoiceid, sourceId);
            jdbc.update("DELETE FROM aqinvoices WHERE invoiceid = ?", sourceId);
        }
    }

    // ── Delete (DelInvoice) ────────────────────────────────────────────────────

    /** Mirrors DelInvoice($invoiceid). */
    public void deleteInvoice(Long invoiceid) {
        log.debug("Entering deleteInvoice - {}", invoiceid);
        jdbc.update("DELETE FROM aqinvoice_adjustments WHERE invoiceid = ?", invoiceid);
        jdbc.update("UPDATE aqorders SET invoiceid = NULL WHERE invoiceid = ?", invoiceid);
        jdbc.update("DELETE FROM aqinvoices WHERE invoiceid = ?", invoiceid);
    }

    // ── Adjustments ────────────────────────────────────────────────────────────

    public List<InvoiceAdjustmentDto> findAdjustments(Long invoiceid) {
        log.debug("Entering findAdjustments - {}", invoiceid);
        return jdbc.query(
                "SELECT * FROM aqinvoice_adjustments WHERE invoiceid = ? ORDER BY adjustment_id",
                (rs, rn) -> InvoiceAdjustmentDto.builder()
                        .adjustmentId(rs.getLong("adjustment_id"))
                        .invoiceid(rs.getLong("invoiceid"))
                        .adjustment(rs.getBigDecimal("adjustment"))
                        .reason(rs.getString("reason"))
                        .note(rs.getString("note"))
                        .budgetId(rs.getObject("budget_id") != null ? rs.getLong("budget_id") : null)
                        .encumberOpen(rs.getBoolean("encumber_open"))
                        .build(),
                invoiceid);
    }

    public Optional<InvoiceAdjustmentDto> findAdjustmentById(Long adjustmentId) {
        log.debug("Entering findAdjustmentById - {}", adjustmentId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM aqinvoice_adjustments WHERE adjustment_id = ?",
                    (rs, rn) -> InvoiceAdjustmentDto.builder()
                            .adjustmentId(rs.getLong("adjustment_id"))
                            .invoiceid(rs.getLong("invoiceid"))
                            .adjustment(rs.getBigDecimal("adjustment"))
                            .reason(rs.getString("reason"))
                            .note(rs.getString("note"))
                            .budgetId(rs.getObject("budget_id") != null ? rs.getLong("budget_id") : null)
                            .encumberOpen(rs.getBoolean("encumber_open"))
                            .build(),
                    adjustmentId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /** Creates a new adjustment. Returns the new adjustment_id. */
    public Long createAdjustment(InvoiceAdjustmentDto dto) {
        log.debug("Entering createAdjustment - {}", dto);
        String sql = """
                INSERT INTO aqinvoice_adjustments
                    (invoiceid, adjustment, reason, note, budget_id, encumber_open)
                VALUES (?,?,?,?,?,?)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, dto.getInvoiceid());
            ps.setObject(2, dto.getAdjustment());
            ps.setObject(3, dto.getReason());
            ps.setObject(4, dto.getNote());
            ps.setObject(5, dto.getBudgetId());
            ps.setInt(6, Boolean.TRUE.equals(dto.getEncumberOpen()) ? 1 : 0);
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("adjustment_id")).longValue();
    }

    /** Updates an existing adjustment. */
    public void updateAdjustment(InvoiceAdjustmentDto dto) {
        log.debug("Entering updateAdjustment - {}", dto);
        jdbc.update("""
                UPDATE aqinvoice_adjustments
                   SET adjustment    = ?,
                       reason        = ?,
                       note          = ?,
                       budget_id     = ?,
                       encumber_open = ?
                 WHERE adjustment_id = ?
                """,
                dto.getAdjustment(), dto.getReason(), dto.getNote(),
                dto.getBudgetId(), Boolean.TRUE.equals(dto.getEncumberOpen()) ? 1 : 0,
                dto.getAdjustmentId());
    }

    /** Deletes a single adjustment. */
    public void deleteAdjustment(Long adjustmentId) {
        log.debug("Entering deleteAdjustment - {}", adjustmentId);
        jdbc.update("DELETE FROM aqinvoice_adjustments WHERE adjustment_id = ?", adjustmentId);
    }

    // ── Log helper ─────────────────────────────────────────────────────────────

    public void logAction(String action, Long objectId, String info) {
        log.debug("Entering logAction - {}, {}, {}", action, objectId, info);
        jdbc.update("""
                INSERT INTO action_logs (timestamp, user, module, action, object, info)
                VALUES (NOW(), 0, 'ACQUISITIONS', ?, ?, ?)
                """, action, objectId, info);
    }

    // ── Vendor invoiceincgst ────────────────────────────────────────────────────

    public boolean vendorInvoiceIncGst(Long booksellerid) {
        log.debug("Entering vendorInvoiceIncGst - {}", booksellerid);
        try {
            Integer v = jdbc.queryForObject(
                    "SELECT invoiceincgst FROM aqbooksellers WHERE id = ?", Integer.class, booksellerid);
            return v != null && v == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    // ── Util ───────────────────────────────────────────────────────────────────

    private BigDecimal round(BigDecimal v) {
        log.debug("Entering round - {}", v);
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }
}

