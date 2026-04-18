package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.BasketOrderLineDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for aqbasket CRUD and basket lifecycle operations.
 * Ported from basket.pl.
 */
public interface IBasketRepository {

    Optional<BasketDto> findById(Long basketno);
    List<BasketDto> findAll(int offset, int limit);
    Long insert(BasketDto dto);
    void close(Long basketno);
    void reopen(Long basketno);
    void delete(Long basketno);
    void updateBranch(Long basketno, String branch);
    void updateBasketgroup(Long basketno, Long basketgroupid);
    List<Long> getBasketUsers(Long basketno);
    void setBasketUsers(Long basketno, List<Long> userIds);
    List<BasketOrderLineDto> findActiveOrders(Long basketno);
    List<BasketOrderLineDto> findCancelledOrders(Long basketno);
    Optional<Map<String, Object>> findBudget(Long budgetId);
    Optional<String> findVendorName(Long booksellerid);
    Optional<Integer> findVendorDeliverytime(Long booksellerid);
    int countUncancelledOrdersForBiblio(Long biblionumber);
    int countItemsForBiblio(Long biblionumber);
    int countItemsForOrder(Long ordernumber);
    int countSubscriptionsForBiblio(Long biblionumber);
    int countHoldsForBiblio(Long biblionumber);
    boolean hasActiveBudgets();
}

