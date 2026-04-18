package com.shailahir.koha.ill.controller;

import com.shailahir.koha.ill.dto.IllRequestCommentDto;
import com.shailahir.koha.ill.dto.IllRequestDto;
import com.shailahir.koha.ill.service.IllService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ILL (Inter-Library Loan) controller - implements core request and comment endpoints.
 */
@RestController
@RequiredArgsConstructor
public class IllController {

    private final IllService service;

    // Requests
    @GetMapping("/ill/requests")
    public ResponseEntity<Page<IllRequestDto>> listIllRequests(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(service.listRequests(q, pageable));
    }

    @PostMapping("/ill/requests")
    public ResponseEntity<IllRequestDto> addIllRequest(@RequestBody IllRequestDto dto) {
        IllRequestDto created = service.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/ill/requests/{ill_request_id}")
    public ResponseEntity<IllRequestDto> getIllRequest(@PathVariable("ill_request_id") Long id) {
        return ResponseEntity.ok(service.getRequest(id));
    }

    @PutMapping("/ill/requests/{ill_request_id}")
    public ResponseEntity<IllRequestDto> updateIllRequest(@PathVariable("ill_request_id") Long id, @RequestBody IllRequestDto dto) {
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }

    @DeleteMapping("/ill/requests/{ill_request_id}")
    public ResponseEntity<Void> deleteIllRequest(@PathVariable("ill_request_id") Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    // Comments
    @GetMapping("/ill/requests/{ill_request_id}/comments")
    public ResponseEntity<List<IllRequestCommentDto>> listComments(@PathVariable("ill_request_id") Long id) {
        return ResponseEntity.ok(service.listComments(id));
    }

    @PostMapping("/ill/requests/{ill_request_id}/comments")
    public ResponseEntity<IllRequestCommentDto> addComment(@PathVariable("ill_request_id") Long id, @RequestBody IllRequestCommentDto dto) {
        IllRequestCommentDto created = service.addComment(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/ill/batches")
    public ResponseEntity<Page<IllBatchDto>> listBatches(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(service.listBatches(q, pageable));
    }

    @PostMapping("/ill/batches")
    public ResponseEntity<IllBatchDto> addBatch(@RequestBody IllBatchDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBatch(dto));
    }

    @GetMapping("/ill/batches/{ill_batch_id}")
    public ResponseEntity<IllBatchDto> getBatch(@PathVariable("ill_batch_id") Long id) {
        return ResponseEntity.ok(service.getBatch(id));
    }

    @PutMapping("/ill/batches/{ill_batch_id}")
    public ResponseEntity<IllBatchDto> updateBatch(@PathVariable("ill_batch_id") Long id, @RequestBody IllBatchDto dto) {
        return ResponseEntity.ok(service.updateBatch(id, dto));
    }

    @DeleteMapping("/ill/batches/{ill_batch_id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable("ill_batch_id") Long id) {
        service.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ill/batchstatuses")
    public ResponseEntity<List<IllBatchStatusDto>> listBatchStatuses() {
        return ResponseEntity.ok(service.listBatchStatuses());
    }

    @PostMapping("/ill/batchstatuses")
    public ResponseEntity<IllBatchStatusDto> addBatchStatus(@RequestBody IllBatchStatusDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBatchStatus(dto));
    }

    @GetMapping("/ill/batchstatuses/{ill_batchstatus_code}")
    public ResponseEntity<IllBatchStatusDto> getBatchStatus(@PathVariable("ill_batchstatus_code") String code) {
        return ResponseEntity.ok(service.getBatchStatus(code));
    }

    @PutMapping("/ill/batchstatuses/{ill_batchstatus_code}")
    public ResponseEntity<IllBatchStatusDto> updateBatchStatus(@PathVariable("ill_batchstatus_code") String code, @RequestBody IllBatchStatusDto dto) {
        return ResponseEntity.ok(service.updateBatchStatus(code, dto));
    }

    @DeleteMapping("/ill/batchstatuses/{ill_batchstatus_code}")
    public ResponseEntity<Void> deleteBatchStatus(@PathVariable("ill_batchstatus_code") String code) {
        service.deleteBatchStatus(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ill/backends")
    public ResponseEntity<List<IllBackendDto>> listBackends() {
        return ResponseEntity.ok(service.listBackends());
    }

    @GetMapping("/ill/backends/{ill_backend_id}")
    public ResponseEntity<IllBackendDto> getBackend(@PathVariable("ill_backend_id") String id) {
        return ResponseEntity.ok(service.getBackend(id));
    }

    @GetMapping("/ill/users")
    public ResponseEntity<Page<IllUserDto>> listUsers(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(service.listUsers(q, pageable));
    }
}

