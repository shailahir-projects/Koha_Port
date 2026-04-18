package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Spent-amount row per fund – mirrors spent.pl. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpentDto {

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("budget_code")
    private String budgetCode;

    @JsonProperty("budget_name")
    private String budgetName;

    @JsonProperty("budget_amount")
    private BigDecimal budgetAmount;

    @JsonProperty("budget_spent")
    private BigDecimal budgetSpent;

    @JsonProperty("budget_ordered")
    private BigDecimal budgetOrdered;

    @JsonProperty("budget_remaining")
    private BigDecimal budgetRemaining;

    @JsonProperty("budget_period_id")
    private Long budgetPeriodId;

    @JsonProperty("budget_period_description")
    private String budgetPeriodDescription;
}

