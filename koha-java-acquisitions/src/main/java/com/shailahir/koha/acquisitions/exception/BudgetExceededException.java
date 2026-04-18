package com.shailahir.koha.acquisitions.exception;

import com.shailahir.koha.acquisitions.dto.BudgetCheckResult;

/**
 * Thrown when an order total exceeds the available budget.
 * Ported from Koha's budget validation logic in addorder.pl.
 */
public class BudgetExceededException extends AcquisitionException {

    private final BudgetCheckResult checkResult;

    public BudgetExceededException(String message) {
        super(message);
        this.checkResult = null;
    }

    public BudgetExceededException(String message, BudgetCheckResult checkResult) {
        super(message);
        this.checkResult = checkResult;
    }

    public BudgetCheckResult getCheckResult() {
        return checkResult;
    }
}

