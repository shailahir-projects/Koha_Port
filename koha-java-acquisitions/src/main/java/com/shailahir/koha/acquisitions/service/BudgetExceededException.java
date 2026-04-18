package com.shailahir.koha.acquisitions.service;

/**
 * @deprecated Use {@link com.shailahir.koha.acquisitions.exception.BudgetExceededException} instead.
 * Kept for backward compatibility with existing service implementations.
 */
@Deprecated(since = "1.0", forRemoval = true)
public class BudgetExceededException extends com.shailahir.koha.acquisitions.exception.BudgetExceededException {
    public BudgetExceededException(String message) {
        super(message);
    }
}
