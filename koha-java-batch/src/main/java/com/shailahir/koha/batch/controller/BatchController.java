package com.shailahir.koha.batch.controller;

import com.shailahir.koha.batch.dto.JobDto;
import com.shailahir.koha.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Batch controller - implements background job management.
 */
@RestController
@RequiredArgsConstructor
public class BatchController {

    private final BatchService service;

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobDto>> listJobs(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(service.listJobs(q, pageable));
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobDto> addJob(@RequestBody JobDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addJob(dto));
    }

    @GetMapping("/jobs/{job_id}")
    public ResponseEntity<JobDto> getJob(@PathVariable("job_id") Long jobId) {
        return ResponseEntity.ok(service.getJob(jobId));
    }

    @PutMapping("/jobs/{job_id}")
    public ResponseEntity<JobDto> updateJob(@PathVariable("job_id") Long jobId, @RequestBody JobDto dto) {
        return ResponseEntity.ok(service.updateJob(jobId, dto));
    }

    @DeleteMapping("/jobs/{job_id}")
    public ResponseEntity<Void> deleteJob(@PathVariable("job_id") Long jobId) {
        service.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}

