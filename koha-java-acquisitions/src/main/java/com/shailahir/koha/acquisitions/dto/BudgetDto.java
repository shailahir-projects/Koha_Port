package com.shailahir.koha.acquisitions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BudgetDto {
    private Long budgetId;
    private Long budgetPeriodId;
    private Long budgetParentId;
    private String budgetName;
    private String budgetDisplayName;
    private Long budgetOwnerId;
    private BigDecimal budgetAmount;
    private BigDecimal budgetSpent;
    private BigDecimal budgetOrdered;
    private BigDecimal budgetAvail;
    private BigDecimal totalSpent;
    private BigDecimal totalOrdered;
    private BigDecimal totalAvail;
    private Boolean budgetPeriodActive;
    private Integer depth;
    private String budgetOwnerName;
    private List<String> budgetUsers;
    private List<String> budgetLibraries;
}

