package com.shailahir.koha.patron.exception;

public class PatronNotFoundException extends RuntimeException {
    public PatronNotFoundException(Long patronId) {
        super("Patron not found: " + patronId);
    }
    public PatronNotFoundException(String message) {
        super(message);
    }
}

