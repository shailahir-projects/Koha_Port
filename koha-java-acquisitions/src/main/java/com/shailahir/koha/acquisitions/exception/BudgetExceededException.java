package com.shailahir.koha.acquisitions.exception;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.BudgetCheckResult;

/**
 * Thrown when an order total exceeds the available budget.
 * Ported from Koha's budget validation logic in addorder.pl.
 */
@Slf4j
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
        log.debug("Entering getCheckResult");
        return checkResult;
    }
}

