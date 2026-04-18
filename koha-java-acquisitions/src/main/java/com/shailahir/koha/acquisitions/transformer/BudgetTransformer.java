package com.shailahir.koha.acquisitions.transformer;

import com.shailahir.koha.acquisitions.dto.BudgetDto;
import com.shailahir.koha.acquisitions.dto.BudgetPeriodDto;
import com.shailahir.koha.acquisitions.dto.SpentDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Transformer for Budget/Fund-related DTOs.
 */
@Component
public class BudgetTransformer {

    public BudgetDto toDto(Map<String, Object> row, BigDecimal spent, BigDecimal ordered) {
        if (row == null) return null;
        BigDecimal amount = toBigDecimal(row, "budget_amount");
        BigDecimal safeSpent   = spent   != null ? spent   : BigDecimal.ZERO;
        BigDecimal safeOrdered = ordered != null ? ordered : BigDecimal.ZERO;
        BigDecimal avail = amount != null
                ? amount.subtract(safeSpent).subtract(safeOrdered) : BigDecimal.ZERO;

        return BudgetDto.builder()
                .budgetId(toLong(row, "budget_id"))
                .budgetName(toString(row, "budget_name"))
                .budgetAmount(amount)
                .budgetSpent(safeSpent)
                .budgetOrdered(safeOrdered)
                .budgetAvail(avail)
                .budgetPeriodId(toLong(row, "budget_period_id"))
                .build();
    }

    public BudgetPeriodDto toPeriodDto(Map<String, Object> row) {
        if (row == null) return null;
        return BudgetPeriodDto.builder()
                .budgetPeriodId(toLong(row, "budget_period_id"))
                .budgetPeriodDescription(toString(row, "budget_period_description"))
                .budgetPeriodActive(toBoolean(row, "budget_period_active"))
                .build();
    }

    public SpentDto toSpentDto(BudgetDto dto) {
        if (dto == null) return null;
        return SpentDto.builder()
                .budgetId(dto.getBudgetId())
                .budgetName(dto.getBudgetName())
                .budgetAmount(dto.getBudgetAmount())
                .budgetSpent(dto.getBudgetSpent())
                .budgetOrdered(dto.getBudgetOrdered())
                .budgetRemaining(dto.getBudgetAvail())
                .budgetPeriodId(dto.getBudgetPeriodId())
                .build();
    }

    private Long toLong(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? ((Number) v).longValue() : null;
    }
    private String toString(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString() : null;
    }
    private Boolean toBoolean(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v == null) return false;
        if (v instanceof Boolean b) return b;
        return ((Number) v).intValue() != 0;
    }
    private BigDecimal toBigDecimal(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v == null) return null;
        if (v instanceof BigDecimal bd) return bd;
        return new BigDecimal(v.toString());
    }
}
