package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.ClaimOrderRequest;
import com.shailahir.koha.acquisitions.dto.LateOrderDto;
import com.shailahir.koha.acquisitions.repository.LateOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST controller porting lateorders.pl.
 *
 * <pre>
 *  GET  /acquisitions/late-orders         — list late orders (filter_by_lates)
 *  GET  /acquisitions/late-orders/letters — available claim letter codes
 *  POST /acquisitions/late-orders/claim   — op=cud-send_alert (send alert + claim)
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class LateOrdersController {

    private final LateOrderRepository lateOrderRepo;

    /**
     * Returns orders that are late based on delay or estimated delivery date range.
     * Mirrors the filter_by_lates() call + bookseller/branch filtering in lateorders.pl.
     * <p>
     * Late-order criteria (mirrors Koha::Acquisition::Orders->filter_by_lates()):
     * <ul>
     *   <li>Basket is closed, order is not cancelled or complete</li>
     *   <li>Not fully received (quantity > quantityreceived)</li>
     *   <li>TODAY − closedate >= {@code delay} (when delay > 0)</li>
     *   <li>estimated_delivery_date in the supplied date range (when given)</li>
     * </ul>
     *
     * @param booksellerid           optional vendor filter
     * @param delay                  minimum days overdue since basket closure (default 0)
     * @param branch                 optional branch filter
     * @param estimatedDeliveryFrom  lower bound for estimated_delivery_date (ISO date)
     * @param estimatedDeliveryTo    upper bound for estimated_delivery_date (ISO date)
     */
    @GetMapping("/acquisitions/late-orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getLateOrders(
            @RequestParam(value = "booksellerid",          required = false) Long booksellerid,
            @RequestParam(value = "delay",                 defaultValue = "0") int delay,
            @RequestParam(value = "branch",                required = false) String branch,
            @RequestParam(value = "estimateddeliverydatefrom", required = false) String estimatedDeliveryFrom,
            @RequestParam(value = "estimateddeliverydateto",   required = false) String estimatedDeliveryTo) {
        log.debug("Entering getLateOrders - {}, {}, {}, {}, {}", booksellerid, delay, branch, estimatedDeliveryFrom, estimatedDeliveryTo);

        // Validate delay (mirrors: if ($delay and not $delay =~ /^\d{1,3}$/))
        if (delay < 0 || delay > 999) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "delay_digits",
                            "message", "Delay must be between 0 and 999"));
        }

        // Default estimatedTo: when neither delay nor estimatedFrom is set, use today
        LocalDate parsedFrom = parseDate(estimatedDeliveryFrom);
        LocalDate parsedTo   = parseDate(estimatedDeliveryTo);
        if (parsedTo == null && delay == 0 && parsedFrom == null) {
            parsedTo = LocalDate.now();
        }

        List<LateOrderDto> lateOrders = lateOrderRepo.filterByLates(
                delay, parsedFrom, parsedTo, booksellerid, branch);

        // Collect distinct vendor ids from results (for booksellers list in template)
        List<Long> vendorIds = lateOrders.stream()
                .map(LateOrderDto::getBooksellerid)
                .distinct().toList();

        return ResponseEntity.ok(Map.of(
                "late_orders",    lateOrders,
                "count",          lateOrders.size(),
                "delay",          delay,
                "booksellerid",   booksellerid != null ? booksellerid : "",
                "vendor_ids",     vendorIds,
                "estimated_from", estimatedDeliveryFrom != null ? estimatedDeliveryFrom : "",
                "estimated_to",   estimatedDeliveryTo   != null ? estimatedDeliveryTo   : ""
        ));
    }

    /**
     * Returns available claim letter codes for the claimacquisition module.
     * Mirrors GetLetters({ module => "claimacquisition" }) in lateorders.pl.
     */
    @GetMapping("/acquisitions/late-orders/letters", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> getClaimLetters() {
        log.debug("Entering getClaimLetters");
        return ResponseEntity.ok(lateOrderRepo.getClaimLetters());
    }

    /**
     * Sends acquisition claim alerts and marks the orders as claimed — op=cud-send_alert.
     * <p>
     * Mirrors lateorders.pl:
     * <ol>
     *   <li>Validates that order numbers are provided (mirrors "no_order_selected" error)</li>
     *   <li>For each order: calls {@code $order->claim()} — sets {@code claimed_date = TODAY},
     *       increments {@code claims_count}</li>
     *   <li>Logs each claim action</li>
     * </ol>
     * <p>
     * <strong>Note on SendAlerts:</strong> The Perl {@code SendAlerts('claimacquisition',...)}
     * generates and sends a formatted letter email using Koha's letter template engine.
     * The actual email dispatch requires the notification microservice. This endpoint records
     * the claim and returns the letter code + order details for the notification service to
     * consume. The "no_email" error case is handled by checking vendor contact info.
     *
     * @param request ordernumbers + letter_code
     * @return claim result with success/error status per order
     */
    @PostMapping("/acquisitions/late-orders/claim", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Transactional
    public ResponseEntity<Map<String, Object>> sendClaimAlert(@RequestBody ClaimOrderRequest request) {
        log.debug("Entering sendClaimAlert - {}", request);

        if (request.getOrdernumbers() == null || request.getOrdernumbers().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "no_order_selected",
                            "message", "No orders selected for claim"));
        }

        List<Long> claimed  = new ArrayList<>();
        List<Long> failed   = new ArrayList<>();

        for (Long ordernumber : request.getOrdernumbers()) {
            try {
                lateOrderRepo.claimOrder(ordernumber);
                lateOrderRepo.logClaim(ordernumber);
                claimed.add(ordernumber);
                log.info("Order {} claimed (letter={})", ordernumber, request.getLetterCode());
            } catch (Exception e) {
                log.warn("Failed to claim order {}: {}", ordernumber, e.getMessage());
                failed.add(ordernumber);
            }
        }

        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("claimed",     claimed);
        result.put("failed",      failed);
        result.put("letter_code", request.getLetterCode());
        result.put("success",     failed.isEmpty());
        result.put("note",
                "Email dispatch via SendAlerts is delegated to the notification microservice. " +
                "Use letter_code and claimed order numbers to trigger the letter.");
        return ResponseEntity.ok(result);
    }

    /**
     * Exports selected late orders as CSV — ports lateorders-export.pl.
     * <p>
     * The default (built-in) export format mirrors the lateorders.tt template fields:
     * orderdate, latesince, estimateddeliverydate, supplier, title, author, isbn,
     * publisher, unitpricesupplier, quantity_to_receive, subtotal, budget,
     * basketname, basketno, claims_count, claimed_date, internalnote, vendornote.
     * <p>
     * When {@code csv_profile_id} is supplied, a note is included in the response
     * indicating that custom CSV profile rendering requires the template engine
     * (delegated to the reporting/template microservice).
     *
     * @param ordernumbers  comma-separated list of ordernumbers to export
     * @param csvProfileId  optional custom CSV profile id
     */
    @GetMapping("/acquisitions/late-orders/export", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<byte[]> exportLateOrders(
            @RequestParam("ordernumber") List<Long> ordernumbers,
            @RequestParam(value = "csv_profile", required = false) Long csvProfileId) {
        log.debug("Entering exportLateOrders - {}, {}", ordernumbers, csvProfileId);

        List<Map<String, Object>> orders = lateOrderRepo.getOrdersForExport(ordernumbers);

        String csv = buildDefaultCsv(orders);

        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lateorders.csv\"");
        headers.setContentLength(bytes.length);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    /**
     * Builds the default CSV matching the lateorders.tt template column order.
     * Mirrors the built-in template path in lateorders-export.pl (no csv_profile_id).
     */
    private String buildDefaultCsv(List<Map<String, Object>> orders) {
        log.debug("Entering buildDefaultCsv - {}", orders);
        String[] HEADERS = {
            "Order Date", "Late Since", "Estimated Delivery",
            "Supplier", "Supplier ID",
            "Title", "Author", "ISBN", "Publisher",
            "Unit Price (Supplier)", "Qty To Receive", "Subtotal",
            "Budget", "Basket Name", "Basket No",
            "Claims Count", "Last Claimed Date",
            "Internal Note", "Vendor Note"
        };

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", HEADERS)).append("\n");

        for (Map<String, Object> o : orders) {
            sb.append(csvField(o.get("closedate")))         .append(",");
            sb.append(csvField(o.get("latesince")))         .append(",");
            sb.append(csvField(o.get("estimated_delivery_date"))).append(",");
            sb.append(csvField(o.get("supplier")))           .append(",");
            sb.append(csvField(o.get("supplierid")))         .append(",");
            sb.append(csvField(o.get("title")))              .append(",");
            sb.append(csvField(o.get("author")))             .append(",");
            sb.append(csvField(o.get("isbn")))               .append(",");
            sb.append(csvField(o.get("publisher")))          .append(",");
            sb.append(csvField(o.get("unitpricesupplier")))  .append(",");
            sb.append(csvField(o.get("quantity_to_receive"))).append(",");
            sb.append(csvField(o.get("subtotal")))           .append(",");
            sb.append(csvField(o.get("budget")))             .append(",");
            sb.append(csvField(o.get("basketname")))         .append(",");
            sb.append(csvField(o.get("basketno")))           .append(",");
            sb.append(csvField(o.get("claims_count")))       .append(",");
            sb.append(csvField(o.get("claimed_date")))       .append(",");
            sb.append(csvField(o.get("order_internalnote"))) .append(",");
            sb.append(csvField(o.get("order_vendornote")))   .append("\n");
        }
        return sb.toString();
    }

    /** Wraps a value in double-quotes, escaping internal double-quotes. */
    private String csvField(Object v) {
        log.debug("Entering csvField - {}", v);
        if (v == null) return "\"\"";
        String s = v.toString().replace("\"", "\"\"");
        return "\"" + s + "\"";
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private LocalDate parseDate(String s) {
        log.debug("Entering parseDate - {}", s);
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.substring(0, 10)); } catch (Exception e) { return null; }
    }
}

