package com.shailahir.koha.patron.controller;
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
@RequestMapping("/api/v1/patrons/{patronId}/extended-attributes")
@RequiredArgsConstructor
@Validated
public class PatronAttributeController {

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> create(@Valid @RequestBody Object request) {
        log.debug("Entering create - {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.emptyMap());
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Object request) {
        log.debug("Entering update - {}, {}", id, request);
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("Entering delete - {}", id);
        return ResponseEntity.noContent().build();
    }
}
