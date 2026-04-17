package com.shailahir.koha.acquisitions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BudgetPeriodDto {
    private Long budgetPeriodId;
    private String budgetPeriodDescription;
    private LocalDate budgetPeriodStartDate;
    private LocalDate budgetPeriodEndDate;
    private Boolean budgetPeriodActive;
    private Boolean budgetPeriodLocked;
    private BigDecimal budgetPeriodTotalAmount;
    private List<BudgetDto> hierarchy;
}

