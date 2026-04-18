package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.LateOrderDto;
import com.shailahir.koha.acquisitions.repository.LateOrderRepository;
import com.shailahir.koha.acquisitions.service.LateOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link LateOrderService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LateOrderServiceImpl implements LateOrderService {

    private final LateOrderRepository lateOrderRepo;

    @Override
    public List<LateOrderDto> getLateOrders(int delay, LocalDate estimatedFrom,
            LocalDate estimatedTo, Long booksellerid, Long budgetId) {
        log.debug("getLateOrders delay={} booksellerid={}", delay, booksellerid);
        // Repository signature: filterByLates(delay, estimatedFrom, estimatedTo, booksellerid, branch)
        // budgetId is not supported by the existing repo signature; pass null for branch
        return lateOrderRepo.filterByLates(delay, estimatedFrom, estimatedTo, booksellerid, null);
    }

    @Override
    @Transactional
    public void claimOrders(List<Long> ordernumbers) {
        log.info("Claiming {} orders", ordernumbers.size());
        for (Long ordernumber : ordernumbers) {
            lateOrderRepo.claimOrder(ordernumber);
            lateOrderRepo.logClaim(ordernumber);
        }
    }

    @Override
    public List<Map<String, Object>> exportOrders(List<Long> ordernumbers) {
        log.debug("Entering exportOrders - {}", ordernumbers);
        return lateOrderRepo.getOrdersForExport(ordernumbers);
    }

    @Override
    public List<Map<String, Object>> getClaimLetters() {
        log.debug("Entering getClaimLetters");
        return lateOrderRepo.getClaimLetters();
    }
}
