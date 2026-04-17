package com.shailahir.koha.acquisitions.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.BudgetCheckResult;
import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.OrderRequest;
import com.shailahir.koha.acquisitions.repository.BudgetRepository;
import com.shailahir.koha.acquisitions.repository.OrderRepository;
import com.shailahir.koha.acquisitions.service.BudgetExceededException;
import com.shailahir.koha.acquisitions.service.DuplicateBiblioException;
import com.shailahir.koha.acquisitions.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Full implementation of addorder.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final BudgetRepository budgetRepo;
    private final ObjectMapper objectMapper;

    // ── Orders ─────────────────────────────────────────────────────────────────

    @Override
    public List<OrderDto> listOrders(int page, int size) {
        return orderRepo.findAll(page * size, size);
    }

    @Override
    public Optional<OrderDto> getOrder(Long ordernumber) {
        return orderRepo.findById(ordernumber);
    }

    @Override
    @Transactional
    public OrderDto saveOrder(OrderRequest req) {

        // ── 1. Budget check ────────────────────────────────────────────────────
        boolean skipBudgetCheck = Boolean.TRUE.equals(req.getConfirmBudgetExceeding());
        if (!skipBudgetCheck && req.getTotal() != null && req.getBudgetId() != null) {
            BudgetCheckResult check = checkBudget(req.getBudgetId(), req.getOrdernumber(), req.getTotal());
            if (check.isExceeded() || check.isEncumbranceExceeded() || check.isExpenditureExceeded()) {
                throw new BudgetExceededException(
                        "Order total exceeds available budget. BudgetCheckResult: " + check);
            }
        }

        // ── 2. Biblio resolution ───────────────────────────────────────────────
        Long biblionumber = req.getBiblionumber();
        if (biblionumber == null) {
            // Duplicate detection (mirrors FindDuplicate in addorder.pl)
            boolean skipDupeCheck = Boolean.TRUE.equals(req.getConfirmNotDuplicate());
            if (!skipDupeCheck) {
                Optional<Long> dupe = budgetRepo.findDuplicateBiblio(req.getIsbn(), req.getTitle(), req.getAuthor());
                if (dupe.isPresent()) {
                    throw new DuplicateBiblioException(dupe.get());
                }
            }

            // Auto-create minimal biblio
            biblionumber = budgetRepo.insertBiblio(
                    req.getTitle(), req.getAuthor(), req.getIsbn(), req.getEan(),
                    req.getPublishercode(), req.getPublicationyear(),
                    req.getItemtype(), req.getEditionstatement(), req.getSeries());
            log.info("Created new biblio {} for order", biblionumber);
        }

        // ── 3. Suggestion update ───────────────────────────────────────────────
        if (req.getSuggestionid() != null) {
            budgetRepo.updateSuggestionOrdered(req.getSuggestionid(), biblionumber);
        }

        // ── 4. Build OrderDto ──────────────────────────────────────────────────
        OrderDto order = OrderDto.builder()
                .ordernumber(req.getOrdernumber())
                .basketno(req.getBasketno())
                .biblionumber(biblionumber)
                .invoiceid(req.getInvoiceid())
                .budgetId(req.getBudgetId())
                .quantity(req.getQuantity())
                .currency(req.getCurrency())
                .listprice(req.getListprice())
                .uncertainprice(req.getUncertainprice())
                .taxRateOnOrdering(req.getTaxRate())
                .discount(req.getDiscount())
                .rrp(req.getRrp())
                .replacementprice(req.getReplacementprice())
                .ecost(req.getEcost())
                .unitprice(req.getUnitprice() != null ? req.getUnitprice() : req.getEcost())
                .orderInternalnote(req.getOrderInternalnote())
                .orderVendornote(req.getOrderVendornote())
                .sort1(req.getSort1())
                .sort2(req.getSort2())
                .subscriptionid(req.getSubscriptionid())
                .estimatedDeliveryDate(req.getEstimatedDeliveryDate())
                .build();

        // ── 5. Populate tax-split price fields ─────────────────────────────────
        populateWithPricesForOrdering(order);

        // ── 6. Persist ─────────────────────────────────────────────────────────
        Long ordernumber;
        String logAction;
        if (req.getOrdernumber() != null) {
            order.setOrdernumber(req.getOrdernumber());
            orderRepo.update(order);
            ordernumber = req.getOrdernumber();
            logAction = "MODIFY_ORDER";
        } else {
            ordernumber = orderRepo.insert(order);
            order.setOrdernumber(ordernumber);
            logAction = "CREATE_ORDER";
        }

        // ── 7. Order-user notifications ────────────────────────────────────────
        if (req.getUsersIds() != null && !req.getUsersIds().isBlank()) {
            List<Long> userIds = Arrays.stream(req.getUsersIds().split(":"))
                    .filter(s -> !s.isBlank())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            orderRepo.setOrderUsers(ordernumber, userIds);
        } else {
            orderRepo.setOrderUsers(ordernumber, Collections.emptyList());
        }

        // ── 8. Item creation (basket.create_items = 'ordering') ───────────────
        if (req.getItems() != null && !req.getItems().isEmpty()) {
            Optional<BasketDto> basket = orderRepo.findBasketById(req.getBasketno());
            boolean createItemsOnOrdering = basket
                    .map(b -> "ordering".equalsIgnoreCase(b.getCreateItems()))
                    .orElse(false);

            if (createItemsOnOrdering) {
                Long bib = biblionumber;
                Optional<Long> biblioitemnumber = budgetRepo.getBiblioitemnumber(bib);
                for (OrderRequest.ItemData item : req.getItems()) {
                    Long itemnumber = budgetRepo.insertItem(
                            bib,
                            biblioitemnumber.orElse(null),
                            item.getBarcode(),
                            item.getHomebranch(),
                            item.getHoldingbranch(),
                            item.getItype(),
                            item.getLocation(),
                            item.getReplacementprice(),
                            item.getCallnumber());
                    orderRepo.linkItemToOrder(ordernumber, itemnumber);
                }
            }
        }

        // ── 9. Acquisition log ─────────────────────────────────────────────────
        try {
            Map<String, Object> logInfo = Map.of(
                    "quantity", order.getQuantity() != null ? order.getQuantity() : 0,
                    "listprice", order.getListprice() != null ? order.getListprice() : BigDecimal.ZERO,
                    "unitprice", order.getUnitprice() != null ? order.getUnitprice() : BigDecimal.ZERO,
                    "ecost", order.getEcost() != null ? order.getEcost() : BigDecimal.ZERO,
                    "rrp", order.getRrp() != null ? order.getRrp() : BigDecimal.ZERO,
                    "tax_rate_on_ordering", order.getTaxRateOnOrdering() != null ? order.getTaxRateOnOrdering() : BigDecimal.ZERO
            );
            budgetRepo.insertAcquisitionLog("ACQUISITIONS", logAction, ordernumber, objectMapper.writeValueAsString(logInfo));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize acquisition log info", e);
        }

        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(Long ordernumber) {
        orderRepo.cancel(ordernumber);
    }

    // ── Budget validation ──────────────────────────────────────────────────────

    @Override
    public BudgetCheckResult checkBudget(Long budgetId, Long excludeOrdernumber, BigDecimal orderTotal) {
        Optional<Map<String, Object>> budgetOpt = budgetRepo.findBudgetById(budgetId);
        if (budgetOpt.isEmpty()) {
            return BudgetCheckResult.builder().exceeded(false).build();
        }
        Map<String, Object> budget = budgetOpt.get();

        BigDecimal budgetAmount = toBD(budget.get("budget_amount"));
        BigDecimal budgetEncumb = toBD(budget.get("budget_encumb"));   // percentage
        BigDecimal budgetExpend = toBD(budget.get("budget_expend"));

        BigDecimal spent   = budgetRepo.getBudgetSpent(budgetId);
        BigDecimal ordered = budgetRepo.getBudgetOrdered(budgetId);

        // When modifying: subtract existing order cost from ordered total
        if (excludeOrdernumber != null) {
            BigDecimal existingCost = budgetRepo.getOrderEcostTaxIncluded(excludeOrdernumber)
                    .orElse(BigDecimal.ZERO);
            ordered = ordered.subtract(existingCost);
        }

        BigDecimal used      = spent.add(ordered);
        BigDecimal remaining = budgetAmount.subtract(used);

        BigDecimal encumbranceLimit = budgetEncumb.compareTo(BigDecimal.ZERO) != 0
                ? budgetAmount.multiply(budgetEncumb).divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        boolean exceeded            = orderTotal.compareTo(remaining) > 0;
        boolean encumbranceExceeded = encumbranceLimit.compareTo(BigDecimal.ZERO) != 0
                && used.add(orderTotal).compareTo(encumbranceLimit) > 0
                && !exceeded;
        boolean expenditureExceeded = budgetExpend.compareTo(BigDecimal.ZERO) != 0
                && used.add(orderTotal).compareTo(budgetExpend) > 0
                && !exceeded;

        String currencySymbol = budgetRepo.getActiveCurrencySymbol().orElse("");

        return BudgetCheckResult.builder()
                .exceeded(exceeded)
                .encumbranceExceeded(encumbranceExceeded)
                .expenditureExceeded(expenditureExceeded)
                .budgetRemaining(remaining)
                .budgetEncumbrance(encumbranceLimit)
                .budgetExpenditure(budgetExpend)
                .currencySymbol(currencySymbol)
                .build();
    }

    // ── Baskets ────────────────────────────────────────────────────────────────

    @Override
    public List<BasketDto> listBaskets(int page, int size) {
        return orderRepo.findAllBaskets(page * size, size);
    }

    @Override
    @Transactional
    public BasketDto addBasket(BasketDto dto) {
        Long basketno = orderRepo.insertBasket(dto);
        dto.setBasketno(basketno);
        return dto;
    }

    @Override
    public List<BasketDto> listBasketManagers() {
        // Returns patrons who have the 'acquisition' permission — stub returning empty
        // Full implementation would join borrowers with userflags
        return List.of();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Mirrors Koha's populate_with_prices_for_ordering:
     * derives tax-included / tax-excluded variants from rrp, ecost, unitprice
     * using the tax_rate_on_ordering.
     */
    private void populateWithPricesForOrdering(OrderDto order) {
        BigDecimal taxRate = order.getTaxRateOnOrdering() != null
                ? order.getTaxRateOnOrdering()
                : BigDecimal.ZERO;

        BigDecimal taxMultiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100)));

        if (order.getRrp() != null) {
            order.setRrpTaxExcluded(order.getRrp().divide(taxMultiplier, 6, java.math.RoundingMode.HALF_UP));
            order.setRrpTaxIncluded(order.getRrp());
        }
        if (order.getEcost() != null) {
            order.setEcostTaxExcluded(order.getEcost().divide(taxMultiplier, 6, java.math.RoundingMode.HALF_UP));
            order.setEcostTaxIncluded(order.getEcost());
        }
        BigDecimal unitprice = order.getUnitprice() != null ? order.getUnitprice() : order.getEcost();
        if (unitprice != null) {
            order.setUnitprice(unitprice);
            order.setUnitpriceTaxExcluded(unitprice.divide(taxMultiplier, 6, java.math.RoundingMode.HALF_UP));
            order.setUnitpriceTaxIncluded(unitprice);
        }
    }

    private BigDecimal toBD(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        return new BigDecimal(val.toString());
    }
}

