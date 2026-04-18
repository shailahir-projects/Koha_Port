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

    // Other endpoints (batches, batchstatuses, backends, users) remain TODO
}

