package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.AcquisitionsHomeDto;
import com.shailahir.koha.acquisitions.dto.BudgetDto;
import com.shailahir.koha.acquisitions.dto.BudgetPeriodDto;
import com.shailahir.koha.acquisitions.service.AcquisitionsHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Acquisitions home/dashboard controller — Java equivalent of acqui-home.pl.
 *
 * <p>The Perl script renders an HTML page but its underlying data logic maps cleanly
 * to REST endpoints that a frontend (or the OPAC gateway) can consume:
 *
 * <ul>
 *   <li>GET /acquisitions/home — full dashboard: suggestions count, budget totals,
 *       budget period list, active currency, and default date filters.</li>
 *   <li>GET /acquisitions/budgets — budget hierarchy viewable by the current user,
 *       with per-budget totals (amount/spent/ordered/available).</li>
 *   <li>GET /acquisitions/budget_periods — all budget periods with nested fund hierarchy.</li>
 * </ul>
 *
 * <p>Budget permission filtering (equivalent to {@code CanUserUseBudget}) and
 * branch-scoped suggestion counting are delegated to {@link AcquisitionsHomeService}.
 */
@RestController
@RequiredArgsConstructor
public class AcquisitionsHomeController {

    private final AcquisitionsHomeService acquisitionsHomeService;

    /**
     * GET /acquisitions/home
     *
     * <p>Returns the full acquisitions dashboard data for the given patron and branch,
     * equivalent to all template variables set by acqui-home.pl:
     * <ul>
     *   <li>suggestions_count, all_pendingsuggestions</li>
     *   <li>total, totspent, totordered, totcomtd, totavail</li>
     *   <li>total_active, totspent_active, totordered_active, totavail_active</li>
     *   <li>loop_budget, bp_loop</li>
     *   <li>filters (from_placed_on, to_placed_on) defaulting to [today-1y, today]</li>
     *   <li>currency (active acquisition currency code)</li>
     * </ul>
     *
     * @param patronId      borrowernumber of the logged-in patron (required for budget permission filtering)
     * @param branchCode    home branch code of the logged-in patron
     * @param onlyMyLibrary true if the system preference OnlyMyLibrary is enabled
     */
    @GetMapping("/acquisitions/home")
    public ResponseEntity<AcquisitionsHomeDto> getAcquisitionsHome(
            @RequestParam("patron_id") Long patronId,
            @RequestParam("branch_code") String branchCode,
            @RequestParam(value = "only_my_library", defaultValue = "false") boolean onlyMyLibrary) {
        return ResponseEntity.ok(
                acquisitionsHomeService.getAcquisitionsHome(patronId, branchCode, onlyMyLibrary));
    }

    /**
     * GET /acquisitions/budgets
     *
     * <p>Returns the budget hierarchy filtered to budgets the given patron can use
     * (equivalent to {@code GetBudgetHierarchy} + {@code CanUserUseBudget} loop in acqui-home.pl).
     * Each budget includes depth-indented display name, aggregated totals, and owner info.
     *
     * @param budgetPeriodId optional; filters to a specific budget period
     * @param patronId       filters budgets by user permission
     */
    @GetMapping("/acquisitions/budgets")
    public ResponseEntity<List<BudgetDto>> getBudgetHierarchy(
            @RequestParam(value = "budget_period_id", required = false) Long budgetPeriodId,
            @RequestParam("patron_id") Long patronId) {
        return ResponseEntity.ok(acquisitionsHomeService.getBudgetHierarchy(budgetPeriodId, patronId));
    }

    /**
     * GET /acquisitions/budget_periods
     *
     * <p>Returns all budget periods, each containing the nested fund/budget hierarchy
     * (equivalent to {@code GetBudgetPeriods} + {@code GetBudgetHierarchy} per period
     * with depth-based display names in acqui-home.pl).
     */
    @GetMapping("/acquisitions/budget_periods")
    public ResponseEntity<List<BudgetPeriodDto>> getBudgetPeriods() {
        return ResponseEntity.ok(acquisitionsHomeService.getBudgetPeriods());
    }
}

