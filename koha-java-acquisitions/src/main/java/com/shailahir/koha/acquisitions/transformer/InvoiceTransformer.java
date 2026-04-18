package com.shailahir.koha.acquisitions.transformer;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.InvoiceDetailDto;
import com.shailahir.koha.acquisitions.dto.InvoiceModRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * Transformer for Invoice-related DTOs.
 *
 * <p>Mirrors field-mapping done in invoice.pl and invoices.pl.
 */
@Slf4j
@Component
public class InvoiceTransformer {

    /**
     * Converts a raw DB row map to an {@link InvoiceDetailDto}.
     *
     * @param row the raw row from aqinvoices join
     * @return invoice detail DTO
     */
    public InvoiceDetailDto fromRow(Map<String, Object> row) {
        log.debug("Entering fromRow - {}", row);
        if (row == null) return null;
        return InvoiceDetailDto.builder()
                .invoiceid(toLong(row, "invoiceid"))
                .invoicenumber(toString(row, "invoicenumber"))
                .booksellerid(toLong(row, "booksellerid"))
                .suppliername(toString(row, "booksellername"))
                .shipmentdate(toLocalDate(row, "shipmentdate"))
                .billingdate(toLocalDate(row, "billingdate"))
                .closedate(toLocalDate(row, "closedate"))
                .shipmentcost(row.get("shipmentcost") != null
                        ? new java.math.BigDecimal(row.get("shipmentcost").toString()) : null)
                .shipmentcostBudgetid(toLong(row, "shipmentcost_budgetid"))
                .build();
    }

    /**
     * Converts an {@link InvoiceModRequest} into a parameter map for the update call.
     */
    public Map<String, Object> toUpdateParams(Long invoiceid, InvoiceModRequest req) {
        log.debug("Entering toUpdateParams - {}, {}", invoiceid, req);
        return Map.of(
                "invoiceid",             invoiceid,
                "invoicenumber",         req.getInvoicenumber() != null ? req.getInvoicenumber() : "",
                "shipmentdate",          req.getShipmentdate(),
                "billingdate",           req.getBillingdate(),
                "shipmentcost",          req.getShipmentcost(),
                "shipmentcost_budgetid", req.getShipmentcostBudgetid()
        );
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long toLong(Map<String, Object> row, String key) {
        log.debug("Entering toLong - {}, {}", row, key);
        Object v = row.get(key);
        return v != null ? ((Number) v).longValue() : null;
    }

    private String toString(Map<String, Object> row, String key) {
        log.debug("Entering toString - {}, {}", row, key);
        Object v = row.get(key);
        return v != null ? v.toString() : null;
    }

    private LocalDate toLocalDate(Map<String, Object> row, String key) {
        log.debug("Entering toLocalDate - {}, {}", row, key);
        Object v = row.get(key);
        if (v == null) return null;
        if (v instanceof LocalDate ld) return ld;
        if (v instanceof java.sql.Date sd) return sd.toLocalDate();
        return LocalDate.parse(v.toString().substring(0, 10));
    }
}
