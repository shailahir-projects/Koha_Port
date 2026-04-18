package com.shailahir.koha.acquisitions.exception;

/**
 * Base runtime exception for all acquisitions domain errors.
 */
public class AcquisitionException extends RuntimeException {
    public AcquisitionException(String message) {
        super(message);
    }
    public AcquisitionException(String message, Throwable cause) {
        super(message, cause);
    }
}

