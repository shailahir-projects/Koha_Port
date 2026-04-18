package com.shailahir.koha.batch.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.batch.dto.JobDto;
import com.shailahir.koha.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

/**
 * Batch controller - implements background job management.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BatchController {

    private final BatchService service;

    @GetMapping("/jobs", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<JobDto>> listJobs(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        log.debug("Entering listJobs - {}, {}", q, pageable);
        return ResponseEntity.ok(service.listJobs(q, pageable));
    }

    @PostMapping("/jobs", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<JobDto> addJob(@RequestBody JobDto dto) {
        log.debug("Entering addJob - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addJob(dto));
    }

    @GetMapping("/jobs/{job_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<JobDto> getJob(@PathVariable("job_id") Long jobId) {
        log.debug("Entering getJob - {}", jobId);
        return ResponseEntity.ok(service.getJob(jobId));
    }

    @PutMapping("/jobs/{job_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<JobDto> updateJob(@PathVariable("job_id") Long jobId, @RequestBody JobDto dto) {
        log.debug("Entering updateJob - {}, {}", jobId, dto);
        return ResponseEntity.ok(service.updateJob(jobId, dto));
    }

    @DeleteMapping("/jobs/{job_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteJob(@PathVariable("job_id") Long jobId) {
        log.debug("Entering deleteJob - {}", jobId);
        service.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}

