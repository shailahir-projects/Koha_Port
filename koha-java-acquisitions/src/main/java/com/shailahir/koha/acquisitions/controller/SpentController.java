package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.SpentDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for spent.pl
 *
 * <p>Shows spent/ordered/remaining amounts for each fund within a budget period.
 *
 * <pre>
 * GET /api/v1/acquisitions/budget-periods/{budgetPeriodId}/spent
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/budget-periods")
@RequiredArgsConstructor
@Slf4j
public class SpentController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns fund-level spending summary for a budget period.
     * Mirrors spent.pl: shows budget_amount, spent, ordered, remaining per fund.
     *
     * @param budgetPeriodId the budget period identifier (aqbudgetperiods.budget_period_id)
     */
    @GetMapping(
            value = "/{budgetPeriodId}/spent",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<List<SpentDto>> getSpent(@PathVariable Long budgetPeriodId) {
        log.debug("GET spent budgetPeriodId={}", budgetPeriodId);
        return ResponseEntity.ok(extRepo.findSpentByPeriod(budgetPeriodId));
    }
}

