package com.shailahir.koha.acquisitions.exception;

/**
 * Thrown when an operation is attempted on a basket that is closed,
 * or when a basket operation violates business rules (e.g., transferring
 * an order to a basket from a different vendor).
 */
public class BasketOperationException extends AcquisitionException {
    public BasketOperationException(String message) {
        super(message);
    }
}

