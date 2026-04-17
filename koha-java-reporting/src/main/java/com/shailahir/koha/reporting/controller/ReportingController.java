package com.shailahir.koha.reporting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Reporting controller - derived from Koha reports module business logic.
 * No swagger path file exists; endpoints modeled from Koha::Reports Perl module.
 *
 * Covers:
 * - /reports (GET, POST)
 * - /reports/{report_id} (GET, PUT, DELETE)
 * - /reports/{report_id}/run (POST)
 * - /reports/groups (GET)
 * - /reports/statistics (GET)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportingController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listReports(Pageable pageable) { return ResponseEntity.ok(List.of()); }
    @PostMapping
    public ResponseEntity<Map<String, Object>> addReport(@RequestBody Map<String, Object> dto) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of()); }
    @GetMapping("/{report_id}")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable("report_id") Long id) { return ResponseEntity.ok(Map.of()); }
    @PutMapping("/{report_id}")
    public ResponseEntity<Map<String, Object>> updateReport(@PathVariable("report_id") Long id, @RequestBody Map<String, Object> dto) { return ResponseEntity.ok(Map.of()); }
    @DeleteMapping("/{report_id}")
    public ResponseEntity<Void> deleteReport(@PathVariable("report_id") Long id) { return ResponseEntity.noContent().build(); }
    @PostMapping("/{report_id}/run")
    public ResponseEntity<Map<String, Object>> runReport(@PathVariable("report_id") Long id, @RequestBody(required = false) Map<String, Object> params) { return ResponseEntity.ok(Map.of()); }
    @GetMapping("/groups")
    public ResponseEntity<List<Map<String, Object>>> listReportGroups() { return ResponseEntity.ok(List.of()); }
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() { return ResponseEntity.ok(Map.of()); }
}

