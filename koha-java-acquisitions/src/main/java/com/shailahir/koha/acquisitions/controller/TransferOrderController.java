package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.TransferOrderRequest;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import com.shailahir.koha.acquisitions.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for transferorder.pl
 *
 * <p>Transfers an order from its current basket to a different basket.
 * Both baskets must belong to the same vendor (enforcement is the caller's
 * responsibility; the Perl script performs this check in the UI layer).
 *
 * <pre>
 * POST /api/v1/acquisitions/orders/{ordernumber}/transfer
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/orders")
@RequiredArgsConstructor
@Slf4j
public class TransferOrderController {

    private final AcquisitionsExtRepository extRepo;
    private final OrderRepository orderRepo;

    /**
     * Moves an order to a different basket.
     * Mirrors transferorder.pl (op=cud-transfer).
     *
     * @param ordernumber the order to transfer
     * @param req         body containing the target basket number
     * @return 200 with the updated order, 404 if not found
     */
    @PostMapping(
            value = "/{ordernumber}/transfer",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> transferOrder(
            @PathVariable Long ordernumber,
            @RequestBody TransferOrderRequest req) {

        log.debug("POST transfer ordernumber={} to basketno={}", ordernumber, req.getToBasketno());
        int rows = extRepo.transferOrder(ordernumber, req.getToBasketno());
        if (rows == 0) {
            return ResponseEntity.notFound().build();
        }
        return orderRepo.findById(ordernumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

