package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.CancelOrderRequest;
import com.shailahir.koha.acquisitions.dto.CancelOrderResult;
import com.shailahir.koha.acquisitions.repository.CancelOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller porting cancelorder.pl.
 *
 * <pre>
 *  GET  /acquisitions/orders/{ordernumber}/cancel  — confirmation form data
 *  POST /acquisitions/orders/{ordernumber}/cancel  — op=cud-confirmcancel
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CancelOrderController {

    private final CancelOrderRepository cancelOrderRepo;

    /**
     * Returns order + basket info needed to render the cancel-confirmation form.
     * Mirrors the initial page load (no op) of cancelorder.pl.
     */
    @GetMapping("/acquisitions/orders/{ordernumber}/cancel")
    public ResponseEntity<Map<String, Object>> getCancelForm(
            @PathVariable Long ordernumber) {

        Map<String, Object> order = cancelOrderRepo.findOrderById(ordernumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + ordernumber));

        return ResponseEntity.ok(Map.of(
                "ordernumber", ordernumber,
                "basketno",    order.getOrDefault("basketno", ""),
                "biblionumber", order.getOrDefault("biblionumber", ""),
                "orderstatus", order.getOrDefault("orderstatus", ""),
                "datecancellationprinted", order.getOrDefault("datecancellationprinted", "")
        ));
    }

    /**
     * Confirms and executes the order cancellation — op=cud-confirmcancel.
     * <p>
     * Business logic (mirrors cancelorder.pl + Koha::Acquisition::Order->cancel()):
     * <ol>
     *   <li>Validate the order exists and has not already been cancelled</li>
     *   <li>Set {@code orderstatus='cancelled'}, {@code datecancellationprinted=NOW()},
     *       {@code cancellationreason=reason}</li>
     *   <li>When {@code delete_biblio=true}: attempt to delete linked items and then
     *       the biblio; populate error codes when constraints prevent deletion</li>
     *   <li>Write an acquisition log entry (ACQUISITIONS / CANCEL_ORDER)</li>
     * </ol>
     *
     * @param ordernumber order to cancel
     * @param request     reason + delete_biblio flag
     * @return result with success flag and any error codes
     */
    @PostMapping("/acquisitions/orders/{ordernumber}/cancel")
    @Transactional
    public ResponseEntity<CancelOrderResult> confirmCancel(
            @PathVariable Long ordernumber,
            @RequestBody CancelOrderRequest request) {

        // ── 1. Validate order ──────────────────────────────────────────────────
        Map<String, Object> order = cancelOrderRepo.findOrderById(ordernumber)
                .orElse(null);

        if (order == null) {
            return ResponseEntity.ok(CancelOrderResult.builder()
                    .ordernumber(ordernumber)
                    .success(false)
                    .error("error_order_not_found")
                    .build());
        }

        // Already cancelled?
        if (order.get("datecancellationprinted") != null) {
            return ResponseEntity.ok(CancelOrderResult.builder()
                    .ordernumber(ordernumber)
                    .basketno(toLong(order.get("basketno")))
                    .biblionumber(toLong(order.get("biblionumber")))
                    .success(false)
                    .error("error_order_already_cancelled")
                    .build());
        }

        Long basketno     = toLong(order.get("basketno"));
        Long biblionumber = toLong(order.get("biblionumber"));

        // ── 2. Cancel the order ────────────────────────────────────────────────
        cancelOrderRepo.cancelOrder(ordernumber, request.getReason());
        log.info("Order {} cancelled. Reason: {}", ordernumber, request.getReason());

        // ── 3. Optional biblio/item deletion ──────────────────────────────────
        String errorCode = null;
        if (Boolean.TRUE.equals(request.getDeleteBiblio()) && biblionumber != null) {
            // Try to delete linked items first
            cancelOrderRepo.deleteOrderItems(ordernumber);
            // Then attempt biblio deletion
            errorCode = cancelOrderRepo.tryDeleteBiblio(biblionumber);
            if (errorCode != null) {
                log.info("Could not delete biblio {} after cancelling order {}: {}",
                        biblionumber, ordernumber, errorCode);
            }
        }

        // ── 4. Acquisition log ─────────────────────────────────────────────────
        cancelOrderRepo.logCancellation(ordernumber);

        // ── 5. Build result ────────────────────────────────────────────────────
        if (errorCode != null) {
            return ResponseEntity.ok(CancelOrderResult.builder()
                    .ordernumber(ordernumber)
                    .basketno(basketno)
                    .biblionumber(biblionumber)
                    .success(false)
                    .error(errorCode)
                    .errorDetail("Biblio or items could not be deleted due to existing references")
                    .build());
        }

        return ResponseEntity.ok(CancelOrderResult.builder()
                .ordernumber(ordernumber)
                .basketno(basketno)
                .biblionumber(biblionumber)
                .success(true)
                .build());
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }
}

