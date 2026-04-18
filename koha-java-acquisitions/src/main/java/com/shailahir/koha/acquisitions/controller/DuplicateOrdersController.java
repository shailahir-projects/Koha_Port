package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.BasketRepository;
import com.shailahir.koha.acquisitions.repository.DuplicateOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller porting duplicate_orders.pl and histsearch.pl.
 *
 * <pre>
 *  GET  /acquisitions/orders/history                                — histsearch.pl
 *  GET  /acquisitions/baskets/{basketno}/duplicate-orders/search    — op=search/select
 *  POST /acquisitions/baskets/{basketno}/duplicate-orders           — op=cud-do_duplicate
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class DuplicateOrdersController {

    private final DuplicateOrderRepository dupeRepo;
    private final BasketRepository         basketRepo;

    /**
     * op=select — searches order history with filters and returns two lists:
     * <ul>
     *   <li>{@code result_orders} — orders matching the filters, excluding those
     *       already in {@code selected_ordernumbers}</li>
     *   <li>{@code selected_orders} — full details for the pre-selected orders</li>
     * </ul>
     * Mirrors the op=select block in duplicate_orders.pl (calls GetHistory twice).
     *
     * @param basketno            target basket (validated to exist)
     * @param filter              search filter parameters (all optional)
     * @param selectedOrdernumbers comma-separated list of already-selected order numbers
     */
    @GetMapping("/acquisitions/baskets/{basketno}/duplicate-orders/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> searchOrderHistory(
            @PathVariable Long basketno,
            @ModelAttribute OrderHistoryFilter filter,
            @RequestParam(value = "ordernumbers", required = false, defaultValue = "") String selectedOrdernumbers) {

        basketRepo.findById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));

        // Default date range: from = 1 year ago, to = today (mirrors dt_from_string defaults)
        applyDefaultDates(filter);

        Set<Long> selectedSet = parseOrdernumbers(selectedOrdernumbers);

        // All orders matching filters, excluding already-selected ones
        List<OrderHistoryDto> allResults = dupeRepo.getHistory(filter);
        List<OrderHistoryDto> resultOrders = allResults.stream()
                .filter(o -> !selectedSet.contains(o.getOrdernumber()))
                .collect(Collectors.toList());

        // Full details for selected orders
        List<OrderHistoryDto> selectedOrders = selectedSet.isEmpty()
                ? List.of()
                : dupeRepo.getHistoryByOrdernumbers(new ArrayList<>(selectedSet));

        return ResponseEntity.ok(Map.of(
                "basket",           basketRepo.findById(basketno).orElseThrow(),
                "result_orders",    resultOrders,
                "selected_orders",  selectedOrders,
                "filters",          filter,
                "ordernumbers",     selectedSet
        ));
    }

    /**
     * op=cud-do_duplicate — duplicates selected orders into the target basket.
     * <p>
     * For each order in {@code request.ordernumbers}:
     * <ol>
     *   <li>Copies all order fields into the target basket</li>
     *   <li>For fields NOT in {@code copy_existing_value}: applies the {@code all_*}
     *       default values from the request body</li>
     *   <li>Sets {@code orderstatus = 'new'}</li>
     * </ol>
     * Mirrors $original_order->duplicate_to($basket, $default_values).
     *
     * @param basketno target basket
     * @param request  list of order numbers to duplicate + field override defaults
     * @return list of newly created order history rows
     */
    @PostMapping("/acquisitions/baskets/{basketno}/duplicate-orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<Map<String, Object>> duplicateOrders(
            @PathVariable Long basketno,
            @RequestBody DuplicateOrdersRequest request) {

        basketRepo.findById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));

        if (request.getOrdernumbers() == null || request.getOrdernumbers().isEmpty()) {
            return ResponseEntity.ok(Map.of("new_orders", List.of(), "count", 0));
        }

        // Build defaults map from request — only fields NOT in copy_existing_value are applied
        List<String> copyFields = request.getCopyExistingValue() != null
                ? request.getCopyExistingValue() : List.of();

        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("currency",            request.getAllCurrency());
        defaults.put("budget_id",           request.getAllBudgetId());
        defaults.put("order_internalnote",  request.getAllOrderInternalnote());
        defaults.put("order_vendornote",    request.getAllOrderVendornote());
        defaults.put("sort1",               request.getAllSort1());
        defaults.put("sort2",               request.getAllSort2());

        List<Long> newOrdernumbers = new ArrayList<>();
        for (Long ordernumber : request.getOrdernumbers()) {
            try {
                Long newOrdernumber = dupeRepo.duplicateOrder(ordernumber, basketno, defaults, copyFields);
                newOrdernumbers.add(newOrdernumber);
                log.info("Duplicated order {} → new order {} in basket {}", ordernumber, newOrdernumber, basketno);
            } catch (Exception e) {
                log.warn("Failed to duplicate order {}: {}", ordernumber, e.getMessage());
            }
        }

        List<OrderHistoryDto> newOrders = dupeRepo.getHistoryByOrdernumbers(newOrdernumbers);

        return ResponseEntity.ok(Map.of(
                "new_orders", newOrders,
                "count", newOrders.size()
        ));
    }

    // ── histsearch.pl ──────────────────────────────────────────────────────────

    /**
     * Order history search — ports histsearch.pl.
     * <p>
     * Returns orders matching the supplied filters. When {@code do_search=false}
     * (default) no results are returned, mirroring histsearch.pl's behaviour of
     * only running GetHistory() when the form was submitted.
     * <p>
     * Supports all histsearch.pl filter parameters including the extras not in
     * duplicate_orders.pl: issn, internalnote, vendornote, is_standing,
     * managing_library, and additional_fields.
     *
     * @param doSearch  pass {@code true} to execute the search; {@code false} returns empty results
     * @param filter    all optional filter parameters
     */
    @GetMapping("/acquisitions/orders/history", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> orderHistory(
            @RequestParam(value = "do_search", defaultValue = "false") boolean doSearch,
            @ModelAttribute OrderHistoryFilter filter) {

        applyDefaultDates(filter);

        // mirrors: if ($filters->{orderstatus} eq "any") { $filters->{get_canceled_order} = 1 }
        // already handled inside getHistory() — "any" removes the cancelled filter

        List<OrderHistoryDto> orders = doSearch
                ? dupeRepo.getHistory(filter)
                : List.of();

        return ResponseEntity.ok(Map.of(
                "orders",      orders,
                "count",       orders.size(),
                "filters",     filter,
                "search_done", doSearch
        ));
    }

    // ── duplicate_orders.pl ────────────────────────────────────────────────────

    private void applyDefaultDates(OrderHistoryFilter filter) {
        if (filter.getFromPlacedOn() == null || filter.getFromPlacedOn().isBlank()) {
            filter.setFromPlacedOn(LocalDate.now().minusYears(1).toString());
        }
        if (filter.getToPlacedOn() == null || filter.getToPlacedOn().isBlank()) {
            filter.setToPlacedOn(LocalDate.now().toString());
        }
    }

    private Set<Long> parseOrdernumbers(String csv) {
        if (csv == null || csv.isBlank()) return Set.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }
}

