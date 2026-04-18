package com.shailahir.koha.ill.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.ill.dto.IllRequestCommentDto;
import com.shailahir.koha.ill.dto.IllRequestDto;
import com.shailahir.koha.ill.service.IllService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * ILL (Inter-Library Loan) controller - implements core request and comment endpoints.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class IllController {

    private final IllService service;

    // Requests
    @GetMapping("/ill/requests", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<IllRequestDto>> listIllRequests(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        log.debug("Entering listIllRequests - {}, {}", q, pageable);
        return ResponseEntity.ok(service.listRequests(q, pageable));
    }

    @PostMapping("/ill/requests", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllRequestDto> addIllRequest(@RequestBody IllRequestDto dto) {
        log.debug("Entering addIllRequest - {}", dto);
        IllRequestDto created = service.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/ill/requests/{ill_request_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllRequestDto> getIllRequest(@PathVariable("ill_request_id") Long id) {
        log.debug("Entering getIllRequest - {}", id);
        return ResponseEntity.ok(service.getRequest(id));
    }

    @PutMapping("/ill/requests/{ill_request_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllRequestDto> updateIllRequest(@PathVariable("ill_request_id") Long id, @RequestBody IllRequestDto dto) {
        log.debug("Entering updateIllRequest - {}, {}", id, dto);
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }

    @DeleteMapping("/ill/requests/{ill_request_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteIllRequest(@PathVariable("ill_request_id") Long id) {
        log.debug("Entering deleteIllRequest - {}", id);
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    // Comments
    @GetMapping("/ill/requests/{ill_request_id}/comments", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<IllRequestCommentDto>> listComments(@PathVariable("ill_request_id") Long id) {
        log.debug("Entering listComments - {}", id);
        return ResponseEntity.ok(service.listComments(id));
    }

    @PostMapping("/ill/requests/{ill_request_id}/comments", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllRequestCommentDto> addComment(@PathVariable("ill_request_id") Long id, @RequestBody IllRequestCommentDto dto) {
        log.debug("Entering addComment - {}, {}", id, dto);
        IllRequestCommentDto created = service.addComment(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/ill/batches", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<IllBatchDto>> listBatches(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        log.debug("Entering listBatches - {}, {}", q, pageable);
        return ResponseEntity.ok(service.listBatches(q, pageable));
    }

    @PostMapping("/ill/batches", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBatchDto> addBatch(@RequestBody IllBatchDto dto) {
        log.debug("Entering addBatch - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBatch(dto));
    }

    @GetMapping("/ill/batches/{ill_batch_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBatchDto> getBatch(@PathVariable("ill_batch_id") Long id) {
        log.debug("Entering getBatch - {}", id);
        return ResponseEntity.ok(service.getBatch(id));
    }

    @PutMapping("/ill/batches/{ill_batch_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBatchDto> updateBatch(@PathVariable("ill_batch_id") Long id, @RequestBody IllBatchDto dto) {
        log.debug("Entering updateBatch - {}, {}", id, dto);
        return ResponseEntity.ok(service.updateBatch(id, dto));
    }

    @DeleteMapping("/ill/batches/{ill_batch_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteBatch(@PathVariable("ill_batch_id") Long id) {
        log.debug("Entering deleteBatch - {}", id);
        service.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ill/batchstatuses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<IllBatchStatusDto>> listBatchStatuses() {
        log.debug("Entering listBatchStatuses");
        return ResponseEntity.ok(service.listBatchStatuses());
    }

    @PostMapping("/ill/batchstatuses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBatchStatusDto> addBatchStatus(@RequestBody IllBatchStatusDto dto) {
        log.debug("Entering addBatchStatus - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBatchStatus(dto));
    }

    @GetMapping("/ill/batchstatuses/{ill_batchstatus_code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBatchStatusDto> getBatchStatus(@PathVariable("ill_batchstatus_code") String code) {
        log.debug("Entering getBatchStatus - {}", code);
        return ResponseEntity.ok(service.getBatchStatus(code));
    }

    @PutMapping("/ill/batchstatuses/{ill_batchstatus_code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBatchStatusDto> updateBatchStatus(@PathVariable("ill_batchstatus_code") String code, @RequestBody IllBatchStatusDto dto) {
        log.debug("Entering updateBatchStatus - {}, {}", code, dto);
        return ResponseEntity.ok(service.updateBatchStatus(code, dto));
    }

    @DeleteMapping("/ill/batchstatuses/{ill_batchstatus_code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteBatchStatus(@PathVariable("ill_batchstatus_code") String code) {
        log.debug("Entering deleteBatchStatus - {}", code);
        service.deleteBatchStatus(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ill/backends", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<IllBackendDto>> listBackends() {
        log.debug("Entering listBackends");
        return ResponseEntity.ok(service.listBackends());
    }

    @GetMapping("/ill/backends/{ill_backend_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<IllBackendDto> getBackend(@PathVariable("ill_backend_id") String id) {
        log.debug("Entering getBackend - {}", id);
        return ResponseEntity.ok(service.getBackend(id));
    }

    @GetMapping("/ill/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<IllUserDto>> listUsers(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        log.debug("Entering listUsers - {}, {}", q, pageable);
        return ResponseEntity.ok(service.listUsers(q, pageable));
    }
}

