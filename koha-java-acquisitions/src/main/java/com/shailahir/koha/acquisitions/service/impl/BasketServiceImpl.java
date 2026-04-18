package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Full implementation of basket.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BasketServiceImpl implements com.shailahir.koha.acquisitions.service.BasketService {

    private final BasketRepository basketRepo;

    // ── List (detail view) ─────────────────────────────────────────────────────

    @Override
    public BasketDetailDto getBasketDetail(Long basketno, String duplinbatch) {
        log.debug("Entering getBasketDetail - {}, {}", basketno, duplinbatch);
        BasketDto basket = basketRepo.findById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));

        // Enrich basket with vendor name, contract name, estimated delivery
        enrichBasket(basket);

        // Users
        List<Long> userIds = basketRepo.getBasketUsers(basketno);
        basket.setUsersIds(userIds.stream().map(String::valueOf).collect(Collectors.joining(":")));

        // Active currency
        String currency = basketRepo.getActiveCurrency().orElse("");

        // Has budgets?
        basket.setHasBudgets(basketRepo.hasActiveBudgets());

        // Orders
        List<BasketOrderLineDto> activeOrders    = basketRepo.findActiveOrders(basketno);
        List<BasketOrderLineDto> cancelledOrders = basketRepo.findCancelledOrders(basketno);

        // Enrich each order line (mirrors get_order_infos)
        boolean uncertainprices = false;
        for (BasketOrderLineDto line : activeOrders) {
            enrichOrderLine(line, basket);
            if (Boolean.TRUE.equals(line.getUncertainprice())) uncertainprices = true;
        }
        for (BasketOrderLineDto line : cancelledOrders) {
            enrichOrderLine(line, basket);
        }

        basket.setUncertainprices(uncertainprices);

        // Footer totals (grouped by tax rate, mirrors %foot in basket.pl)
        Map<BigDecimal, BasketDetailDto.TaxFootDto> foot = new LinkedHashMap<>();
        int    totalQty         = 0;
        BigDecimal totalTaxExcl = BigDecimal.ZERO;
        BigDecimal totalTaxIncl = BigDecimal.ZERO;
        BigDecimal totalTaxVal  = BigDecimal.ZERO;

        for (BasketOrderLineDto line : activeOrders) {
            BigDecimal rate = line.getTaxRate() != null ? line.getTaxRate() : BigDecimal.ZERO;
            BasketDetailDto.TaxFootDto f = foot.computeIfAbsent(rate, r ->
                    BasketDetailDto.TaxFootDto.builder()
                            .taxRate(r)
                            .taxValue(BigDecimal.ZERO)
                            .quantity(0)
                            .totalTaxExcluded(BigDecimal.ZERO)
                            .totalTaxIncluded(BigDecimal.ZERO)
                            .build());

            int qty = line.getQuantity() != null ? line.getQuantity() : 0;
            f.setQuantity(f.getQuantity() + qty);
            totalQty += qty;

            BigDecimal tv = line.getTaxValue() != null ? line.getTaxValue() : BigDecimal.ZERO;
            f.setTaxValue(f.getTaxValue().add(rounded(tv)));
            totalTaxVal = totalTaxVal.add(tv);

            BigDecimal excl = line.getTotalTaxExcluded() != null ? line.getTotalTaxExcluded() : BigDecimal.ZERO;
            f.setTotalTaxExcluded(f.getTotalTaxExcluded().add(excl));
            totalTaxExcl = totalTaxExcl.add(excl);

            BigDecimal incl = line.getTotalTaxIncluded() != null ? line.getTotalTaxIncluded() : BigDecimal.ZERO;
            f.setTotalTaxIncluded(f.getTotalTaxIncluded().add(incl));
            totalTaxIncl = totalTaxIncl.add(incl);
        }

        // Unclosable: true when basket is standing OR has no orders at all
        boolean unclosable = Boolean.TRUE.equals(basket.getIsStanding())
                || (activeOrders.isEmpty() && cancelledOrders.isEmpty());
        basket.setUnclosable(unclosable);

        return BasketDetailDto.builder()
                .basket(basket)
                .orders(activeOrders)
                .cancelledOrders(cancelledOrders)
                .foot(new ArrayList<>(foot.values()))
                .totalQuantity(totalQty)
                .totalTaxExcluded(totalTaxExcl)
                .totalTaxIncluded(totalTaxIncl)
                .totalTaxValue(totalTaxVal)
                .currency(currency)
                .duplinbatch(duplinbatch)
                .build();
    }

    // ── Basket CRUD ────────────────────────────────────────────────────────────

    @Override
    public List<BasketDto> listBaskets(int page, int size) {
        log.debug("Entering listBaskets - {}, {}", page, size);
        List<BasketDto> baskets = basketRepo.findAll(page * size, size);
        baskets.forEach(this::enrichBasket);
        return baskets;
    }

    @Override
    @Transactional
    public BasketDto addBasket(BasketDto dto) {
        log.debug("Entering addBasket - {}", dto);
        Long basketno = basketRepo.insert(dto);
        dto.setBasketno(basketno);
        return dto;
    }

    // ── Close ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Long closeBasket(Long basketno, BasketCloseRequest request) {
        log.debug("Entering closeBasket - {}, {}", basketno, request);
        BasketDto basket = basketRepo.findById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));

        basketRepo.close(basketno);
        log.info("Basket {} closed", basketno);

        Long basketgroupid = null;
        if (Boolean.TRUE.equals(request.getCreateBasketGroup())) {
            String branch = request.getBranchcode();
            basketgroupid = basketRepo.createBasketGroup(
                    basket.getBasketname(),
                    basket.getBooksellerid(),
                    branch, branch,
                    true);
            basketRepo.updateBasketgroup(basketno, basketgroupid);
            log.info("Basket {} assigned to new basket group {}", basketno, basketgroupid);
        }

        return basketgroupid;
    }

    // ── Reopen ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void reopenBasket(Long basketno) {
        log.debug("Entering reopenBasket - {}", basketno);
        basketRepo.reopen(basketno);
        log.info("Basket {} reopened", basketno);
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteBasket(Long basketno) {
        log.debug("Entering deleteBasket - {}", basketno);
        basketRepo.delete(basketno);
        log.info("Basket {} deleted", basketno);
    }

    @Override
    @Transactional
    public void deleteCancelledOrder(Long ordernumber) {
        log.debug("Entering deleteCancelledOrder - {}", ordernumber);
        // Only allow deleting cancelled orders without a biblionumber (mirrors basket.pl restriction)
        basketRepo.deleteCancelledOrderWithoutBiblio(ordernumber);
        log.info("Deleted cancelled order {}", ordernumber);
    }

    // ── Basket users ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void setBasketUsers(Long basketno, List<Long> userIds) {
        log.debug("Entering setBasketUsers - {}, {}", basketno, userIds);
        basketRepo.setBasketUsers(basketno, userIds);
    }

    // ── Basket branch ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void setBasketBranch(Long basketno, String branch) {
        log.debug("Entering setBasketBranch - {}, {}", basketno, branch);
        basketRepo.updateBranch(basketno, (branch != null && branch.isBlank()) ? null : branch);
    }

    // ── CSV export ─────────────────────────────────────────────────────────────

    @Override
    public String exportBasketAsCsv(Long basketno) {
        log.debug("Entering exportBasketAsCsv - {}", basketno);
        BasketDto basket = basketRepo.findById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));
        enrichBasket(basket);

        List<BasketOrderLineDto> orders = basketRepo.findActiveOrders(basketno);
        orders.forEach(line -> enrichOrderLine(line, basket));

        StringBuilder sb = new StringBuilder();
        sb.append("ordernumber,title,author,isbn,quantity,listprice,ecost_tax_excluded,")
          .append("ecost_tax_included,total_tax_excluded,total_tax_included,budget_name\n");

        for (BasketOrderLineDto line : orders) {
            sb.append(csv(line.getOrdernumber())).append(',')
              .append(csv(line.getTitle())).append(',')
              .append(csv(line.getAuthor())).append(',')
              .append(csv(line.getIsbn())).append(',')
              .append(csv(line.getQuantity())).append(',')
              .append(csv(line.getListprice())).append(',')
              .append(csv(line.getEcostTaxExcluded())).append(',')
              .append(csv(line.getEcostTaxIncluded())).append(',')
              .append(csv(line.getTotalTaxExcluded())).append(',')
              .append(csv(line.getTotalTaxIncluded())).append(',')
              .append(csv(line.getBudgetName()))
              .append('\n');
        }
        return sb.toString();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Enriches a BasketDto with vendor name, contract name and estimated delivery date. */
    private void enrichBasket(BasketDto basket) {
        log.debug("Entering enrichBasket - {}", basket);
        if (basket.getBooksellerid() != null) {
            basketRepo.findVendorName(basket.getBooksellerid())
                    .ifPresent(basket::setBooksellername);

            // Estimated delivery date = closedate + vendor.deliverytime
            if (basket.getClosedate() != null && !basket.getClosedate().isBlank()) {
                try {
                    LocalDate closed = LocalDate.parse(basket.getClosedate().substring(0, 10));
                    int deliverytime = basketRepo.findVendorDeliverytime(basket.getBooksellerid()).orElse(0);
                    basket.setEstimatedDeliveryDate(closed.plusDays(deliverytime));
                } catch (Exception ignored) {
                }
            }
        }
        if (basket.getContractnumber() != null) {
            basketRepo.findContract(basket.getContractnumber())
                    .ifPresent(c -> basket.setContractname((String) c.get("contractname")));
        }
    }

    /**
     * Enriches an order line with budget name, biblio-level flags, and
     * order_received flag. Mirrors get_order_infos() in basket.pl.
     */
    private void enrichOrderLine(BasketOrderLineDto line, BasketDto basket) {
        log.debug("Entering enrichOrderLine - {}, {}", line, basket);
        // Budget
        if (line.getBudgetId() != null) {
            basketRepo.findBudget(line.getBudgetId()).ifPresent(b -> {
                line.setBudgetName((String) b.get("budget_name"));
                line.setSort1Authcat((String) b.get("sort1_authcat"));
                line.setSort2Authcat((String) b.get("sort2_authcat"));
            });
        }

        // order_received: qty received == qty ordered (and non-zero for non-standing)
        int qty     = line.getQuantity()         != null ? line.getQuantity()         : 0;
        int qtyRecv = line.getQuantityreceived() != null ? line.getQuantityreceived() : 0;
        boolean standing = Boolean.TRUE.equals(basket.getIsStanding());
        line.setOrderReceived(qty > 0 && qty == qtyRecv && (standing ? qty > 0 : true));

        Long biblionumber = line.getBiblionumber();
        if (biblionumber != null) {
            int uncancelledOrders = basketRepo.countUncancelledOrdersForBiblio(biblionumber);
            int totalItems        = basketRepo.countItemsForBiblio(biblionumber);
            int orderItems        = basketRepo.countItemsForOrder(line.getOrdernumber());
            int subscriptions     = basketRepo.countSubscriptionsForBiblio(biblionumber);
            int holds             = basketRepo.countHoldsForBiblio(biblionumber);
            int itemHolds         = basketRepo.countItemHoldsForOrder(biblionumber, line.getOrdernumber());

            int itemsElsewhere = totalItems - orderItems;
            line.setItemsElsewhere(itemsElsewhere);
            line.setLeftItem(itemsElsewhere >= 1);
            line.setLeftBiblio(uncancelledOrders > 1);
            line.setBibliosCount(uncancelledOrders - 1);
            line.setLeftSubscription(subscriptions > 0);
            line.setSubscriptionsCount(subscriptions);
            line.setLeftHolds(holds >= 1);
            line.setHoldsCount(holds);
            line.setLeftHoldsOnOrder(holds >= 1 && (itemsElsewhere == 0 || itemHolds > 0));
            line.setHoldsOnOrder(line.getLeftHoldsOnOrder() != null && line.getLeftHoldsOnOrder()
                    ? (itemHolds > 0 ? itemHolds : holds) : null);

            // can_del_bib: no other orders, all items belong to this order, no subscriptions, no holds
            line.setCanDelBib(uncancelledOrders <= 1
                    && totalItems == orderItems
                    && subscriptions == 0
                    && holds == 0);

            // Suggestion
            basketRepo.findSuggestionForBiblio(biblionumber)
                    .ifPresent(line::setSuggestionId);
        } else {
            line.setDeletedBiblio(true);
        }
    }

    private BigDecimal rounded(BigDecimal v) {
        log.debug("Entering rounded - {}", v);
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }

    private String csv(Object v) {
        log.debug("Entering csv - {}", v);
        if (v == null) return "";
        String s = v.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}


