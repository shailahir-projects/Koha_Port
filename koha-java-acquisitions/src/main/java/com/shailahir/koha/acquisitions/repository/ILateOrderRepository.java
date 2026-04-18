package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.LateOrderDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for late orders / claims.
 * Ported from lateorders.pl and lateorders-export.pl.
 */
public interface ILateOrderRepository {

    List<LateOrderDto> filterByLates(int delay, LocalDate estimatedFrom,
            LocalDate estimatedTo, Long booksellerid, Long budgetId);
    void claimOrder(Long ordernumber);
    List<Map<String, Object>> getOrdersForExport(List<Long> ordernumbers);
    List<Map<String, Object>> getClaimLetters();
    String getVendorEmail(Long ordernumber);
    void logClaim(Long ordernumber);
}

