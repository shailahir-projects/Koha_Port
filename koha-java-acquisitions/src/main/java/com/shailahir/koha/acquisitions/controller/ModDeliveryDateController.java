package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.ModDeliveryDateRequest;
import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for moddeliverydate.pl
 *
 * <p>Provides a dedicated PATCH endpoint to update only the
 * {@code estimated_delivery_date} of an existing acquisition order
 * (typically after its basket has been closed).
 *
 * <pre>
 * Perl equivalent: acqui/moddeliverydate.pl  op=cud-save
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/orders")
@RequiredArgsConstructor
@Slf4j
public class ModDeliveryDateController {

    private final OrderService orderService;

    /**
     * PATCH /api/v1/acquisitions/orders/{ordernumber}/delivery-date
     *
     * <p>Update (or clear) the estimated delivery date of a single order.
     * Accepts JSON or XML; produces JSON and XML.
     *
     * <p>Request body example (JSON):
     * <pre>
     * { "estimated_delivery_date": "2026-06-30" }
     * </pre>
     * Set {@code estimated_delivery_date} to {@code null} to clear the date.
     *
     * @param ordernumber the order identifier (aqorders.ordernumber)
     * @param request     body containing the new date (may be null to clear)
     * @return 200 OK with the updated order, or 404 if the order does not exist
     */
    @PatchMapping(
            value = "/{ordernumber}/delivery-date",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> updateDeliveryDate(
            @PathVariable Long ordernumber,
            @RequestBody ModDeliveryDateRequest request) {

        log.debug("PATCH delivery-date ordernumber={} date={}", ordernumber,
                request.getEstimatedDeliveryDate());

        OrderDto updated = orderService.updateDeliveryDate(
                ordernumber, request.getEstimatedDeliveryDate());

        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/v1/acquisitions/orders/{ordernumber}/delivery-date
     *
     * <p>Retrieve just the current estimated delivery date of an order.
     * Mirrors the initial form-display branch of moddeliverydate.pl.
     *
     * @param ordernumber the order identifier
     * @return 200 OK with the order DTO (includes {@code estimated_delivery_date}),
     *         or 404 if not found
     */
    @GetMapping(
            value = "/{ordernumber}/delivery-date",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> getDeliveryDate(@PathVariable Long ordernumber) {
        log.debug("GET delivery-date ordernumber={}", ordernumber);
        return orderService.getOrder(ordernumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

