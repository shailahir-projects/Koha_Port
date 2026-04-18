package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.AcquisitionsHomeDto;
import com.shailahir.koha.acquisitions.dto.BudgetDto;
import com.shailahir.koha.acquisitions.dto.BudgetPeriodDto;
import com.shailahir.koha.acquisitions.repository.BudgetRepository;
import com.shailahir.koha.acquisitions.service.AcquisitionsHomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link AcquisitionsHomeService}.
 * Ports the business logic of acqui-home.pl:
 * suggestion counts, budget hierarchy, budget period list, active currency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcquisitionsHomeServiceImpl implements AcquisitionsHomeService {

    private final BudgetRepository budgetRepo;

    // ── Public API ─────────────────────────────────────────────────────────────

    @Override
    public AcquisitionsHomeDto getAcquisitionsHome(Long patronId, String branchCode, boolean onlyMyLibrary) {
        log.debug("Entering getAcquisitionsHome - {}, {}, {}", patronId, branchCode, onlyMyLibrary);

        long suggestionsCount = countPendingSuggestions(branchCode);
        log.debug("getAcquisitionsHome - suggestionsCount for branch {} = {}", branchCode, suggestionsCount);

        long allPending = onlyMyLibrary ? suggestionsCount : countAllPendingSuggestions();
        log.debug("getAcquisitionsHome - allPending = {}", allPending);

        List<BudgetDto> budgets = getBudgetHierarchy(null, patronId);
        log.debug("getAcquisitionsHome - loaded {} budgets for patronId {}", budgets.size(), patronId);

        // Aggregate totals across all viewable budgets
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal totalOrdered = BigDecimal.ZERO;
        BigDecimal totalAvailable = BigDecimal.ZERO;
        BigDecimal totalActive = BigDecimal.ZERO;
        BigDecimal totalSpentActive = BigDecimal.ZERO;
        BigDecimal totalOrderedActive = BigDecimal.ZERO;
        BigDecimal totalAvailableActive = BigDecimal.ZERO;

        for (BudgetDto b : budgets) {
            BigDecimal amt = b.getBudgetAmount() != null ? b.getBudgetAmount() : BigDecimal.ZERO;
            BigDecimal spent = b.getBudgetSpent() != null ? b.getBudgetSpent() : BigDecimal.ZERO;
            BigDecimal ordered = b.getBudgetOrdered() != null ? b.getBudgetOrdered() : BigDecimal.ZERO;
            BigDecimal avail = amt.subtract(spent).subtract(ordered);

            total = total.add(amt);
            totalSpent = totalSpent.add(spent);
            totalOrdered = totalOrdered.add(ordered);
            totalAvailable = totalAvailable.add(avail);

            if (Boolean.TRUE.equals(b.getBudgetPeriodActive())) {
                totalActive = totalActive.add(amt);
                totalSpentActive = totalSpentActive.add(spent);
                totalOrderedActive = totalOrderedActive.add(ordered);
                totalAvailableActive = totalAvailableActive.add(avail);
            }
        }
        log.debug("getAcquisitionsHome - totals: amount={}, spent={}, ordered={}, avail={}",
                total, totalSpent, totalOrdered, totalAvailable);

        List<BudgetPeriodDto> periods = getBudgetPeriods();
        log.debug("getAcquisitionsHome - loaded {} budget periods", periods.size());

        String currency = budgetRepo.getActiveCurrencySymbol().orElse("");
        log.debug("getAcquisitionsHome - active currency = {}", currency);

        LocalDate toDay = LocalDate.now();
        LocalDate fromDay = toDay.minusYears(1);

        AcquisitionsHomeDto dto = AcquisitionsHomeDto.builder()
                .suggestionsCount(suggestionsCount)
                .allPendingSuggestions(allPending)
                .total(total)
                .totalSpent(totalSpent)
                .totalOrdered(totalOrdered)
                .totalCommitted(totalOrdered)
                .totalAvailable(totalAvailable)
                .totalActive(totalActive)
                .totalSpentActive(totalSpentActive)
                .totalOrderedActive(totalOrderedActive)
                .totalAvailableActive(totalAvailableActive)
                .budgets(budgets)
                .budgetPeriods(periods)
                .fromPlacedOn(fromDay)
                .toPlacedOn(toDay)
                .activeCurrency(currency)
                .build();
        log.debug("Exiting getAcquisitionsHome - result assembled");
        return dto;
    }

    @Override
    public List<BudgetDto> getBudgetHierarchy(Long budgetPeriodId, Long patronId) {
        log.debug("Entering getBudgetHierarchy - budgetPeriodId={}, patronId={}", budgetPeriodId, patronId);

        String sql = budgetPeriodId != null
                ? "SELECT b.*, bp.budget_period_active FROM aqbudgets b " +
                  "JOIN aqbudgetperiods bp ON b.budget_period_id = bp.budget_period_id " +
                  "WHERE b.budget_period_id = ? ORDER BY b.budget_id"
                : "SELECT b.*, bp.budget_period_active FROM aqbudgets b " +
                  "JOIN aqbudgetperiods bp ON b.budget_period_id = bp.budget_period_id " +
                  "ORDER BY b.budget_period_id, b.budget_id";

        List<Map<String, Object>> rows = budgetPeriodId != null
                ? budgetRepo.getJdbc().queryForList(sql, budgetPeriodId)
                : budgetRepo.getJdbc().queryForList(sql);
        log.debug("getBudgetHierarchy - query returned {} rows", rows.size());

        List<BudgetDto> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long budgetId = toLong(row.get("budget_id"));
            BigDecimal amount = toBigDecimal(row.get("budget_amount"));
            BigDecimal spent = budgetId != null ? safeGetSpent(budgetId) : BigDecimal.ZERO;
            BigDecimal ordered = budgetId != null ? safeGetOrdered(budgetId) : BigDecimal.ZERO;
            BigDecimal avail = amount.subtract(spent).subtract(ordered);

            String name = str(row.get("budget_name"));
            Long parentId = toLong(row.get("budget_parent_id"));
            int depth = computeDepth(rows, budgetId, parentId, 0);
            String displayName = ">>>".repeat(depth) + " " + name;

            boolean periodActive = Boolean.TRUE.equals(row.get("budget_period_active"))
                    || "1".equals(String.valueOf(row.get("budget_period_active")));

            BudgetDto dto = BudgetDto.builder()
                    .budgetId(budgetId)
                    .budgetPeriodId(toLong(row.get("budget_period_id")))
                    .budgetParentId(parentId)
                    .budgetName(name)
                    .budgetDisplayName(displayName)
                    .budgetOwnerId(toLong(row.get("budget_owner_id")))
                    .budgetAmount(amount)
                    .budgetSpent(spent)
                    .budgetOrdered(ordered)
                    .budgetAvail(avail)
                    .totalSpent(spent)
                    .totalOrdered(ordered)
                    .totalAvail(avail)
                    .budgetPeriodActive(periodActive)
                    .depth(depth)
                    .build();
            result.add(dto);
        }
        log.debug("Exiting getBudgetHierarchy - returning {} budgets", result.size());
        return result;
    }

    @Override
    public List<BudgetPeriodDto> getBudgetPeriods() {
        log.debug("Entering getBudgetPeriods");
        List<Map<String, Object>> periodRows = budgetRepo.getJdbc().queryForList(
                "SELECT * FROM aqbudgetperiods ORDER BY budget_period_id DESC");
        log.debug("getBudgetPeriods - found {} periods", periodRows.size());

        List<BudgetPeriodDto> periods = new ArrayList<>();
        for (Map<String, Object> pr : periodRows) {
            Long periodId = toLong(pr.get("budget_period_id"));
            log.debug("getBudgetPeriods - loading hierarchy for period {}", periodId);
            List<BudgetDto> hierarchy = getBudgetHierarchy(periodId, null);
            boolean active = Boolean.TRUE.equals(pr.get("budget_period_active"))
                    || "1".equals(String.valueOf(pr.get("budget_period_active")));
            boolean locked = Boolean.TRUE.equals(pr.get("budget_period_locked"))
                    || "1".equals(String.valueOf(pr.get("budget_period_locked")));

            BudgetPeriodDto dto = BudgetPeriodDto.builder()
                    .budgetPeriodId(periodId)
                    .budgetPeriodDescription(str(pr.get("budget_period_description")))
                    .budgetPeriodStartDate(toLocalDate(pr.get("budget_period_startdate")))
                    .budgetPeriodEndDate(toLocalDate(pr.get("budget_period_enddate")))
                    .budgetPeriodActive(active)
                    .budgetPeriodLocked(locked)
                    .budgetPeriodTotalAmount(toBigDecimal(pr.get("budget_period_total")))
                    .hierarchy(hierarchy)
                    .build();
            periods.add(dto);
        }
        log.debug("Exiting getBudgetPeriods - returning {} periods", periods.size());
        return periods;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private long countPendingSuggestions(String branchCode) {
        log.debug("Entering countPendingSuggestions - {}", branchCode);
        try {
            Long count = budgetRepo.getJdbc().queryForObject(
                    "SELECT COUNT(*) FROM suggestions WHERE STATUS = 'ASKED' AND branchcode = ?",
                    Long.class, branchCode);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("countPendingSuggestions error for branch {}: {}", branchCode, e.getMessage(), e);
            return 0L;
        }
    }

    private long countAllPendingSuggestions() {
        log.debug("Entering countAllPendingSuggestions");
        try {
            Long count = budgetRepo.getJdbc().queryForObject(
                    "SELECT COUNT(*) FROM suggestions WHERE STATUS = 'ASKED'",
                    Long.class);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("countAllPendingSuggestions error: {}", e.getMessage(), e);
            return 0L;
        }
    }

    private BigDecimal safeGetSpent(Long budgetId) {
        log.debug("Entering safeGetSpent - {}", budgetId);
        try {
            return budgetRepo.getBudgetSpent(budgetId);
        } catch (Exception e) {
            log.error("safeGetSpent error for budget {}: {}", budgetId, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal safeGetOrdered(Long budgetId) {
        log.debug("Entering safeGetOrdered - {}", budgetId);
        try {
            return budgetRepo.getBudgetOrdered(budgetId);
        } catch (Exception e) {
            log.error("safeGetOrdered error for budget {}: {}", budgetId, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    private int computeDepth(List<Map<String, Object>> rows, Long id, Long parentId, int iterations) {
        if (parentId == null || iterations > 10) return 0;
        for (Map<String, Object> r : rows) {
            if (parentId.equals(toLong(r.get("budget_id")))) {
                Long grandParent = toLong(r.get("budget_parent_id"));
                return 1 + computeDepth(rows, parentId, grandParent, iterations + 1);
            }
        }
        return 0;
    }

    private static Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long l) return l;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(o.toString()); } catch (NumberFormatException e) { return null; }
    }

    private static BigDecimal toBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal bd) return bd;
        if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(o.toString()); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    private static LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDate ld) return ld;
        if (o instanceof java.sql.Date sd) return sd.toLocalDate();
        try { return LocalDate.parse(o.toString()); } catch (Exception e) { return null; }
    }
}
