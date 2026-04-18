package com.shailahir.koha.finance.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Collections;
@Slf4j
@RestController
@RequestMapping("/api/v1/cash-registers/{registerId}/cashups")
@RequiredArgsConstructor
@Validated
public class CashupController {

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> list(Pageable pageable) {
        log.debug("Entering list - {}", pageable);
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.debug("Entering getById - {}", id);
        return ResponseEntity.ok(Collections.emptyMap());
    }
}
