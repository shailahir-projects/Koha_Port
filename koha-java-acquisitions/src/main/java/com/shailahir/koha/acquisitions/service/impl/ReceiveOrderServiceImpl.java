package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.ReceiveOrderRequest;
import com.shailahir.koha.acquisitions.dto.ReceiveOrderResult;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import com.shailahir.koha.acquisitions.repository.ReceiveOrderRepository;
import com.shailahir.koha.acquisitions.service.ReceiveOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Implementation of {@link ReceiveOrderService}.
 * Ports finishreceive.pl and orderreceive.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiveOrderServiceImpl implements ReceiveOrderService {

    private final ReceiveOrderRepository receiveRepo;
    private final AcquisitionsExtRepository extRepo;

    @Override
    public Optional<OrderDto> getOrderForReceive(Long ordernumber) {
        log.debug("Entering getOrderForReceive - {}", ordernumber);
        return extRepo.findOrderForReceive(ordernumber);
    }

    @Override
    @Transactional
    public ReceiveOrderResult receiveOrder(Long ordernumber, ReceiveOrderRequest req) {
        log.info("Receiving order {} invoiceid={}", ordernumber, req.getInvoiceid());

        Map<String, Object> order = receiveRepo.findOrder(ordernumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + ordernumber));

        Map<String, Object> basket = receiveRepo.findBasket(
                ((Number) order.get("basketno")).longValue())
                .orElseThrow(() -> new NoSuchElementException("Basket not found"));

        boolean listIncGst = receiveRepo.vendorListIncGst(
                ((Number) basket.get("booksellerid")).longValue());

        BigDecimal taxRate = req.getTaxRate() != null ? req.getTaxRate() : BigDecimal.ZERO;
        BigDecimal[] prices = receiveRepo.calculateReceivingPrices(req.getUnitprice(), taxRate, listIncGst);
        // prices[0]=unitprice, prices[1]=tax_excluded, prices[2]=tax_included

        String dateReceived = req.getDatereceived() != null ? req.getDatereceived() : LocalDate.now().toString();
        int qty = req.getQuantityReceived() != null ? req.getQuantityReceived() : 1;

        Long newOrdernumber = receiveRepo.modReceiveOrder(
                order,
                qty,
                req.getInvoiceid(),
                req.getBudgetId(),
                dateReceived,
                prices[1],          // unitpriceTaxExcluded
                prices[2],          // unitpriceTaxIncluded
                BigDecimal.ZERO,    // taxValueOnReceiving – computed by repo
                taxRate,
                prices[0],          // unitprice
                req.getReplacementprice(),
                req.getOrderInternalnote(),
                req.getInvoiceUnitprice(),
                req.getInvoiceCurrency()
        );

        if (req.getSuggestionId() != null) {
            receiveRepo.updateSuggestionReason(req.getSuggestionId(),
                    req.getSuggestionReason() != null ? req.getSuggestionReason() : "AVAILABLE");
        }

        receiveRepo.logReceipt(newOrdernumber, "Order received via API");

        return ReceiveOrderResult.builder()
                .ordernumber(newOrdernumber)
                .originalOrdernumber(ordernumber)
                .invoiceid(req.getInvoiceid())
                .datereceived(dateReceived)
                .quantityReceived(qty)
                .unitpriceTaxExcluded(prices[1])
                .unitpriceTaxIncluded(prices[2])
                .build();
    }
}
