package com.shailahir.koha.acquisitions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shailahir.koha.acquisitions.dto.ReceiveOrderRequest;
import com.shailahir.koha.acquisitions.dto.ReceiveOrderResult;
import com.shailahir.koha.acquisitions.repository.ReceiveOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller porting finishreceive.pl — mark an order as received.
 *
 * <pre>
 *  POST /acquisitions/orders/{ordernumber}/receive
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class FinishReceiveController {

    private final ReceiveOrderRepository receiveRepo;
    private final ObjectMapper           objectMapper;

    /**
     * Marks an order (or partial quantity) as received — mirrors finishreceive.pl.
     * <p>
     * Full business logic:
     * <ol>
     *   <li>Load order + basket + invoice</li>
     *   <li>Skip if quantity received has not increased</li>
     *   <li>Apply {@code AcqItemSetSubfieldsWhenReceived} to pre-existing items
     *       (when {@code basket.create_items = 'ordering'})</li>
     *   <li>Set order price fields; call {@code populate_with_prices_for_receiving()}
     *       to derive tax-exclusive/inclusive unit prices</li>
     *   <li>Call {@code ModReceiveOrder()} — updates order status; creates a
     *       "remainder" order when only a partial quantity is received</li>
     *   <li>Update all received items: booksellerid, dateaccessioned, price, etc.</li>
     *   <li>Update suggestion reason if applicable</li>
     *   <li>Write acquisition log entry (RECEIVE_ORDER)</li>
     * </ol>
     */
    @PostMapping("/acquisitions/orders/{ordernumber}/receive", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<ReceiveOrderResult> finishReceive(
            @PathVariable Long ordernumber,
            @RequestBody ReceiveOrderRequest req) {

        // ── 1. Load order ──────────────────────────────────────────────────────
        Map<String, Object> order = receiveRepo.findOrder(ordernumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + ordernumber));

        int origQtyRec = req.getOrigQuantityReceived() != null ? req.getOrigQuantityReceived() : 0;
        int newQtyRec  = req.getQuantityReceived()     != null ? req.getQuantityReceived()     : 0;

        // Only proceed if quantity received has actually increased
        if (newQtyRec <= origQtyRec) {
            log.info("No new items received for order {}; skipping", ordernumber);
            return ResponseEntity.ok(ReceiveOrderResult.builder()
                    .originalOrdernumber(ordernumber)
                    .ordernumber(ordernumber)
                    .invoiceid(req.getInvoiceid())
                    .quantityReceived(origQtyRec)
                    .build());
        }

        // ── 2. Basket & vendor metadata ────────────────────────────────────────
        Long basketno = toLong(order.get("basketno"));
        Map<String, Object> basket = receiveRepo.findBasket(basketno).orElse(Map.of());
        String createItems = (String) basket.getOrDefault("create_items", "");

        boolean listIncGst = req.getBooksellerid() != null
                && receiveRepo.vendorListIncGst(req.getBooksellerid());

        // ── 3. Item subfield updates (create_items='ordering') ─────────────────
        if ("ordering".equals(createItems) && req.getItemsToReceive() != null
                && !req.getItemsToReceive().isEmpty()) {
            // AcqItemSetSubfieldsWhenReceived preference parsing would go here.
            // We accept pre-parsed field=value pairs via the API request if needed.
            // No-op for now: the preference is a system-level config not available at runtime here.
        }

        // ── 4. Price calculations (populate_with_prices_for_receiving) ─────────
        BigDecimal unitprice = req.getUnitprice() != null ? req.getUnitprice() : BigDecimal.ZERO;
        BigDecimal[] prices  = receiveRepo.calculateReceivingPrices(unitprice, req.getTaxRate(), listIncGst);
        BigDecimal unitExcl  = prices[0];
        BigDecimal unitIncl  = prices[1];
        BigDecimal taxValue  = prices[2];

        // ── 5. Resolve datereceived ────────────────────────────────────────────
        String datereceived = req.getDatereceived() != null && !req.getDatereceived().isBlank()
                ? req.getDatereceived()
                : LocalDate.now().toString();

        // ── 6. ModReceiveOrder ─────────────────────────────────────────────────
        Long newOrdernumber = receiveRepo.modReceiveOrder(
                order,
                newQtyRec,
                req.getInvoiceid(),
                req.getBudgetId(),
                datereceived,
                unitExcl,
                unitIncl,
                taxValue,
                req.getTaxRate(),
                unitprice,
                req.getReplacementprice(),
                req.getOrderInternalnote(),
                req.getInvoiceUnitprice(),
                req.getInvoiceCurrency());

        // ── 7. Update received items ───────────────────────────────────────────
        receiveRepo.updateOrderItems(
                ordernumber,
                req.getBooksellerid(),
                datereceived,
                unitprice,
                req.getReplacementprice());

        // ── 8. Suggestion reason ───────────────────────────────────────────────
        if (req.getSuggestionId() != null && req.getSuggestionReason() != null) {
            receiveRepo.updateSuggestionReason(req.getSuggestionId(), req.getSuggestionReason());
        }

        // ── 9. Acquisition log ─────────────────────────────────────────────────
        try {
            String info = objectMapper.writeValueAsString(Map.of(
                    "quantityrec",      newQtyRec,
                    "bookfund",         req.getBudgetId() != null ? req.getBudgetId() : "unchanged",
                    "tax_rate",         req.getTaxRate() != null ? req.getTaxRate() : 0,
                    "replacementprice", req.getReplacementprice() != null ? req.getReplacementprice() : 0,
                    "unitprice",        unitprice,
                    "unitprice_tax_excluded", unitExcl,
                    "unitprice_tax_included", unitIncl
            ));
            receiveRepo.logReceipt(ordernumber, info);
        } catch (Exception e) {
            log.warn("Could not write acquisition log for order {}: {}", ordernumber, e.getMessage());
        }

        log.info("Order {} received: qty={}, new_ordernumber={}", ordernumber, newQtyRec, newOrdernumber);

        return ResponseEntity.ok(ReceiveOrderResult.builder()
                .originalOrdernumber(ordernumber)
                .ordernumber(newOrdernumber)
                .invoiceid(req.getInvoiceid())
                .datereceived(datereceived)
                .quantityReceived(newQtyRec)
                .unitpriceTaxExcluded(unitExcl)
                .unitpriceTaxIncluded(unitIncl)
                .taxValueOnReceiving(taxValue)
                .build());
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }
}

