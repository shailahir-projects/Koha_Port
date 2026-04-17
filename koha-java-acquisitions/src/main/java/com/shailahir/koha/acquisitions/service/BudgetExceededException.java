package com.shailahir.koha.acquisitions.service;

/**
 * Thrown when an order would exceed the available budget and the caller
 * has not confirmed they wish to proceed (confirm_budget_exceeding=false).
 */
public class BudgetExceededException extends RuntimeException {

    public BudgetExceededException(String message) {
        super(message);
    }
}

