package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.OrderHistoryDto;
import com.shailahir.koha.acquisitions.dto.OrderHistoryFilter;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for order history search and duplication.
 * Ported from duplicate_orders.pl and histsearch.pl.
 */
public interface IDuplicateOrderRepository {

    List<OrderHistoryDto> getHistory(OrderHistoryFilter filter);
    List<OrderHistoryDto> getHistoryByOrdernumbers(List<Long> ordernumbers);
    Long duplicateOrder(Long ordernumber, Long targetBasketno,
            Map<String, Object> defaults, List<String> copyFields);
}

