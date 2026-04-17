package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.*;

/**
 * Service interface for basket group operations, porting basketgroup.pl.
 */
public interface BasketGroupService {

    /**
     * op=display — returns all basket groups for a vendor with their baskets
     * and all unassigned closed baskets, each with a computed total.
     * Mirrors displaybasketgroups() + BasketTotal().
     */
    BasketGroupPageDto getPage(Long booksellerid);

    /**
     * op=add_form — returns a single basket group with its assigned baskets.
     */
    BasketGroupDto getBasketGroup(Long basketgroupid);

    /**
     * op=cud-attachbasket — create or modify a basket group and assign baskets.
     * When basketgroupid is null, creates a new group (mirrors NewBasketgroup()).
     * When basketgroupid is set, modifies the existing group (mirrors ModBasketgroup()).
     */
    BasketGroupDto saveBasketGroup(Long basketgroupid, BasketGroupRequest request);

    /**
     * op=cud-delete — deletes the basket group, unlinking its baskets first.
     * Mirrors DelBasketgroup().
     */
    void deleteBasketGroup(Long basketgroupid);

    /**
     * op=closeandprint (close part) — closes a basket group.
     * Mirrors CloseBasketgroup().
     */
    void closeBasketGroup(Long basketgroupid);

    /**
     * op=cud-reopen — reopens a closed basket group.
     * Mirrors ReOpenBasketgroup().
     */
    void reopenBasketGroup(Long basketgroupid);

    /**
     * op=cud-mod_basket — assigns a single basket to a basket group.
     * Mirrors ModBasket({ basketno => ..., basketgroupid => ... }).
     */
    void assignBasketToGroup(Long basketno, Long basketgroupid);

    /**
     * op=export — returns the basket group orders as a CSV string.
     * Mirrors GetBasketGroupAsCSV().
     */
    String exportAsCsv(Long basketgroupid);
}

