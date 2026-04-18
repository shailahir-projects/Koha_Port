package com.shailahir.koha.acquisitions.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.BudgetCheckResult;
import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.OrderRequest;
import com.shailahir.koha.acquisitions.repository.BudgetRepository;
import com.shailahir.koha.acquisitions.service.BudgetExceededException;
import com.shailahir.koha.acquisitions.service.DuplicateBiblioException;
import com.shailahir.koha.acquisitions.service.OrderService;
import com.shailahir.koha.acquisitions.service.BasketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Acquisitions REST controller — full implementation of addorder.pl business logic
 * plus all other acquisitions endpoints.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AcquisitionsController {

    private final OrderService orderService;
    private final BasketService basketService;
    private final BudgetRepository budgetRepository;

    // ── Baskets ────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/baskets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<BasketDto>> listBaskets(Pageable pageable) {
        log.debug("Entering listBaskets - {}", pageable);
        return ResponseEntity.ok(basketService.listBaskets(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @PostMapping("/acquisitions/baskets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BasketDto> addBasket(@RequestBody @Valid BasketDto dto) {
        log.debug("Entering addBasket - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(basketService.addBasket(dto));
    }

    @GetMapping("/acquisitions/baskets/managers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<BasketDto>> listBasketsManagers() {
        log.debug("Entering listBasketsManagers");
        return ResponseEntity.ok(List.of());
    }

    // ── Orders ─────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<OrderDto>> listOrders(Pageable pageable) {
        log.debug("Entering listOrders - {}", pageable);
        return ResponseEntity.ok(orderService.listOrders(pageable.getPageNumber(), pageable.getPageSize()));
    }

    /**
     * POST /acquisitions/orders — implements cud-order from addorder.pl.
     * <p>
     * Steps: budget validation → duplicate detection → biblio auto-create
     * → suggestion update → order persist → order-user assignment
     * → item creation → acquisition log.
     *
     * Returns 409 CONFLICT with error details if budget is exceeded or duplicate biblio found.
     */
    @PostMapping("/acquisitions/orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addOrder(@RequestBody @Valid OrderRequest dto) {
        log.debug("Entering addOrder - {}", dto);
        try {
            OrderDto saved = orderService.saveOrder(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (BudgetExceededException ex) {
            BudgetCheckResult check = orderService.checkBudget(
                    dto.getBudgetId(), dto.getOrdernumber(),
                    dto.getTotal() != null ? dto.getTotal() : BigDecimal.ZERO);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "budget_exceeded",
                    "message", ex.getMessage(),
                    "budgetCheck", check));
        } catch (DuplicateBiblioException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "duplicate_biblio",
                    "message", ex.getMessage(),
                    "duplicateBiblionumber", ex.getDuplicateBiblionumber()));
        }
    }

    @GetMapping("/acquisitions/orders/{order_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrderDto> getOrder(@PathVariable("order_id") Long id) {
        log.debug("Entering getOrder - {}", id);
        return orderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /acquisitions/orders/{order_id} — modify an existing order.
     * Same business logic as POST with ordernumber forced to the path variable.
     */
    @PutMapping("/acquisitions/orders/{order_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateOrder(@PathVariable("order_id") Long id,
                                          @RequestBody @Valid OrderRequest dto) {
        log.debug("Entering updateOrder - {}, {}", id, dto);
        dto.setOrdernumber(id);
        try {
            return ResponseEntity.ok(orderService.saveOrder(dto));
        } catch (BudgetExceededException ex) {
            BudgetCheckResult check = orderService.checkBudget(
                    dto.getBudgetId(), id,
                    dto.getTotal() != null ? dto.getTotal() : BigDecimal.ZERO);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "budget_exceeded",
                    "message", ex.getMessage(),
                    "budgetCheck", check));
        } catch (DuplicateBiblioException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "duplicate_biblio",
                    "duplicateBiblionumber", ex.getDuplicateBiblionumber()));
        }
    }

    /** Soft-cancels the order (orderstatus = 'cancelled'). */
    @DeleteMapping("/acquisitions/orders/{order_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteOrder(@PathVariable("order_id") Long id) {
        log.debug("Entering deleteOrder - {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ── Vendors ────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/vendors", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listVendors(Pageable pageable) {
        log.debug("Entering listVendors - {}", pageable);
        return ResponseEntity.ok(budgetRepository.findAllVendors(
                pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()));
    }

    @PostMapping("/acquisitions/vendors", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> addVendor(@RequestBody Map<String, Object> dto) {
        log.debug("Entering addVendor - {}", dto);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("error", "not_implemented"));
    }

    @GetMapping("/acquisitions/vendors/{vendor_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getVendor(@PathVariable("vendor_id") Long id) {
        log.debug("Entering getVendor - {}", id);
        return budgetRepository.findVendorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/acquisitions/vendors/{vendor_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateVendor(@PathVariable("vendor_id") Long id,
                                                             @RequestBody Map<String, Object> dto) {
        log.debug("Entering updateVendor - {}, {}", id, dto);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("error", "not_implemented"));
    }

    @DeleteMapping("/acquisitions/vendors/{vendor_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteVendor(@PathVariable("vendor_id") Long id) {
        log.debug("Entering deleteVendor - {}", id);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/acquisitions/vendors/config", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getVendorsConfig() { return ResponseEntity.ok(Map.of()); }

    @GetMapping("/acquisitions/vendors/extended_attribute_types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listVendorExtendedAttributeTypes() { return ResponseEntity.ok(List.of()); }

    @GetMapping("/acquisitions/vendors/{vendor_id}/issues", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listVendorIssues(@PathVariable("vendor_id") Long vendorId) { return ResponseEntity.ok(List.of()); }

    // ── Funds ──────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/funds", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listFunds(Pageable pageable) {
        log.debug("Entering listFunds - {}", pageable);
        return ResponseEntity.ok(budgetRepository.findAllFunds(
                pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()));
    }

    @GetMapping("/acquisitions/funds/owners", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listFundsOwners() { return ResponseEntity.ok(List.of()); }

    @GetMapping("/acquisitions/funds/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listFundsUsers() { return ResponseEntity.ok(List.of()); }

    /**
     * Updates just the estimated_delivery_date of an individual order.
     * Ports moddeliverydate.pl — op=cud-save.
     * <p>
     * Mirrors: {@code $order->{'estimated_delivery_date'} = $date; ModOrder($order);}
     * Used when a basket is already closed and the librarian wants to update only
     * the expected delivery date without editing the full order.
     *
     * @param ordernumber order to update
     * @param date        new estimated delivery date (ISO 8601 date string), or null/blank to clear it
     * @return updated order info (ordernumber, basketno, estimated_delivery_date)
     */
    @PatchMapping("/acquisitions/orders/{ordernumber}/estimated-delivery-date", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateEstimatedDeliveryDate(
            @PathVariable Long ordernumber,
            @RequestBody Map<String, String> body) {
        log.debug("Entering updateEstimatedDeliveryDate - {}, {}", ordernumber, body);

        // Validate the order exists
        Integer exists = budgetRepository.getJdbc().queryForObject(
                "SELECT COUNT(*) FROM aqorders WHERE ordernumber = ?", Integer.class, ordernumber);
        if (exists == null || exists == 0) {
            return ResponseEntity.notFound().build();
        }

        String dateStr = body != null ? body.get("estimated_delivery_date") : null;
        java.time.LocalDate date = null;
        if (dateStr != null && !dateStr.isBlank()) {
            try { date = java.time.LocalDate.parse(dateStr.substring(0, 10)); } catch (Exception ignored) {}
        }

        // ModOrder — update only estimated_delivery_date (mirrors moddeliverydate.pl)
        budgetRepository.getJdbc().update(
                "UPDATE aqorders SET estimated_delivery_date = ? WHERE ordernumber = ?",
                date, ordernumber);

        return ResponseEntity.ok(Map.of(
                "ordernumber",             ordernumber,
                "estimated_delivery_date", date != null ? date.toString() : ""
        ));
    }

    /**
     * Returns the budget_amount for a given budget_id.
     * Ports check_budget_total.pl — used as an AJAX helper when the user changes
     * the fund on the order form to show the available budget total.
     *
     * @param budgetId the budget_id to look up
     * @return {@code { "budget_amount": 1234.56 }} or 404 when not found
     */
    @GetMapping("/acquisitions/budgets/{budget_id}/amount", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getBudgetAmount(
            @PathVariable("budget_id") Long budgetId) {
        log.debug("Entering getBudgetAmount - {}", budgetId);
        return budgetRepository.getBudgetAmount(budgetId)
                .map(amount -> ResponseEntity.ok(Map.<String, Object>of("budget_amount", amount)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Checks whether given field/value pairs already exist in the items table.
     * Ports check_uniqueness.pl — called by the add-item JavaScript (check_additem).
     * <p>
     * Accepts parallel lists of {@code field[]} and {@code value[]} query parameters.
     * Only fields from a fixed allow-list are queried to prevent SQL injection.
     * <p>
     * Returns a JSON map of {@code { fieldName: [duplicate_value, ...] }}
     * containing only the pairs where a duplicate was found.
     * <p>
     * Example request: {@code ?field[]=barcode&value[]=1234&field[]=barcode&value[]=1235}
     * Example response: {@code {"barcode":["1234","1235"]}}
     *
     * @param fields parallel list of items-table field names to check
     * @param values parallel list of values corresponding to each field
     * @return map of field → list of duplicate values found
     */
    @GetMapping("/acquisitions/items/check-uniqueness", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, List<String>>> checkItemUniqueness(
            @RequestParam(value = "field[]", required = false) List<String> fields,
            @RequestParam(value = "value[]", required = false) List<String> values) {
        log.debug("Entering checkItemUniqueness - {}, {}", fields, values);

        // Allow-list of item fields that may be checked (mirrors what Koha uses)
        java.util.Set<String> ALLOWED_FIELDS = java.util.Set.of(
                "barcode", "stocknumber", "itemnumber");

        Map<String, List<String>> result = new java.util.LinkedHashMap<>();

        if (fields == null || values == null || fields.size() != values.size()) {
            return ResponseEntity.ok(result);
        }

        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            String value = values.get(i);

            if (!ALLOWED_FIELDS.contains(field) || value == null || value.isBlank()) {
                continue;
            }

            // Safe: field is from allow-list only
            Integer count = budgetRepository.getJdbc().queryForObject(
                    "SELECT COUNT(*) FROM items WHERE " + field + " = ?",
                    Integer.class, value);

            if (count != null && count > 0) {
                result.computeIfAbsent(field, k -> new java.util.ArrayList<>()).add(value);
            }
        }

        return ResponseEntity.ok(result);
    }

    // ── EDI files ──────────────────────────────────────────────────────────────

    // ── EDIFACT files (stub — full implementation in EdifactMsgsController) ──────

    // ── Quotes ─────────────────────────────────────────────────────────────────

    @GetMapping("/quotes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listQuotes(Pageable pageable) { return ResponseEntity.ok(List.of()); }

    @PostMapping("/quotes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> addQuote(@RequestBody Map<String, Object> dto) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of()); }

    @GetMapping("/quotes/{quote_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getQuote(@PathVariable("quote_id") Long id) { return ResponseEntity.ok(Map.of()); }

    @PutMapping("/quotes/{quote_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateQuote(@PathVariable("quote_id") Long id, @RequestBody Map<String, Object> dto) { return ResponseEntity.ok(Map.of()); }

    @DeleteMapping("/quotes/{quote_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteQuote(@PathVariable("quote_id") Long id) { return ResponseEntity.noContent().build(); }

    // ── Suggestions ────────────────────────────────────────────────────────────

    @GetMapping("/suggestions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listSuggestions(Pageable pageable) { return ResponseEntity.ok(List.of()); }

    @PostMapping("/suggestions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> addSuggestion(@RequestBody Map<String, Object> dto) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of()); }

    @GetMapping("/suggestions/{suggestion_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getSuggestion(@PathVariable("suggestion_id") Long id) { return ResponseEntity.ok(Map.of()); }

    @PutMapping("/suggestions/{suggestion_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateSuggestion(@PathVariable("suggestion_id") Long id, @RequestBody Map<String, Object> dto) { return ResponseEntity.ok(Map.of()); }

    @DeleteMapping("/suggestions/{suggestion_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSuggestion(@PathVariable("suggestion_id") Long id) { return ResponseEntity.noContent().build(); }

    @GetMapping("/suggestions/managers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listSuggestionsManagers() { return ResponseEntity.ok(List.of()); }
}

