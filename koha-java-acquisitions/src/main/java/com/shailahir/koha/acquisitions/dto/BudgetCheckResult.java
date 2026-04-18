package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Budget validation result — mirrors the budget-exceeded logic in addorder.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class BudgetCheckResult {

    private boolean exceeded;
    private boolean encumbranceExceeded;
    private boolean expenditureExceeded;

    private BigDecimal budgetRemaining;
    private BigDecimal budgetEncumbrance;
    private BigDecimal budgetExpenditure;
    private String currencySymbol;
}

