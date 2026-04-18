package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.OrderHistoryDto;
import com.shailahir.koha.acquisitions.dto.OrderHistoryFilter;

import java.util.List;
import java.util.Map;

/**
 * Business logic for order history search and order duplication.
 * Ports duplicate_orders.pl and histsearch.pl.
 */
public interface OrderHistoryService {

    /** Searches order history with filters — mirrors histsearch.pl. */
    List<OrderHistoryDto> searchHistory(OrderHistoryFilter filter);

    /**
     * Duplicates selected orders into a target basket.
     * Mirrors duplicate_orders.pl (op=cud-duplicate).
     *
     * @param ordernumbers  orders to duplicate
     * @param targetBasketno target basket
     * @param defaults      field overrides (currency, budget_id, etc.)
     * @param copyFields    field names to copy unchanged from the source order
     * @return list of newly created order numbers
     */
    List<Long> duplicateOrders(List<Long> ordernumbers, Long targetBasketno,
            Map<String, Object> defaults, List<String> copyFields);
}

