package com.shailahir.koha.acquisitions.repository;

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
import java.util.*;

/**
 * JDBC repository for invoice operations.
 * Mirrors CloseInvoice, ReopenInvoice, ModInvoice, MergeInvoices, DelInvoice,
 * GetInvoiceDetails, and aqinvoice_adjustments CRUD from invoice.pl.
 */
@Repository
@RequiredArgsConstructor
public class InvoiceRepository {

    private final JdbcTemplate jdbc;

    // ── Invoice lookup (GetInvoiceDetails) ────────────────────────────────────

    public Optional<Map<String, Object>> findInvoice(Long invoiceid) {
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
        jdbc.update("UPDATE aqinvoices SET closedate = NOW() WHERE invoiceid = ?", invoiceid);
    }

    /** Mirrors ReopenInvoice($invoiceid). */
    public void reopenInvoice(Long invoiceid) {
        jdbc.update("UPDATE aqinvoices SET closedate = NULL WHERE invoiceid = ?", invoiceid);
    }

    // ── Modify (ModInvoice) ────────────────────────────────────────────────────

    public void modifyInvoice(Long invoiceid, String invoicenumber, LocalDate shipmentdate,
                              LocalDate billingdate, BigDecimal shipmentcost, Long shipmentBudgetId) {
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
        jdbc.update("DELETE FROM aqinvoice_adjustments WHERE invoiceid = ?", invoiceid);
        jdbc.update("UPDATE aqorders SET invoiceid = NULL WHERE invoiceid = ?", invoiceid);
        jdbc.update("DELETE FROM aqinvoices WHERE invoiceid = ?", invoiceid);
    }

    // ── Adjustments ────────────────────────────────────────────────────────────

    public List<InvoiceAdjustmentDto> findAdjustments(Long invoiceid) {
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
        jdbc.update("DELETE FROM aqinvoice_adjustments WHERE adjustment_id = ?", adjustmentId);
    }

    // ── Log helper ─────────────────────────────────────────────────────────────

    public void logAction(String action, Long objectId, String info) {
        jdbc.update("""
                INSERT INTO action_logs (timestamp, user, module, action, object, info)
                VALUES (NOW(), 0, 'ACQUISITIONS', ?, ?, ?)
                """, action, objectId, info);
    }

    // ── Vendor invoiceincgst ────────────────────────────────────────────────────

    public boolean vendorInvoiceIncGst(Long booksellerid) {
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
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }
}

