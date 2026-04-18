package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for uncertainprice.pl
 *
 * <p>Lists orders whose price is flagged as uncertain (uncertainprice=1)
 * so that acquisitions staff can confirm or update them.
 *
 * <pre>
 * GET /api/v1/acquisitions/orders/uncertain-prices[?booksellerid=]
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/orders")
@RequiredArgsConstructor
@Slf4j
public class UncertainPriceController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns all active orders with uncertain prices.
     * Mirrors uncertainprice.pl: joins aqorders + aqbasket, filters uncertainprice=1.
     *
     * @param booksellerid optional vendor filter
     */
    @GetMapping(
            value = "/uncertain-prices",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<List<OrderDto>> getUncertainPrices(
            @RequestParam(required = false) Long booksellerid) {

        log.debug("GET uncertain-prices booksellerid={}", booksellerid);
        return ResponseEntity.ok(extRepo.findUncertainPriceOrders(booksellerid));
    }
}

