package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.OrderedItemDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for ordered.pl
 *
 * <p>Lists all active (ordered, not received/cancelled) orders for a given fund (budget).
 *
 * <pre>
 * GET /api/v1/acquisitions/budgets/{budgetId}/ordered
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/budgets")
@RequiredArgsConstructor
@Slf4j
public class OrderedController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns ordered items for a budget.
     * Mirrors ordered.pl: lists aqorders rows in 'new'/'ordered' status for the fund.
     *
     * @param budgetId the budget (fund) identifier (aqbudgets.budget_id)
     * @return list of ordered item DTOs
     */
    @GetMapping(
            value = "/{budgetId}/ordered",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<List<OrderedItemDto>> getOrderedByBudget(@PathVariable Long budgetId) {
        log.debug("GET ordered budgetId={}", budgetId);
        List<OrderedItemDto> result = extRepo.findOrderedByBudget(budgetId);
        return ResponseEntity.ok(result);
    }
}

