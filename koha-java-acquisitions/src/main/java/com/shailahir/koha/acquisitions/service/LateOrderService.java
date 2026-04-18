package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.LateOrderDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Business logic for late orders and claims.
 * Ports lateorders.pl and lateorders-export.pl.
 */
public interface LateOrderService {

    /** Filters late orders with optional constraints — mirrors lateorders.pl. */
    List<LateOrderDto> getLateOrders(int delay, LocalDate estimatedFrom, LocalDate estimatedTo,
            Long booksellerid, Long budgetId);

    /** Sends a claim notification for the given order numbers. */
    void claimOrders(List<Long> ordernumbers);

    /** Returns raw order data suitable for EDI/CSV export. */
    List<Map<String, Object>> exportOrders(List<Long> ordernumbers);

    /** Returns available claim letter templates. */
    List<Map<String, Object>> getClaimLetters();
}

