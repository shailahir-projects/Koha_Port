package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.CancelOrderRequest;
import com.shailahir.koha.acquisitions.dto.CancelOrderResult;
import com.shailahir.koha.acquisitions.repository.CancelOrderRepository;
import com.shailahir.koha.acquisitions.service.CancelOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Implementation of {@link CancelOrderService}.
 * Ports cancelorder.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CancelOrderServiceImpl implements CancelOrderService {

    private final CancelOrderRepository cancelOrderRepo;

    @Override
    @Transactional
    public CancelOrderResult cancelOrder(Long ordernumber, CancelOrderRequest req) {
        log.info("Cancelling order {} reason={}", ordernumber, req.getReason());

        Map<String, Object> order = cancelOrderRepo.findOrderById(ordernumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + ordernumber));

        cancelOrderRepo.cancelOrder(ordernumber, req.getReason());

        boolean itemsDeleted = false;
        boolean biblioDeleted = false;

        // Optionally delete attached items (CancelOrderRequest has deleteBiblio but not deleteItems)
        int deletable = cancelOrderRepo.countDeletableItems(ordernumber);
        if (deletable > 0) {
            itemsDeleted = cancelOrderRepo.deleteOrderItems(ordernumber);
        }

        Long biblionumber = order.get("biblionumber") != null
                ? ((Number) order.get("biblionumber")).longValue() : null;

        if (Boolean.TRUE.equals(req.getDeleteBiblio()) && biblionumber != null) {
            String deleteResult = cancelOrderRepo.tryDeleteBiblio(biblionumber);
            biblioDeleted = "deleted".equals(deleteResult);
        }

        cancelOrderRepo.logCancellation(ordernumber);

        return CancelOrderResult.builder()
                .ordernumber(ordernumber)
                .success(true)
                .biblionumber(biblionumber)
                .build();
    }
}
