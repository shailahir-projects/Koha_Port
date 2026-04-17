package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.*;

import java.util.List;

/**
 * Service interface for basket operations, porting basket.pl.
 */
public interface BasketService {

    /**
     * op=list — assembles the full basket detail view.
     * Mirrors the list block in basket.pl including order enrichment
     * (get_order_infos), footer totals, estimated delivery date, etc.
     */
    BasketDetailDto getBasketDetail(Long basketno, String duplinbatch);

    List<BasketDto> listBaskets(int page, int size);

    BasketDto addBasket(BasketDto dto);

    /**
     * op=cud-close — closes the basket and optionally creates a basket group.
     * Returns the basket group id when one was created, otherwise null.
     */
    Long closeBasket(Long basketno, BasketCloseRequest request);

    /**
     * op=cud-reopen — reopens a closed basket.
     */
    void reopenBasket(Long basketno);

    /**
     * op=cud-delete — cancels all orders in the basket then deletes it.
     */
    void deleteBasket(Long basketno);

    /**
     * op=cud-delete-order — hard-deletes a cancelled order that has no biblionumber.
     */
    void deleteCancelledOrder(Long ordernumber);

    /**
     * op=cud-mod_users — replaces the user notification list for the basket.
     */
    void setBasketUsers(Long basketno, List<Long> userIds);

    /**
     * op=cud-mod_branch — updates the basket's assigned branch.
     */
    void setBasketBranch(Long basketno, String branch);

    /**
     * op=export — returns the basket as a CSV string.
     * Mirrors GetBasketAsCSV().
     */
    String exportBasketAsCsv(Long basketno);
}

