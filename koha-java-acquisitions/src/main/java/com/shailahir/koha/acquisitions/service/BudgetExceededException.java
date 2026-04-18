package com.shailahir.koha.acquisitions.service;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated Use {@link com.shailahir.koha.acquisitions.exception.BudgetExceededException} instead.
 * Kept for backward compatibility with existing service implementations.
 */
@Slf4j
@Deprecated(since = "1.0", forRemoval = true)
public class BudgetExceededException extends com.shailahir.koha.acquisitions.exception.BudgetExceededException {
    public BudgetExceededException(String message) {
        super(message);
    }
}
