package com.shailahir.koha.acquisitions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller porting invoice.pl — invoice detail and management.
 *
 * <pre>
 *  GET    /acquisitions/invoices/{id}                    — GetInvoiceDetails
 *  PUT    /acquisitions/invoices/{id}                    — op=cud-mod
 *  POST   /acquisitions/invoices/{id}/close              — op=cud-close
 *  POST   /acquisitions/invoices/{id}/reopen             — op=cud-reopen
 *  DELETE /acquisitions/invoices/{id}                    — op=cud-delete
 *  GET    /acquisitions/invoices/{id}/adjustments        — list adjustments
 *  POST   /acquisitions/invoices/{id}/adjustments        — create adjustment (cud-mod_adj new)
 *  PUT    /acquisitions/invoices/{id}/adjustments/{adj}  — update adjustment (cud-mod_adj)
 *  DELETE /acquisitions/invoices/{id}/adjustments/{adj}  — op=cud-del_adj
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceRepository invoiceRepo;
    private final ObjectMapper      objectMapper;

    // ── Invoice search (invoices.pl) ───────────────────────────────────────────

    /**
     * Searches invoices with optional filters — ports invoices.pl.
     * <p>
     * Mirrors {@code GetInvoices()} + the open/closed split in invoices.pl.
     * When {@code do_search=false} (default) returns empty lists, mirroring
     * the Perl behaviour of only running the query when {@code op=do_search}.
     *
     * @param doSearch        execute the search (pass {@code true} when form submitted)
     * @param invoicenumber   partial match on invoice number
     * @param supplierid      exact vendor id
     * @param shipmentdatefrom / shipmentdateto  shipment date range (ISO dates)
     * @param billingdatefrom / billingdateto    billing date range (ISO dates)
     * @param isbneanissn     matches isbn, ean or issn of ordered items
     * @param title           partial biblio title match
     * @param author          partial biblio author match
     * @param publisher       partial publishercode match
     * @param publicationyear exact publication year match
     * @param branch          basket branchcode filter
     * @param messageId       EDIFACT message id filter
     * @param additionalFields JSON array of {id,value} additional-field filters
     */
    @GetMapping("/acquisitions/invoices", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> searchInvoices(
            @RequestParam(value = "do_search",        defaultValue = "false") boolean doSearch,
            @RequestParam(value = "invoicenumber",    required = false) String invoicenumber,
            @RequestParam(value = "supplierid",       required = false) Long supplierid,
            @RequestParam(value = "shipmentdatefrom", required = false) String shipmentdatefrom,
            @RequestParam(value = "shipmentdateto",   required = false) String shipmentdateto,
            @RequestParam(value = "billingdatefrom",  required = false) String billingdatefrom,
            @RequestParam(value = "billingdateto",    required = false) String billingdateto,
            @RequestParam(value = "isbneanissn",      required = false) String isbneanissn,
            @RequestParam(value = "title",            required = false) String title,
            @RequestParam(value = "author",           required = false) String author,
            @RequestParam(value = "publisher",        required = false) String publisher,
            @RequestParam(value = "publicationyear",  required = false) String publicationyear,
            @RequestParam(value = "branch",           required = false) String branch,
            @RequestParam(value = "message_id",       required = false) Long messageId,
            @RequestParam(value = "additional_fields", required = false) String additionalFieldsJson) {

        if (!doSearch) {
            return ResponseEntity.ok(Map.of(
                    "opened_invoices", List.of(),
                    "closed_invoices", List.of(),
                    "invoices",        List.of(),
                    "do_search",       false));
        }

        // Parse optional additional_fields JSON: [{id:1,value:"..."}, ...]
        List<Map<String, Object>> additionalFields = List.of();
        if (additionalFieldsJson != null && !additionalFieldsJson.isBlank()) {
            try {
                additionalFields = objectMapper.readValue(additionalFieldsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            } catch (Exception ignored) {}
        }

        List<Map<String, Object>> invoices = invoiceRepo.searchInvoices(
                invoicenumber, supplierid,
                parseDate(shipmentdatefrom), parseDate(shipmentdateto),
                parseDate(billingdatefrom),  parseDate(billingdateto),
                isbneanissn, title, author, publisher, publicationyear,
                branch, messageId, additionalFields);

        // Split into open / closed (mirrors the Perl for loop)
        List<Map<String, Object>> opened = invoices.stream()
                .filter(i -> i.get("closedate") == null).toList();
        List<Map<String, Object>> closed = invoices.stream()
                .filter(i -> i.get("closedate") != null).toList();

        return ResponseEntity.ok(Map.of(
                "invoices",        invoices,
                "opened_invoices", opened,
                "closed_invoices", closed,
                "do_search",       true
        ));
    }

    private java.time.LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return java.time.LocalDate.parse(s.substring(0, 10)); } catch (Exception e) { return null; }
    }

    // ── GET detail ─────────────────────────────────────────────────────────────

    /**
     * Returns full invoice detail including enriched order lines and totals.
     * Mirrors GetInvoiceDetails() + the order loop + footer totals in invoice.pl.
     */
    @GetMapping("/acquisitions/invoices/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<InvoiceDetailDto> getInvoice(@PathVariable Long id) {
        Map<String, Object> inv = invoiceRepo.findInvoice(id)
                .orElse(null);
        if (inv == null) return ResponseEntity.notFound().build();

        List<InvoiceOrderLineDto> orders = invoiceRepo.findOrdersByInvoice(id);

        // ── Footer totals (per-tax-rate aggregation, mirrors invoice.pl) ───────
        Map<String, Map<String, Object>> foot = new LinkedHashMap<>();
        BigDecimal totalQty   = BigDecimal.ZERO;
        BigDecimal totalExcl  = BigDecimal.ZERO;
        BigDecimal totalIncl  = BigDecimal.ZERO;
        BigDecimal totalTax   = BigDecimal.ZERO;

        for (InvoiceOrderLineDto o : orders) {
            String rateKey = o.getTaxRateOnReceiving() != null
                    ? o.getTaxRateOnReceiving().toPlainString() : "0";
            foot.computeIfAbsent(rateKey, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("tax_rate", o.getTaxRateOnReceiving());
                m.put("tax_value", BigDecimal.ZERO);
                m.put("quantity", 0);
                m.put("total_tax_excluded", BigDecimal.ZERO);
                m.put("total_tax_included", BigDecimal.ZERO);
                return m;
            });
            Map<String, Object> fr = foot.get(rateKey);
            BigDecimal tv = o.getTaxValueOnReceiving() != null ? o.getTaxValueOnReceiving() : BigDecimal.ZERO;
            fr.put("tax_value",         round((BigDecimal) fr.get("tax_value")).add(round(tv)));
            fr.put("quantity",          (int) fr.get("quantity") + (o.getQuantity() != null ? o.getQuantity() : 0));
            fr.put("total_tax_excluded", round((BigDecimal) fr.get("total_tax_excluded"))
                    .add(o.getTotalTaxExcluded() != null ? round(o.getTotalTaxExcluded()) : BigDecimal.ZERO));
            fr.put("total_tax_included", round((BigDecimal) fr.get("total_tax_included"))
                    .add(o.getTotalTaxIncluded() != null ? round(o.getTotalTaxIncluded()) : BigDecimal.ZERO));

            totalQty  = totalQty.add(BigDecimal.valueOf(o.getQuantity() != null ? o.getQuantity() : 0));
            totalExcl = totalExcl.add(o.getTotalTaxExcluded() != null ? round(o.getTotalTaxExcluded()) : BigDecimal.ZERO);
            totalIncl = totalIncl.add(o.getTotalTaxIncluded() != null ? round(o.getTotalTaxIncluded()) : BigDecimal.ZERO);
            totalTax  = totalTax.add(tv);
        }

        BigDecimal shipmentcost = (BigDecimal) inv.getOrDefault("shipmentcost", BigDecimal.ZERO);
        if (shipmentcost == null) shipmentcost = BigDecimal.ZERO;

        return ResponseEntity.ok(InvoiceDetailDto.builder()
                .invoiceid(toLong(inv.get("invoiceid")))
                .invoicenumber((String) inv.get("invoicenumber"))
                .booksellerid(toLong(inv.get("booksellerid")))
                .suppliername((String) inv.get("suppliername"))
                .shipmentdate(toDate(inv.get("shipmentdate")))
                .billingdate(toDate(inv.get("billingdate")))
                .closedate(toDate(inv.get("closedate")))
                .shipmentcost(shipmentcost)
                .shipmentcostBudgetid(toLong(inv.get("shipmentcost_budgetid")))
                .messageId(toLong(inv.get("message_id")))
                .orders(orders)
                .footLoop(new ArrayList<>(foot.values()))
                .totalQuantity(totalQty.intValue())
                .totalTaxExcluded(totalExcl)
                .totalTaxIncluded(totalIncl)
                .totalTaxValue(totalTax)
                .totalTaxExcludedShipment(totalExcl.add(shipmentcost))
                .totalTaxIncludedShipment(totalIncl.add(shipmentcost))
                .invoiceincgst(inv.get("invoiceincgst") != null && toInt(inv.get("invoiceincgst")) == 1)
                .build());
    }

    // ── Close / Reopen ─────────────────────────────────────────────────────────

    /** op=cud-close — mirrors CloseInvoice(). */
    @PostMapping("/acquisitions/invoices/{id}/close", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<Void> closeInvoice(@PathVariable Long id) {
        ensureExists(id);
        invoiceRepo.closeInvoice(id);
        log.info("Invoice {} closed", id);
        return ResponseEntity.noContent().build();
    }

    /** op=cud-reopen — mirrors ReopenInvoice(). */
    @PostMapping("/acquisitions/invoices/{id}/reopen", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<Void> reopenInvoice(@PathVariable Long id) {
        ensureExists(id);
        invoiceRepo.reopenInvoice(id);
        log.info("Invoice {} reopened", id);
        return ResponseEntity.noContent().build();
    }

    // ── Modify ─────────────────────────────────────────────────────────────────

    /**
     * op=cud-mod — mirrors ModInvoice() + optional reopen/close/merge.
     */
    @PutMapping("/acquisitions/invoices/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<InvoiceDetailDto> modifyInvoice(
            @PathVariable Long id,
            @RequestBody InvoiceModRequest req) {

        ensureExists(id);
        invoiceRepo.modifyInvoice(id, req.getInvoicenumber(),
                req.getShipmentdate(), req.getBillingdate(),
                req.getShipmentcost(), req.getShipmentcostBudgetid());

        if (Boolean.TRUE.equals(req.getReopen())) {
            invoiceRepo.reopenInvoice(id);
        } else if (Boolean.TRUE.equals(req.getClose())) {
            invoiceRepo.closeInvoice(id);
        }

        if (req.getMergeSources() != null && !req.getMergeSources().isEmpty()) {
            invoiceRepo.mergeInvoices(id, req.getMergeSources());
            log.info("Invoice {} merged sources {}", id, req.getMergeSources());
        }

        return getInvoice(id);
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    /** op=cud-delete — mirrors DelInvoice(). Unlinks orders, deletes adjustments. */
    @DeleteMapping("/acquisitions/invoices/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        ensureExists(id);
        invoiceRepo.deleteInvoice(id);
        log.info("Invoice {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    // ── Adjustments ────────────────────────────────────────────────────────────

    /** Returns all adjustments for this invoice. */
    @GetMapping("/acquisitions/invoices/{id}/adjustments", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<InvoiceAdjustmentDto>> getAdjustments(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceRepo.findAdjustments(id));
    }

    /**
     * Creates a new adjustment (cud-mod_adj where adjustment_id = 'new').
     * Mirrors the $new_adj->store() + logaction(CREATE_INVOICE_ADJUSTMENT) block.
     */
    @PostMapping("/acquisitions/invoices/{id}/adjustments", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<InvoiceAdjustmentDto> createAdjustment(
            @PathVariable Long id,
            @RequestBody InvoiceAdjustmentDto dto) {

        dto.setInvoiceid(id);
        Long newId = invoiceRepo.createAdjustment(dto);
        dto.setAdjustmentId(newId);

        try {
            invoiceRepo.logAction("CREATE_INVOICE_ADJUSTMENT", newId, objectMapper.writeValueAsString(dto));
        } catch (Exception ignored) {}

        log.info("Invoice {} adjustment {} created", id, newId);
        return ResponseEntity.status(201).body(dto);
    }

    /**
     * Updates an existing adjustment (cud-mod_adj for existing id).
     * Logs if the adjustment values changed, mirroring the Perl comparison block.
     */
    @PutMapping("/acquisitions/invoices/{id}/adjustments/{adjId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<InvoiceAdjustmentDto> updateAdjustment(
            @PathVariable Long id,
            @PathVariable Long adjId,
            @RequestBody InvoiceAdjustmentDto dto) {

        InvoiceAdjustmentDto old = invoiceRepo.findAdjustmentById(adjId)
                .orElseThrow(() -> new NoSuchElementException("Adjustment not found: " + adjId));

        boolean changed = !Objects.equals(old.getAdjustment(), dto.getAdjustment())
                || !Objects.equals(old.getReason(), dto.getReason())
                || !Objects.equals(old.getBudgetId(), dto.getBudgetId())
                || !Objects.equals(old.getEncumberOpen(), dto.getEncumberOpen())
                || !Objects.equals(old.getNote(), dto.getNote());

        if (changed) {
            dto.setAdjustmentId(adjId);
            dto.setInvoiceid(id);
            invoiceRepo.updateAdjustment(dto);
            try {
                Map<String, Object> info = Map.of(
                        "adjustment", dto.getAdjustment(), "reason", String.valueOf(dto.getReason()),
                        "budget_id", String.valueOf(dto.getBudgetId()),
                        "encumber_open", Boolean.TRUE.equals(dto.getEncumberOpen()) ? 1 : 0,
                        "adjustment_old", old.getAdjustment(), "reason_old", String.valueOf(old.getReason()),
                        "budget_id_old", String.valueOf(old.getBudgetId()));
                invoiceRepo.logAction("UPDATE_INVOICE_ADJUSTMENT", adjId, objectMapper.writeValueAsString(info));
            } catch (Exception ignored) {}
            log.info("Invoice {} adjustment {} updated", id, adjId);
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * Deletes an adjustment (op=cud-del_adj).
     * Mirrors $del_adj->delete() + logaction(DELETE_INVOICE_ADJUSTMENT).
     */
    @DeleteMapping("/acquisitions/invoices/{id}/adjustments/{adjId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<Void> deleteAdjustment(
            @PathVariable Long id,
            @PathVariable Long adjId) {

        InvoiceAdjustmentDto adj = invoiceRepo.findAdjustmentById(adjId)
                .orElseThrow(() -> new NoSuchElementException("Adjustment not found: " + adjId));
        try {
            invoiceRepo.logAction("DELETE_INVOICE_ADJUSTMENT", adjId, objectMapper.writeValueAsString(adj));
        } catch (Exception ignored) {}
        invoiceRepo.deleteAdjustment(adjId);
        log.info("Invoice {} adjustment {} deleted", id, adjId);
        return ResponseEntity.noContent().build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void ensureExists(Long id) {
        invoiceRepo.findInvoice(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
    }

    private BigDecimal round(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }

    private int toInt(Object v) {
        Long l = toLong(v); return l != null ? l.intValue() : 0;
    }

    private java.time.LocalDate toDate(Object v) {
        if (v == null) return null;
        if (v instanceof java.time.LocalDate d) return d;
        if (v instanceof java.sql.Date d) return d.toLocalDate();
        try { return java.time.LocalDate.parse(v.toString().substring(0, 10)); } catch (Exception e) { return null; }
    }
}

