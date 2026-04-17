package com.shailahir.koha.acquisitions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for GET /acquisitions/home — equivalent to acqui-home.pl.
 * Contains budget summaries, budget periods, suggestions count, and active currency.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AcquisitionsHomeDto {

    // ── Suggestions ──
    /** Pending (ASKED) suggestions for the user's home branch */
    private Long suggestionsCount;
    /** Total pending suggestions across all branches (0 if same as local count) */
    private Long allPendingSuggestions;

    // ── Budget totals (all viewable budgets) ──
    private BigDecimal total;
    private BigDecimal totalSpent;
    private BigDecimal totalOrdered;
    private BigDecimal totalCommitted;
    private BigDecimal totalAvailable;

    // ── Budget totals (active periods only) ──
    private BigDecimal totalActive;
    private BigDecimal totalSpentActive;
    private BigDecimal totalOrderedActive;
    private BigDecimal totalAvailableActive;

    // ── Budget list (filtered by user permission) ──
    private List<BudgetDto> budgets;

    // ── Budget period list with nested hierarchy ──
    private List<BudgetPeriodDto> budgetPeriods;

    // ── Date filter defaults (used by UI) ──
    private LocalDate fromPlacedOn;
    private LocalDate toPlacedOn;

    // ── Active currency ──
    private String activeCurrency;
}

