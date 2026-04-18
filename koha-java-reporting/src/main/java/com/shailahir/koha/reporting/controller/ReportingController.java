package com.shailahir.koha.reporting.controller;

import com.shailahir.koha.reporting.dto.ReportResultDto;
import com.shailahir.koha.reporting.dto.SavedReportDto;
import com.shailahir.koha.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Reporting controller - derived from Koha reports module business logic.
 * Covers:
 * - /reports (GET, POST)
 * - /reports/{report_id} (GET, PUT, DELETE)
 * - /reports/{report_id}/run (POST)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping
    public ResponseEntity<Page<SavedReportDto>> listReports(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(reportingService.listReports(query, pageable));
    }

    @PostMapping
    public ResponseEntity<SavedReportDto> addReport(@RequestBody SavedReportDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportingService.addReport(dto));
    }

    @GetMapping("/{report_id}")
    public ResponseEntity<SavedReportDto> getReport(@PathVariable("report_id") Long id) {
        return ResponseEntity.ok(reportingService.getReport(id));
    }

    @PutMapping("/{report_id}")
    public ResponseEntity<SavedReportDto> updateReport(@PathVariable("report_id") Long id, @RequestBody SavedReportDto dto) {
        return ResponseEntity.ok(reportingService.updateReport(id, dto));
    }

    @DeleteMapping("/{report_id}")
    public ResponseEntity<Void> deleteReport(@PathVariable("report_id") Long id) {
        reportingService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{report_id}/run")
    public ResponseEntity<ReportResultDto> runReport(
            @PathVariable("report_id") Long id,
            @RequestBody(required = false) Map<String, String> params) {
        return ResponseEntity.ok(reportingService.runReport(id, params));
    }
}
