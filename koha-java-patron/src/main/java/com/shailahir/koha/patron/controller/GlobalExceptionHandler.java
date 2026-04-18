package com.shailahir.koha.patron.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;

import com.shailahir.koha.patron.exception.PatronNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PatronNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatronNotFound(PatronNotFoundException ex) {
        log.error("handlePatronNotFound: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(ex.getMessage()).error_code("patron_not_found").build());
    }
    @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        log.error("handleNotFound: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(ex.getMessage()).error_code("not_found").build());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        log.error("handleBadRequest: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().error(ex.getMessage()).error_code("bad_request").build());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternal(Exception ex) {
        log.error("handleInternal: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder().error(ex.getMessage()).error_code("internal_server_error").build());
    }
}

