package com.shailahir.koha.erm.controller;

import com.shailahir.koha.erm.dto.*;
import com.shailahir.koha.erm.service.ErmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.server.ResponseStatusException;

/**
 * ERM controller covering agreements, licenses, and eHoldings packages.
 * Mirrors: erm/*.pl, Koha/ERM/*.pm
 */
@RestController
@RequiredArgsConstructor
public class ErmController {

    private final ErmService ermService;
    private final JdbcTemplate jdbc;

    // ── Agreements ────────────────────────────────────────────────────────────

    @GetMapping("/erm/agreements")
    public ResponseEntity<Page<AgreementDto>> listAgreements(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listAgreements(query, pageable));
    }

    @PostMapping("/erm/agreements")
    public ResponseEntity<AgreementDto> addAgreement(@RequestBody AgreementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addAgreement(dto));
    }

    @GetMapping("/erm/agreements/{agreement_id}")
    public ResponseEntity<AgreementDto> getAgreement(@PathVariable("agreement_id") Long id) {
        return ResponseEntity.ok(ermService.getAgreement(id));
    }

    @PutMapping("/erm/agreements/{agreement_id}")
    public ResponseEntity<AgreementDto> updateAgreement(@PathVariable("agreement_id") Long id, @RequestBody AgreementDto dto) {
        return ResponseEntity.ok(ermService.updateAgreement(id, dto));
    }

    @DeleteMapping("/erm/agreements/{agreement_id}")
    public ResponseEntity<Void> deleteAgreement(@PathVariable("agreement_id") Long id) {
        ermService.deleteAgreement(id);
        return ResponseEntity.noContent().build();
    }

    // ── Licenses ──────────────────────────────────────────────────────────────

    @GetMapping("/erm/licenses")
    public ResponseEntity<Page<LicenseDto>> listLicenses(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listLicenses(query, pageable));
    }

    @PostMapping("/erm/licenses")
    public ResponseEntity<LicenseDto> addLicense(@RequestBody LicenseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addLicense(dto));
    }

    @GetMapping("/erm/licenses/{license_id}")
    public ResponseEntity<LicenseDto> getLicense(@PathVariable("license_id") Long id) {
        return ResponseEntity.ok(ermService.getLicense(id));
    }

    @PutMapping("/erm/licenses/{license_id}")
    public ResponseEntity<LicenseDto> updateLicense(@PathVariable("license_id") Long id, @RequestBody LicenseDto dto) {
        return ResponseEntity.ok(ermService.updateLicense(id, dto));
    }

    @DeleteMapping("/erm/licenses/{license_id}")
    public ResponseEntity<Void> deleteLicense(@PathVariable("license_id") Long id) {
        ermService.deleteLicense(id);
        return ResponseEntity.noContent().build();
    }

    // ── eHoldings Packages ────────────────────────────────────────────────────

    @GetMapping("/erm/eholdings/packages")
    public ResponseEntity<Page<ErmPackageDto>> listPackages(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listPackages(query, pageable));
    }

    @PostMapping("/erm/eholdings/packages")
    public ResponseEntity<ErmPackageDto> addPackage(@RequestBody ErmPackageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addPackage(dto));
    }

    @GetMapping("/erm/eholdings/packages/{package_id}")
    public ResponseEntity<ErmPackageDto> getPackage(@PathVariable("package_id") Long id) {
        return ResponseEntity.ok(ermService.getPackage(id));
    }

    @PutMapping("/erm/eholdings/packages/{package_id}")
    public ResponseEntity<ErmPackageDto> updatePackage(@PathVariable("package_id") Long id, @RequestBody ErmPackageDto dto) {
        return ResponseEntity.ok(ermService.updatePackage(id, dto));
    }

    @DeleteMapping("/erm/eholdings/packages/{package_id}")
    public ResponseEntity<Void> deletePackage(@PathVariable("package_id") Long id) {
        ermService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    // Provider-aware package routes from Swagger parity
    @GetMapping("/erm/eholdings/{provider}/packages")
    public ResponseEntity<Page<ErmPackageDto>> listProviderPackages(
            @PathVariable("provider") String provider,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(ermService.listPackages(query, pageable));
    }

    @PostMapping("/erm/eholdings/{provider}/packages")
    public ResponseEntity<ErmPackageDto> addProviderPackage(
            @PathVariable("provider") String provider,
            @RequestBody ErmPackageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addPackage(dto));
    }

    @GetMapping("/erm/eholdings/{provider}/packages/{package_id}")
    public ResponseEntity<ErmPackageDto> getProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id) {
        return ResponseEntity.ok(ermService.getPackage(id));
    }

    @PutMapping("/erm/eholdings/{provider}/packages/{package_id}")
    public ResponseEntity<ErmPackageDto> updateProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id,
            @RequestBody ErmPackageDto dto) {
        return ResponseEntity.ok(ermService.updatePackage(id, dto));
    }

    @DeleteMapping("/erm/eholdings/{provider}/packages/{package_id}")
    public ResponseEntity<Void> deleteProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id) {
        ermService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/erm/eholdings/{provider}/packages/{package_id}")
    public ResponseEntity<Map<String, Object>> patchProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id,
            @RequestBody Map<String, Object> body) {
        if (body.containsKey("is_selected")) {
            jdbc.update("UPDATE erm_eholdings_packages SET is_selected = ? WHERE package_id = ?", body.get("is_selected"), id);
        }
        return ResponseEntity.ok(body);
    }

    // Config
    @GetMapping("/erm/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(Map.of("module", "erm", "status", "enabled"));
    }

    // Documents
    @GetMapping("/erm/documents")
    public ResponseEntity<Page<Map<String, Object>>> listDocuments(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_documents", "document_id", pageable));
    }

    @PostMapping("/erm/documents")
    public ResponseEntity<Map<String, Object>> addDocument(@RequestBody Map<String, Object> dto) {
        long id = insertRow("erm_documents", dto, "document_id");
        return ResponseEntity.status(HttpStatus.CREATED).body(getRow("erm_documents", "document_id", id));
    }

    @GetMapping("/erm/documents/{document_id}")
    public ResponseEntity<Map<String, Object>> getDocument(@PathVariable("document_id") Long id) {
        return ResponseEntity.ok(getRow("erm_documents", "document_id", id));
    }

    @PutMapping("/erm/documents/{document_id}")
    public ResponseEntity<Map<String, Object>> updateDocument(@PathVariable("document_id") Long id, @RequestBody Map<String, Object> dto) {
        updateRow("erm_documents", "document_id", id, dto);
        return ResponseEntity.ok(getRow("erm_documents", "document_id", id));
    }

    @DeleteMapping("/erm/documents/{document_id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("document_id") Long id) {
        jdbc.update("DELETE FROM erm_documents WHERE document_id = ?", id);
        return ResponseEntity.noContent().build();
    }

    // Users (ERM user roles)
    @GetMapping("/erm/users")
    public ResponseEntity<Page<Map<String, Object>>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_user_roles", "user_role_id", pageable));
    }

    @PostMapping("/erm/users")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, Object> dto) {
        long id = insertRow("erm_user_roles", dto, "user_role_id");
        return ResponseEntity.status(HttpStatus.CREATED).body(getRow("erm_user_roles", "user_role_id", id));
    }

    @GetMapping("/erm/users/{user_role_id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable("user_role_id") Long id) {
        return ResponseEntity.ok(getRow("erm_user_roles", "user_role_id", id));
    }

    @PutMapping("/erm/users/{user_role_id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("user_role_id") Long id, @RequestBody Map<String, Object> dto) {
        updateRow("erm_user_roles", "user_role_id", id, dto);
        return ResponseEntity.ok(getRow("erm_user_roles", "user_role_id", id));
    }

    @DeleteMapping("/erm/users/{user_role_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_role_id") Long id) {
        jdbc.update("DELETE FROM erm_user_roles WHERE user_role_id = ?", id);
        return ResponseEntity.noContent().build();
    }

    // eHoldings resources
    @GetMapping("/erm/eholdings/{provider}/resources")
    public ResponseEntity<Page<Map<String, Object>>> listResources(@PathVariable("provider") String provider, Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_eholdings_resources", "resource_id", pageable));
    }

    @GetMapping("/erm/eholdings/{provider}/packages/{package_id}/resources")
    public ResponseEntity<Page<Map<String, Object>>> listPackageResources(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long packageId,
            Pageable pageable) {
        return ResponseEntity.ok(pageQueryByForeignKey("erm_eholdings_resources", "resource_id", "package_id", packageId, pageable));
    }

    @GetMapping("/erm/eholdings/{provider}/titles/{title_id}/resources")
    public ResponseEntity<Page<Map<String, Object>>> listTitleResources(
            @PathVariable("provider") String provider,
            @PathVariable("title_id") Long titleId,
            Pageable pageable) {
        return ResponseEntity.ok(pageQueryByForeignKey("erm_eholdings_resources", "resource_id", "title_id", titleId, pageable));
    }

    @GetMapping("/erm/eholdings/{provider}/resources/{resource_id}")
    public ResponseEntity<Map<String, Object>> getResource(@PathVariable("provider") String provider, @PathVariable("resource_id") Long id) {
        return ResponseEntity.ok(getRow("erm_eholdings_resources", "resource_id", id));
    }

    @PatchMapping("/erm/eholdings/{provider}/resources/{resource_id}")
    public ResponseEntity<Map<String, Object>> patchResource(
            @PathVariable("provider") String provider,
            @PathVariable("resource_id") Long id,
            @RequestBody Map<String, Object> body) {
        if (body.containsKey("is_selected")) {
            body.remove("is_selected");
        }
        updateRow("erm_eholdings_resources", "resource_id", id, body);
        return ResponseEntity.ok(body);
    }

    // eHoldings titles
    @GetMapping("/erm/eholdings/{provider}/titles")
    public ResponseEntity<Page<Map<String, Object>>> listTitles(@PathVariable("provider") String provider, Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_eholdings_titles", "title_id", pageable));
    }

    @PostMapping("/erm/eholdings/{provider}/titles")
    public ResponseEntity<Map<String, Object>> addTitle(@PathVariable("provider") String provider, @RequestBody Map<String, Object> dto) {
        long id = insertRow("erm_eholdings_titles", dto, "title_id");
        return ResponseEntity.status(HttpStatus.CREATED).body(getRow("erm_eholdings_titles", "title_id", id));
    }

    @GetMapping("/erm/eholdings/{provider}/titles/{title_id}")
    public ResponseEntity<Map<String, Object>> getTitle(@PathVariable("provider") String provider, @PathVariable("title_id") Long id) {
        return ResponseEntity.ok(getRow("erm_eholdings_titles", "title_id", id));
    }

    @PutMapping("/erm/eholdings/{provider}/titles/{title_id}")
    public ResponseEntity<Map<String, Object>> updateTitle(@PathVariable("provider") String provider, @PathVariable("title_id") Long id, @RequestBody Map<String, Object> dto) {
        updateRow("erm_eholdings_titles", "title_id", id, dto);
        return ResponseEntity.ok(getRow("erm_eholdings_titles", "title_id", id));
    }

    @DeleteMapping("/erm/eholdings/{provider}/titles/{title_id}")
    public ResponseEntity<Void> deleteTitle(@PathVariable("provider") String provider, @PathVariable("title_id") Long id) {
        jdbc.update("DELETE FROM erm_eholdings_titles WHERE title_id = ?", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/erm/eholdings/local/titles/import")
    public ResponseEntity<Map<String, Object>> importTitles(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("job_id", "queued-local-title-import"));
    }

    @PostMapping("/erm/eholdings/local/titles/import_kbart")
    public ResponseEntity<Map<String, Object>> importTitlesFromKbart(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("job_ids", List.of("queued-kbart-import")));
    }

    // EUsage / Counter families
    @GetMapping("/erm/counter/files")
    public ResponseEntity<Page<Map<String, Object>>> listCounterFiles(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_counter_files", "erm_counter_files_id", pageable));
    }

    @GetMapping("/erm/counter/logs")
    public ResponseEntity<Page<Map<String, Object>>> listCounterLogs(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_counter_logs", "erm_counter_log_id", pageable));
    }

    @GetMapping("/erm/default_usage_reports")
    public ResponseEntity<Page<Map<String, Object>>> listDefaultUsageReports(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_default_usage_reports", "erm_default_usage_report_id", pageable));
    }

    @GetMapping("/erm/usage/data_providers")
    public ResponseEntity<Page<Map<String, Object>>> listUsageDataProviders(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_usage_data_providers", "erm_usage_data_provider_id", pageable));
    }

    @GetMapping("/erm/usage/databases")
    public ResponseEntity<Page<Map<String, Object>>> listUsageDatabases(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_usage_databases", "database_id", pageable));
    }

    @GetMapping("/erm/usage/items")
    public ResponseEntity<Page<Map<String, Object>>> listUsageItems(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_usage_items", "item_id", pageable));
    }

    @GetMapping("/erm/usage/platforms")
    public ResponseEntity<Page<Map<String, Object>>> listUsagePlatforms(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_usage_platforms", "platform_id", pageable));
    }

    @GetMapping("/erm/usage/titles")
    public ResponseEntity<Page<Map<String, Object>>> listUsageTitles(Pageable pageable) {
        return ResponseEntity.ok(pageQuery("erm_usage_titles", "title_id", pageable));
    }

    @GetMapping("/erm/counter/registries")
    public ResponseEntity<List<Map<String, Object>>> listCounterRegistries() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT DISTINCT service_url, report_release, customer_id FROM erm_usage_data_providers WHERE service_url IS NOT NULL"));
    }

    @GetMapping("/erm/custom_reports")
    public ResponseEntity<List<Map<String, Object>>> listCustomReports() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT id, report_name, notes FROM saved_sql WHERE report_group ILIKE 'ERM%' ORDER BY id DESC"));
    }

    @GetMapping("/erm/sushi_services")
    public ResponseEntity<List<Map<String, Object>>> listSushiServices() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT erm_usage_data_provider_id, name, service_url, service_type, report_release FROM erm_usage_data_providers ORDER BY erm_usage_data_provider_id DESC"));
    }

    @GetMapping("/erm/extended_attribute_types")
    public ResponseEntity<List<Map<String, Object>>> listExtendedAttributeTypes() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT * FROM additional_field_types WHERE tablename ILIKE 'erm_%' ORDER BY id"));
    }

    private Page<Map<String, Object>> pageQuery(String table, String orderColumn, Pageable pageable) {
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM " + table + " ORDER BY " + orderColumn + " DESC LIMIT ? OFFSET ?",
                pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(rows, pageable, total != null ? total : 0);
    }

    private Page<Map<String, Object>> pageQueryByForeignKey(
            String table,
            String orderColumn,
            String foreignKeyColumn,
            Long foreignKeyValue,
            Pageable pageable) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + foreignKeyColumn + " = ?",
                Integer.class,
                foreignKeyValue);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM " + table + " WHERE " + foreignKeyColumn + " = ? ORDER BY " + orderColumn + " DESC LIMIT ? OFFSET ?",
                foreignKeyValue,
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(rows, pageable, total != null ? total : 0);
    }

    private Map<String, Object> getRow(String table, String idColumn, Long id) {
        try {
            return jdbc.queryForMap("SELECT * FROM " + table + " WHERE " + idColumn + " = ?", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }

    private long insertRow(String table, Map<String, Object> body, String idColumn) {
        if (body == null || body.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be empty");
        }
        List<String> keys = new ArrayList<>(body.keySet());
        String columns = String.join(", ", keys);
        String placeholders = String.join(", ", keys.stream().map(k -> "?").toList());
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")",
                    Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < keys.size(); i++) {
                ps.setObject(i + 1, body.get(keys.get(i)));
            }
            return ps;
        }, kh);
        Number id = (Number) kh.getKeys().get(idColumn);
        if (id == null) {
            Object first = new LinkedHashMap<>(kh.getKeys()).values().stream().findFirst().orElseThrow();
            id = (Number) first;
        }
        return id.longValue();
    }

    private void updateRow(String table, String idColumn, Long id, Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            return;
        }
        List<String> keys = new ArrayList<>(body.keySet());
        String setSql = String.join(", ", keys.stream().map(k -> k + "=?").toList());
        List<Object> values = new ArrayList<>();
        for (String key : keys) {
            values.add(body.get(key));
        }
        values.add(id);
        jdbc.update("UPDATE " + table + " SET " + setSql + " WHERE " + idColumn + " = ?", values.toArray());
    }
}
