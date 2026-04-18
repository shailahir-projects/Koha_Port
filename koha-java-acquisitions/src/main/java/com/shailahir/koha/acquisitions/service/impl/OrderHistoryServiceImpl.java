package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.OrderHistoryDto;
import com.shailahir.koha.acquisitions.dto.OrderHistoryFilter;
import com.shailahir.koha.acquisitions.repository.DuplicateOrderRepository;
import com.shailahir.koha.acquisitions.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link OrderHistoryService}.
 * Ports histsearch.pl and duplicate_orders.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private final DuplicateOrderRepository dupeRepo;

    @Override
    public List<OrderHistoryDto> searchHistory(OrderHistoryFilter filter) {
        log.debug("searchHistory filter={}", filter);
        // Apply default date range if not set
        if (filter.getFromPlacedOn() == null || filter.getFromPlacedOn().isBlank()) {
            filter.setFromPlacedOn(LocalDate.now().minusYears(1).toString());
        }
        if (filter.getToPlacedOn() == null || filter.getToPlacedOn().isBlank()) {
            filter.setToPlacedOn(LocalDate.now().toString());
        }
        return dupeRepo.getHistory(filter);
    }

    @Override
    @Transactional
    public List<Long> duplicateOrders(List<Long> ordernumbers, Long targetBasketno,
            Map<String, Object> defaults, List<String> copyFields) {
        log.info("Duplicating {} orders into basket {}", ordernumbers.size(), targetBasketno);
        List<Long> newOrdernumbers = new ArrayList<>();
        for (Long ordernumber : ordernumbers) {
            Long newOrdernumber = dupeRepo.duplicateOrder(
                    ordernumber, targetBasketno, defaults, copyFields);
            newOrdernumbers.add(newOrdernumber);
            log.debug("Duplicated order {} -> {}", ordernumber, newOrdernumber);
        }
        return newOrdernumbers;
    }
}

