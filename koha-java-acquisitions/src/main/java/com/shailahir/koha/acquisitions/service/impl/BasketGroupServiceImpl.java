package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.BasketGroupRepository;
import com.shailahir.koha.acquisitions.service.BasketGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Full implementation of basketgroup.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BasketGroupServiceImpl implements BasketGroupService {

    private final BasketGroupRepository repo;

    // ── Display (op=display) ───────────────────────────────────────────────────

    @Override
    public BasketGroupPageDto getPage(Long booksellerid) {
        boolean listIncGst = repo.vendorListIncGst(booksellerid);
        String vendorName  = repo.findVendorName(booksellerid).orElse("");

        // All basket groups for this vendor
        List<BasketGroupDto> groups = repo.findByBookseller(booksellerid);
        for (BasketGroupDto g : groups) {
            List<BasketSummaryDto> groupBaskets = repo.findBasketsByGroup(g.getId());
            for (BasketSummaryDto b : groupBaskets) {
                b.setTotal(repo.basketTotal(b.getBasketno(), listIncGst));
            }
            g.setBaskets(groupBaskets);
            g.setBasketsqty(groupBaskets.size());
        }

        // All baskets for this vendor
        List<BasketSummaryDto> allBaskets = repo.findBasketsByBookseller(booksellerid);

        // Unassigned = closed baskets with no basketgroupid
        // (open baskets are excluded, mirrors the splice in displaybasketgroups)
        List<BasketSummaryDto> unassigned = allBaskets.stream()
                .filter(b -> b.getClosedate() != null && !b.getClosedate().isBlank())
                .filter(b -> b.getBasketgroupid() == null)
                .collect(Collectors.toList());
        for (BasketSummaryDto b : unassigned) {
            b.setTotal(repo.basketTotal(b.getBasketno(), listIncGst));
        }

        return BasketGroupPageDto.builder()
                .basketGroups(groups)
                .unassignedBaskets(unassigned)
                .booksellerid(booksellerid)
                .booksellername(vendorName)
                .build();
    }

    // ── Get single group with its baskets (op=add_form) ───────────────────────

    @Override
    public BasketGroupDto getBasketGroup(Long basketgroupid) {
        BasketGroupDto g = repo.findById(basketgroupid)
                .orElseThrow(() -> new NoSuchElementException("Basket group not found: " + basketgroupid));

        boolean listIncGst = g.getBooksellerid() != null && repo.vendorListIncGst(g.getBooksellerid());
        List<BasketSummaryDto> baskets = repo.findBasketsByGroup(basketgroupid);
        for (BasketSummaryDto b : baskets) {
            b.setTotal(repo.basketTotal(b.getBasketno(), listIncGst));
        }
        g.setBaskets(baskets);
        g.setBasketsqty(baskets.size());
        return g;
    }

    // ── Create / modify (op=cud-attachbasket) ────────────────────────────────

    @Override
    @Transactional
    public BasketGroupDto saveBasketGroup(Long basketgroupid, BasketGroupRequest request) {
        Long id;
        if (basketgroupid != null) {
            // Modify existing
            BasketGroupDto existing = repo.findById(basketgroupid)
                    .orElseThrow(() -> new NoSuchElementException("Basket group not found: " + basketgroupid));
            existing.setName(request.getName());
            existing.setDeliveryplace(request.getDeliveryplace());
            existing.setFreedeliveryplace(request.getFreedeliveryplace());
            existing.setDeliverycomment(request.getDeliverycomment());
            existing.setBillingplace(request.getBillingplace());
            existing.setClosed(request.getClosed());
            repo.update(existing);
            id = basketgroupid;
            log.info("Updated basket group {}", id);
        } else {
            // Create new
            BasketGroupDto dto = BasketGroupDto.builder()
                    .name(request.getName())
                    .booksellerid(request.getBooksellerid())
                    .deliveryplace(request.getDeliveryplace())
                    .freedeliveryplace(request.getFreedeliveryplace())
                    .deliverycomment(request.getDeliverycomment())
                    .billingplace(request.getBillingplace())
                    .closed(request.getClosed())
                    .build();
            id = repo.insert(dto);
            log.info("Created basket group {}", id);
        }

        // Assign baskets to the group
        repo.assignBaskets(id, request.getBasketList());

        return getBasketGroup(id);
    }

    // ── Delete (op=cud-delete) ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteBasketGroup(Long basketgroupid) {
        repo.delete(basketgroupid);
        log.info("Deleted basket group {}", basketgroupid);
    }

    // ── Close (op=closeandprint only the close part) ──────────────────────────

    @Override
    @Transactional
    public void closeBasketGroup(Long basketgroupid) {
        repo.close(basketgroupid);
        log.info("Closed basket group {}", basketgroupid);
    }

    // ── Reopen (op=cud-reopen) ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void reopenBasketGroup(Long basketgroupid) {
        repo.reopen(basketgroupid);
        log.info("Reopened basket group {}", basketgroupid);
    }

    // ── Assign single basket (op=cud-mod_basket) ──────────────────────────────

    @Override
    @Transactional
    public void assignBasketToGroup(Long basketno, Long basketgroupid) {
        repo.assignBasket(basketno, basketgroupid);
        log.info("Basket {} assigned to group {}", basketno, basketgroupid);
    }

    // ── CSV export (op=export) ─────────────────────────────────────────────────

    @Override
    public String exportAsCsv(Long basketgroupid) {
        return repo.exportAsCsv(basketgroupid);
    }
}

