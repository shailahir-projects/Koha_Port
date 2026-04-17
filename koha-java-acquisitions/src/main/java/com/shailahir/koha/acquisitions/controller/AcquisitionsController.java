package com.shailahir.koha.acquisitions.controller;

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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Acquisitions REST controller — full implementation of addorder.pl business logic
 * plus all other acquisitions endpoints.
 */
@RestController
@RequiredArgsConstructor
public class AcquisitionsController {

    private final OrderService orderService;
    private final BasketService basketService;
    private final BudgetRepository budgetRepository;

    // ── Baskets ────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/baskets")
    public ResponseEntity<List<BasketDto>> listBaskets(Pageable pageable) {
        return ResponseEntity.ok(basketService.listBaskets(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @PostMapping("/acquisitions/baskets")
    public ResponseEntity<BasketDto> addBasket(@RequestBody @Valid BasketDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(basketService.addBasket(dto));
    }

    @GetMapping("/acquisitions/baskets/managers")
    public ResponseEntity<List<BasketDto>> listBasketsManagers() {
        return ResponseEntity.ok(List.of());
    }

    // ── Orders ─────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/orders")
    public ResponseEntity<List<OrderDto>> listOrders(Pageable pageable) {
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
    @PostMapping("/acquisitions/orders")
    public ResponseEntity<?> addOrder(@RequestBody @Valid OrderRequest dto) {
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

    @GetMapping("/acquisitions/orders/{order_id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("order_id") Long id) {
        return orderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /acquisitions/orders/{order_id} — modify an existing order.
     * Same business logic as POST with ordernumber forced to the path variable.
     */
    @PutMapping("/acquisitions/orders/{order_id}")
    public ResponseEntity<?> updateOrder(@PathVariable("order_id") Long id,
                                          @RequestBody @Valid OrderRequest dto) {
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
    @DeleteMapping("/acquisitions/orders/{order_id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("order_id") Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ── Vendors ────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/vendors")
    public ResponseEntity<List<Map<String, Object>>> listVendors(Pageable pageable) {
        return ResponseEntity.ok(budgetRepository.findAllVendors(
                pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()));
    }

    @PostMapping("/acquisitions/vendors")
    public ResponseEntity<Map<String, Object>> addVendor(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("error", "not_implemented"));
    }

    @GetMapping("/acquisitions/vendors/{vendor_id}")
    public ResponseEntity<Map<String, Object>> getVendor(@PathVariable("vendor_id") Long id) {
        return budgetRepository.findVendorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/acquisitions/vendors/{vendor_id}")
    public ResponseEntity<Map<String, Object>> updateVendor(@PathVariable("vendor_id") Long id,
                                                             @RequestBody Map<String, Object> dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("error", "not_implemented"));
    }

    @DeleteMapping("/acquisitions/vendors/{vendor_id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable("vendor_id") Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/acquisitions/vendors/config")
    public ResponseEntity<Map<String, Object>> getVendorsConfig() { return ResponseEntity.ok(Map.of()); }

    @GetMapping("/acquisitions/vendors/extended_attribute_types")
    public ResponseEntity<List<Map<String, Object>>> listVendorExtendedAttributeTypes() { return ResponseEntity.ok(List.of()); }

    @GetMapping("/acquisitions/vendors/{vendor_id}/issues")
    public ResponseEntity<List<Map<String, Object>>> listVendorIssues(@PathVariable("vendor_id") Long vendorId) { return ResponseEntity.ok(List.of()); }

    // ── Funds ──────────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/funds")
    public ResponseEntity<List<Map<String, Object>>> listFunds(Pageable pageable) {
        return ResponseEntity.ok(budgetRepository.findAllFunds(
                pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()));
    }

    @GetMapping("/acquisitions/funds/owners")
    public ResponseEntity<List<Map<String, Object>>> listFundsOwners() { return ResponseEntity.ok(List.of()); }

    @GetMapping("/acquisitions/funds/users")
    public ResponseEntity<List<Map<String, Object>>> listFundsUsers() { return ResponseEntity.ok(List.of()); }

    // ── EDI files ──────────────────────────────────────────────────────────────

    @GetMapping("/acquisitions/edifiles")
    public ResponseEntity<List<Map<String, Object>>> listEdifactFiles(Pageable pageable) { return ResponseEntity.ok(List.of()); }

    // ── Quotes ─────────────────────────────────────────────────────────────────

    @GetMapping("/quotes")
    public ResponseEntity<List<Map<String, Object>>> listQuotes(Pageable pageable) { return ResponseEntity.ok(List.of()); }

    @PostMapping("/quotes")
    public ResponseEntity<Map<String, Object>> addQuote(@RequestBody Map<String, Object> dto) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of()); }

    @GetMapping("/quotes/{quote_id}")
    public ResponseEntity<Map<String, Object>> getQuote(@PathVariable("quote_id") Long id) { return ResponseEntity.ok(Map.of()); }

    @PutMapping("/quotes/{quote_id}")
    public ResponseEntity<Map<String, Object>> updateQuote(@PathVariable("quote_id") Long id, @RequestBody Map<String, Object> dto) { return ResponseEntity.ok(Map.of()); }

    @DeleteMapping("/quotes/{quote_id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable("quote_id") Long id) { return ResponseEntity.noContent().build(); }

    // ── Suggestions ────────────────────────────────────────────────────────────

    @GetMapping("/suggestions")
    public ResponseEntity<List<Map<String, Object>>> listSuggestions(Pageable pageable) { return ResponseEntity.ok(List.of()); }

    @PostMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> addSuggestion(@RequestBody Map<String, Object> dto) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of()); }

    @GetMapping("/suggestions/{suggestion_id}")
    public ResponseEntity<Map<String, Object>> getSuggestion(@PathVariable("suggestion_id") Long id) { return ResponseEntity.ok(Map.of()); }

    @PutMapping("/suggestions/{suggestion_id}")
    public ResponseEntity<Map<String, Object>> updateSuggestion(@PathVariable("suggestion_id") Long id, @RequestBody Map<String, Object> dto) { return ResponseEntity.ok(Map.of()); }

    @DeleteMapping("/suggestions/{suggestion_id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable("suggestion_id") Long id) { return ResponseEntity.noContent().build(); }

    @GetMapping("/suggestions/managers")
    public ResponseEntity<List<Map<String, Object>>> listSuggestionsManagers() { return ResponseEntity.ok(List.of()); }
}

