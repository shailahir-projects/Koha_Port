package com.shailahir.koha.reporting.controller;

import com.shailahir.koha.reporting.dto.ReportResultDto;
import com.shailahir.koha.reporting.dto.SavedReportDto;
import com.shailahir.koha.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

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
    private final JdbcTemplate jdbc;

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

    @GetMapping("/{report_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SavedReportDto> getReport(@PathVariable("report_id") Long id) {
        return ResponseEntity.ok(reportingService.getReport(id));
    }

    @PutMapping("/{report_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SavedReportDto> updateReport(@PathVariable("report_id") Long id, @RequestBody SavedReportDto dto) {
        return ResponseEntity.ok(reportingService.updateReport(id, dto));
    }

    @DeleteMapping("/{report_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteReport(@PathVariable("report_id") Long id) {
        reportingService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{report_id}/run", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ReportResultDto> runReport(
            @PathVariable("report_id") Long id,
            @RequestBody(required = false) Map<String, String> params) {
        return ResponseEntity.ok(reportingService.runReport(id, params));
    }

    @GetMapping("/home", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> reportsHome() {
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM saved_sql", Integer.class);
        Integer publics = jdbc.queryForObject("SELECT COUNT(*) FROM saved_sql WHERE public = 1", Integer.class);
        return ResponseEntity.ok(Map.of(
                "total_reports", total != null ? total : 0,
                "public_reports", publics != null ? publics : 0));
    }

    @GetMapping("/dictionary", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> reportDictionary() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT DISTINCT report_group, report_subgroup FROM saved_sql ORDER BY report_group, report_subgroup"));
    }

    @GetMapping("/stats/{stat_type}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> reportStats(@PathVariable("stat_type") String statType) {
        String sql = switch (statType) {
            case "issues" -> "SELECT DATE(date) AS day, COUNT(*) AS total FROM issues GROUP BY DATE(date) ORDER BY day DESC LIMIT 100";
            case "borrowers" -> "SELECT categorycode, COUNT(*) AS total FROM borrowers GROUP BY categorycode ORDER BY total DESC";
            case "reserves" -> "SELECT found, COUNT(*) AS total FROM reserves GROUP BY found ORDER BY total DESC";
            default -> throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown stat type");
        };
        return ResponseEntity.ok(jdbc.queryForList(sql));
    }

    @GetMapping("/acquisitions_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> acquisitionsStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT DATE(datereceived) AS day, COUNT(*) AS total FROM aqorders GROUP BY DATE(datereceived) ORDER BY day DESC LIMIT 100"));
    }

    @GetMapping("/borrowers_out", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> borrowersOut() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT borrowernumber, cardnumber, surname, firstname, dateexpiry FROM borrowers ORDER BY dateexpiry DESC LIMIT 500"));
    }

    @GetMapping("/borrowers_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> borrowersStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT categorycode, COUNT(*) AS total FROM borrowers GROUP BY categorycode ORDER BY total DESC"));
    }

    @GetMapping("/bor_issues_top", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> borrowerIssuesTop() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT borrowernumber, COUNT(*) AS total FROM statistics WHERE type = 'issue' GROUP BY borrowernumber ORDER BY total DESC LIMIT 100"));
    }

    @GetMapping("/cash_register_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> cashRegisterStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT register_id, type, COUNT(*) AS total, SUM(amount) AS amount FROM cash_register_actions GROUP BY register_id, type ORDER BY register_id"));
    }

    @GetMapping("/catalogue_out", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> catalogueOut() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT biblionumber, title FROM biblio ORDER BY biblionumber DESC LIMIT 500"));
    }

    @GetMapping("/catalogue_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> catalogueStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT itemtype, COUNT(*) AS total FROM biblioitems GROUP BY itemtype ORDER BY total DESC"));
    }

    @GetMapping("/catalog_by_itemtype", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> catalogByItemType() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT itemtype, COUNT(*) AS total FROM items GROUP BY itemtype ORDER BY total DESC"));
    }

    @GetMapping("/cat_issues_top", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> categoryIssuesTop() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT categorycode, COUNT(*) AS total FROM statistics WHERE type='issue' GROUP BY categorycode ORDER BY total DESC LIMIT 100"));
    }

    @GetMapping("/issues_avg_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> issuesAvgStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT DATE(date) AS day, AVG(1.0) AS avg_issues FROM issues GROUP BY DATE(date) ORDER BY day DESC LIMIT 100"));
    }

    @GetMapping("/issues_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> issuesStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT DATE(date) AS day, COUNT(*) AS total FROM issues GROUP BY DATE(date) ORDER BY day DESC LIMIT 100"));
    }

    @GetMapping("/itemslost", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> itemsLost() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT itemnumber, barcode, itemlost FROM items WHERE itemlost IS NOT NULL ORDER BY itemnumber DESC LIMIT 500"));
    }

    @GetMapping("/orders_by_fund", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> ordersByFund() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT budget_id, COUNT(*) AS total FROM aqorders GROUP BY budget_id ORDER BY total DESC"));
    }

    @GetMapping("/reserves_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> reservesStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT found, COUNT(*) AS total FROM reserves GROUP BY found ORDER BY total DESC"));
    }

    @GetMapping("/serials_stats", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> serialsStats() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT status, COUNT(*) AS total FROM serial GROUP BY status ORDER BY total DESC"));
    }
}
