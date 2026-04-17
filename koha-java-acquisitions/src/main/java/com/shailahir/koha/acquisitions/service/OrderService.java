package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.BudgetCheckResult;
import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.OrderRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for order operations, porting addorder.pl business logic.
 */
public interface OrderService {

    // ── Orders ──

    List<OrderDto> listOrders(int page, int size);

    Optional<OrderDto> getOrder(Long ordernumber);

    /**
     * Creates or modifies an order (mirrors the cud-order op in addorder.pl).
     * <p>
     * Steps:
     * <ol>
     *   <li>Budget validation (unless confirm_budget_exceeding=true)</li>
     *   <li>Duplicate-biblio detection (unless confirm_not_duplicate=true)</li>
     *   <li>Auto-create biblio when no biblionumber supplied</li>
     *   <li>Update suggestion status to ORDERED when suggestionid present</li>
     *   <li>Persist order (INSERT or UPDATE)</li>
     *   <li>Set order-user notifications</li>
     *   <li>Create items when basket.create_items = 'ordering'</li>
     *   <li>Write acquisition log</li>
     * </ol>
     *
     * @param request validated order form data
     * @return the saved order DTO
     * @throws BudgetExceededException  when the order total exceeds the available budget
     *                                  and confirm_budget_exceeding is false
     * @throws DuplicateBiblioException when a probable duplicate biblio exists
     *                                  and confirm_not_duplicate is false
     */
    OrderDto saveOrder(OrderRequest request);

    void deleteOrder(Long ordernumber);

    /**
     * Validates budget headroom without persisting anything.
     * Returns a result object describing which thresholds (if any) are breached.
     */
    BudgetCheckResult checkBudget(Long budgetId, Long excludeOrdernumber, java.math.BigDecimal orderTotal);

    // ── Baskets ──

    List<BasketDto> listBaskets(int page, int size);

    BasketDto addBasket(BasketDto dto);

    List<BasketDto> listBasketManagers();
}

