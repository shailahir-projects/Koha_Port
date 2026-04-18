package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.exception.AcquisitionException;
import com.shailahir.koha.acquisitions.exception.BasketOperationException;
import com.shailahir.koha.acquisitions.exception.BudgetExceededException;
import com.shailahir.koha.acquisitions.exception.DuplicateBiblioException;
import com.shailahir.koha.acquisitions.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Centralised exception handler for all acquisitions REST endpoints.
 * Maps domain exceptions to appropriate HTTP status codes and error bodies.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("handleResourceNotFound: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "error_code", "not_found",
                        "resource_type", ex.getResourceType()));
    }

    @ExceptionHandler(BudgetExceededException.class)
    public ResponseEntity<Map<String, Object>> handleBudgetExceeded(BudgetExceededException ex) {
        log.warn("Budget exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "error_code", "budget_exceeded",
                        "check_result", ex.getCheckResult() != null ? ex.getCheckResult() : "n/a"));
    }

    @ExceptionHandler(DuplicateBiblioException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateBiblio(DuplicateBiblioException ex) {
        log.warn("Duplicate biblio detected: biblionumber={}", ex.getExistingBiblionumber());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "error_code", "duplicate_biblio",
                        "existing_biblionumber", ex.getExistingBiblionumber()));
    }

    @ExceptionHandler(BasketOperationException.class)
    public ResponseEntity<Map<String, Object>> handleBasketOperation(BasketOperationException ex) {
        log.error("handleBasketOperation: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", ex.getMessage(), "error_code", "basket_operation_error"));
    }

    @ExceptionHandler(AcquisitionException.class)
    public ResponseEntity<Map<String, Object>> handleAcquisition(AcquisitionException ex) {
        log.error("Acquisition error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage(), "error_code", "acquisition_error"));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        log.error("handleNotFound: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "error_code", "not_found"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        log.error("handleBadRequest: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage(), "error_code", "bad_request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternal(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage(), "error_code", "internal_server_error"));
    }
}
