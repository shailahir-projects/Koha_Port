package com.shailahir.koha.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Batch controller covering:
 * - /jobs (GET)
 * - /jobs/{job_id} (GET)
 */
@RestController
@RequiredArgsConstructor
public class BatchController {

    @GetMapping("/jobs")
    public ResponseEntity<List<Map<String, Object>>> listJobs(Pageable pageable) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/jobs/{job_id}")
    public ResponseEntity<Map<String, Object>> getJob(@PathVariable("job_id") Long jobId) {
        return ResponseEntity.ok(Map.of());
    }
}

