package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.ModOrderNotesRequest;
import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import com.shailahir.koha.acquisitions.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for modordernotes.pl
 *
 * <p>Updates the internal and vendor notes on an existing acquisition order.
 *
 * <pre>
 * PATCH /api/v1/acquisitions/orders/{ordernumber}/notes
 * GET   /api/v1/acquisitions/orders/{ordernumber}
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/orders")
@RequiredArgsConstructor
@Slf4j
public class ModOrderNotesController {

    private final AcquisitionsExtRepository extRepo;
    private final OrderRepository orderRepo;

    /**
     * Update the internal note and/or vendor note of an order.
     * Mirrors modordernotes.pl (op=cud-save).
     */
    @PatchMapping(
            value = "/{ordernumber}/notes",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> updateNotes(
            @PathVariable Long ordernumber,
            @RequestBody ModOrderNotesRequest req) {

        log.debug("PATCH notes ordernumber={}", ordernumber);
        int rows = extRepo.updateOrderNotes(ordernumber,
                req.getOrderInternalnote(), req.getOrderVendornote());
        if (rows == 0) {
            return ResponseEntity.notFound().build();
        }
        return orderRepo.findById(ordernumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Read the current notes for an order (mirrors the form-display branch).
     */
    @GetMapping(
            value = "/{ordernumber}/notes",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> getNotes(@PathVariable Long ordernumber) {
        return orderRepo.findById(ordernumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

