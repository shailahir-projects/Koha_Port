package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.AcquisitionsHomeDto;
import com.shailahir.koha.acquisitions.dto.BudgetDto;
import com.shailahir.koha.acquisitions.dto.BudgetPeriodDto;

import java.util.List;

public interface AcquisitionsHomeService {

    /**
     * Assembles the acquisitions dashboard data equivalent to acqui-home.pl.
     * <p>
     * Business logic:
     * <ul>
     *   <li>Counts pending (ASKED, non-archived) purchase suggestions for the user's branch</li>
     *   <li>If the user can see all branches, also counts total across all branches</li>
     *   <li>Loads the full budget hierarchy filtered to budgets the user can use</li>
     *   <li>Computes aggregate totals: amount, spent, ordered, available — for all and active-only</li>
     *   <li>Loads budget periods with nested fund hierarchy</li>
     *   <li>Applies a default date filter of [today-1 year, today] for placed-on range</li>
     *   <li>Returns the active acquisition currency</li>
     * </ul>
     *
     * @param patronId       the currently logged-in patron's borrowernumber
     * @param branchCode     the user's home branch code
     * @param onlyMyLibrary  whether the system preference restricts to local branch only
     * @return               fully assembled dashboard DTO
     */
    AcquisitionsHomeDto getAcquisitionsHome(Long patronId, String branchCode, boolean onlyMyLibrary);

    /**
     * Returns the full budget hierarchy for a given budget period,
     * filtered to budgets the given patron has permission to use.
     * Each budget entry includes depth-based display name (>>> prefix).
     *
     * @param budgetPeriodId  optional period ID; null means all active periods
     * @param patronId        patron to filter by permission
     * @return                list of budgets with hierarchy metadata
     */
    List<BudgetDto> getBudgetHierarchy(Long budgetPeriodId, Long patronId);

    /**
     * Returns all budget periods, each populated with their fund hierarchy.
     *
     * @return list of budget periods with nested budgets
     */
    List<BudgetPeriodDto> getBudgetPeriods();
}

