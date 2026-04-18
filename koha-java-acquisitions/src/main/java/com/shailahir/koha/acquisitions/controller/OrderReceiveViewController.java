package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for orderreceive.pl and showorder.pl
 *
 * <p>orderreceive.pl – loads the receive-order form data (order + invoice context).
 * showorder.pl      – shows a single order summary.
 *
 * <pre>
 * GET /api/v1/acquisitions/orders/{ordernumber}/receive  (orderreceive.pl)
 * GET /api/v1/acquisitions/orders/{ordernumber}          (showorder.pl)
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderReceiveViewController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns the full order context needed to populate the receive-order form.
     * Mirrors orderreceive.pl (initial page load – not the POST/save action,
     * which is handled by FinishReceiveController).
     */
    @GetMapping(
            value = "/{ordernumber}/receive",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> getOrderForReceive(@PathVariable Long ordernumber) {
        log.debug("GET order-receive ordernumber={}", ordernumber);
        return extRepo.findOrderForReceive(ordernumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns a single order summary.
     * Mirrors showorder.pl.
     */
    @GetMapping(
            value = "/{ordernumber}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> showOrder(@PathVariable Long ordernumber) {
        log.debug("GET showorder ordernumber={}", ordernumber);
        return extRepo.findOrderForReceive(ordernumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

