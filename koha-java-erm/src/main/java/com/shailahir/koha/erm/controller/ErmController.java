package com.shailahir.koha.erm.controller;

import com.shailahir.koha.erm.dto.*;
import com.shailahir.koha.erm.service.ErmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

/**
 * ERM controller covering agreements, licenses, and eHoldings packages.
 * Mirrors: erm/*.pl, Koha/ERM/*.pm
 */
@RestController
@RequiredArgsConstructor
public class ErmController {

    private final ErmService ermService;

    // ── Agreements ────────────────────────────────────────────────────────────

    @GetMapping("/erm/agreements", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<AgreementDto>> listAgreements(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listAgreements(query, pageable));
    }

    @PostMapping("/erm/agreements", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AgreementDto> addAgreement(@RequestBody AgreementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addAgreement(dto));
    }

    @GetMapping("/erm/agreements/{agreement_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AgreementDto> getAgreement(@PathVariable("agreement_id") Long id) {
        return ResponseEntity.ok(ermService.getAgreement(id));
    }

    @PutMapping("/erm/agreements/{agreement_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AgreementDto> updateAgreement(@PathVariable("agreement_id") Long id, @RequestBody AgreementDto dto) {
        return ResponseEntity.ok(ermService.updateAgreement(id, dto));
    }

    @DeleteMapping("/erm/agreements/{agreement_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteAgreement(@PathVariable("agreement_id") Long id) {
        ermService.deleteAgreement(id);
        return ResponseEntity.noContent().build();
    }

    // ── Licenses ──────────────────────────────────────────────────────────────

    @GetMapping("/erm/licenses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LicenseDto>> listLicenses(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listLicenses(query, pageable));
    }

    @PostMapping("/erm/licenses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LicenseDto> addLicense(@RequestBody LicenseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addLicense(dto));
    }

    @GetMapping("/erm/licenses/{license_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LicenseDto> getLicense(@PathVariable("license_id") Long id) {
        return ResponseEntity.ok(ermService.getLicense(id));
    }

    @PutMapping("/erm/licenses/{license_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LicenseDto> updateLicense(@PathVariable("license_id") Long id, @RequestBody LicenseDto dto) {
        return ResponseEntity.ok(ermService.updateLicense(id, dto));
    }

    @DeleteMapping("/erm/licenses/{license_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteLicense(@PathVariable("license_id") Long id) {
        ermService.deleteLicense(id);
        return ResponseEntity.noContent().build();
    }

    // ── eHoldings Packages ────────────────────────────────────────────────────

    @GetMapping("/erm/eholdings/packages", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ErmPackageDto>> listPackages(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listPackages(query, pageable));
    }

    @PostMapping("/erm/eholdings/packages", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ErmPackageDto> addPackage(@RequestBody ErmPackageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addPackage(dto));
    }

    @GetMapping("/erm/eholdings/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ErmPackageDto> getPackage(@PathVariable("package_id") Long id) {
        return ResponseEntity.ok(ermService.getPackage(id));
    }

    @PutMapping("/erm/eholdings/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ErmPackageDto> updatePackage(@PathVariable("package_id") Long id, @RequestBody ErmPackageDto dto) {
        return ResponseEntity.ok(ermService.updatePackage(id, dto));
    }

    @DeleteMapping("/erm/eholdings/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePackage(@PathVariable("package_id") Long id) {
        ermService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    // Provider-aware package routes from Swagger parity
    @GetMapping("/erm/eholdings/{provider}/packages", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ErmPackageDto>> listProviderPackages(
            @PathVariable("provider") String provider,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(ermService.listPackages(query, pageable));
    }

    @PostMapping("/erm/eholdings/{provider}/packages", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ErmPackageDto> addProviderPackage(
            @PathVariable("provider") String provider,
            @RequestBody ErmPackageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addPackage(dto));
    }

    @GetMapping("/erm/eholdings/{provider}/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ErmPackageDto> getProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id) {
        return ResponseEntity.ok(ermService.getPackage(id));
    }

    @PutMapping("/erm/eholdings/{provider}/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ErmPackageDto> updateProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id,
            @RequestBody ErmPackageDto dto) {
        return ResponseEntity.ok(ermService.updatePackage(id, dto));
    }

    @DeleteMapping("/erm/eholdings/{provider}/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id) {
        ermService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/erm/eholdings/{provider}/packages/{package_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> patchProviderPackage(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long id,
            @RequestBody Map<String, Object> body) {
        if (body.containsKey("is_selected")) {
            ermService.updatePackageSelected(id, body.get("is_selected"));
        }
        return ResponseEntity.ok(body);
    }

    // Config
    @GetMapping("/erm/config", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(Map.of("module", "erm", "status", "enabled"));
    }

    // Documents
    @GetMapping("/erm/documents", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listDocuments(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_documents", "document_id", pageable));
    }

    @PostMapping("/erm/documents", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> addDocument(@RequestBody Map<String, Object> dto) {
        long id = ermService.insertRow("erm_documents", dto, "document_id");
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.getRow("erm_documents", "document_id", id));
    }

    @GetMapping("/erm/documents/{document_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getDocument(@PathVariable("document_id") Long id) {
        return ResponseEntity.ok(ermService.getRow("erm_documents", "document_id", id));
    }

    @PutMapping("/erm/documents/{document_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateDocument(@PathVariable("document_id") Long id, @RequestBody Map<String, Object> dto) {
        ermService.updateRow("erm_documents", "document_id", id, dto);
        return ResponseEntity.ok(ermService.getRow("erm_documents", "document_id", id));
    }

    @DeleteMapping("/erm/documents/{document_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteDocument(@PathVariable("document_id") Long id) {
        ermService.deleteRow("erm_documents", "document_id", id);
        return ResponseEntity.noContent().build();
    }

    // Users (ERM user roles)
    @GetMapping("/erm/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_user_roles", "user_role_id", pageable));
    }

    @PostMapping("/erm/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, Object> dto) {
        long id = ermService.insertRow("erm_user_roles", dto, "user_role_id");
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.getRow("erm_user_roles", "user_role_id", id));
    }

    @GetMapping("/erm/users/{user_role_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable("user_role_id") Long id) {
        return ResponseEntity.ok(ermService.getRow("erm_user_roles", "user_role_id", id));
    }

    @PutMapping("/erm/users/{user_role_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("user_role_id") Long id, @RequestBody Map<String, Object> dto) {
        ermService.updateRow("erm_user_roles", "user_role_id", id, dto);
        return ResponseEntity.ok(ermService.getRow("erm_user_roles", "user_role_id", id));
    }

    @DeleteMapping("/erm/users/{user_role_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteUser(@PathVariable("user_role_id") Long id) {
        ermService.deleteRow("erm_user_roles", "user_role_id", id);
        return ResponseEntity.noContent().build();
    }

    // eHoldings resources
    @GetMapping("/erm/eholdings/{provider}/resources", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listResources(@PathVariable("provider") String provider, Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_eholdings_resources", "resource_id", pageable));
    }

    @GetMapping("/erm/eholdings/{provider}/packages/{package_id}/resources", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listPackageResources(
            @PathVariable("provider") String provider,
            @PathVariable("package_id") Long packageId,
            Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQueryByForeignKey("erm_eholdings_resources", "resource_id", "package_id", packageId, pageable));
    }

    @GetMapping("/erm/eholdings/{provider}/titles/{title_id}/resources", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listTitleResources(
            @PathVariable("provider") String provider,
            @PathVariable("title_id") Long titleId,
            Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQueryByForeignKey("erm_eholdings_resources", "resource_id", "title_id", titleId, pageable));
    }

    @GetMapping("/erm/eholdings/{provider}/resources/{resource_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getResource(@PathVariable("provider") String provider, @PathVariable("resource_id") Long id) {
        return ResponseEntity.ok(ermService.getRow("erm_eholdings_resources", "resource_id", id));
    }

    @PatchMapping("/erm/eholdings/{provider}/resources/{resource_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> patchResource(
            @PathVariable("provider") String provider,
            @PathVariable("resource_id") Long id,
            @RequestBody Map<String, Object> body) {
        if (body.containsKey("is_selected")) {
            body.remove("is_selected");
        }
        ermService.updateRow("erm_eholdings_resources", "resource_id", id, body);
        return ResponseEntity.ok(body);
    }

    // eHoldings titles
    @GetMapping("/erm/eholdings/{provider}/titles", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listTitles(@PathVariable("provider") String provider, Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_eholdings_titles", "title_id", pageable));
    }

    @PostMapping("/erm/eholdings/{provider}/titles", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> addTitle(@PathVariable("provider") String provider, @RequestBody Map<String, Object> dto) {
        long id = ermService.insertRow("erm_eholdings_titles", dto, "title_id");
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.getRow("erm_eholdings_titles", "title_id", id));
    }

    @GetMapping("/erm/eholdings/{provider}/titles/{title_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> getTitle(@PathVariable("provider") String provider, @PathVariable("title_id") Long id) {
        return ResponseEntity.ok(ermService.getRow("erm_eholdings_titles", "title_id", id));
    }

    @PutMapping("/erm/eholdings/{provider}/titles/{title_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> updateTitle(@PathVariable("provider") String provider, @PathVariable("title_id") Long id, @RequestBody Map<String, Object> dto) {
        ermService.updateRow("erm_eholdings_titles", "title_id", id, dto);
        return ResponseEntity.ok(ermService.getRow("erm_eholdings_titles", "title_id", id));
    }

    @DeleteMapping("/erm/eholdings/{provider}/titles/{title_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteTitle(@PathVariable("provider") String provider, @PathVariable("title_id") Long id) {
        ermService.deleteRow("erm_eholdings_titles", "title_id", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/erm/eholdings/local/titles/import", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> importTitles(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("job_id", "queued-local-title-import"));
    }

    @PostMapping("/erm/eholdings/local/titles/import_kbart", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> importTitlesFromKbart(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("job_ids", List.of("queued-kbart-import")));
    }

    // EUsage / Counter families
    @GetMapping("/erm/counter/files", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listCounterFiles(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_counter_files", "erm_counter_files_id", pageable));
    }

    @GetMapping("/erm/counter/logs", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listCounterLogs(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_counter_logs", "erm_counter_log_id", pageable));
    }

    @GetMapping("/erm/default_usage_reports", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listDefaultUsageReports(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_default_usage_reports", "erm_default_usage_report_id", pageable));
    }

    @GetMapping("/erm/usage/data_providers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listUsageDataProviders(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_usage_data_providers", "erm_usage_data_provider_id", pageable));
    }

    @GetMapping("/erm/usage/databases", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listUsageDatabases(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_usage_databases", "database_id", pageable));
    }

    @GetMapping("/erm/usage/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listUsageItems(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_usage_items", "item_id", pageable));
    }

    @GetMapping("/erm/usage/platforms", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listUsagePlatforms(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_usage_platforms", "platform_id", pageable));
    }

    @GetMapping("/erm/usage/titles", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Map<String, Object>>> listUsageTitles(Pageable pageable) {
        return ResponseEntity.ok(ermService.pageQuery("erm_usage_titles", "title_id", pageable));
    }

    @GetMapping("/erm/counter/registries", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listCounterRegistries() {
        return ResponseEntity.ok(ermService.listCounterRegistries());
    }

    @GetMapping("/erm/custom_reports", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listCustomReports() {
        return ResponseEntity.ok(ermService.listCustomReports());
    }

    @GetMapping("/erm/sushi_services", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listSushiServices() {
        return ResponseEntity.ok(ermService.listSushiServices());
    }

    @GetMapping("/erm/extended_attribute_types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> listExtendedAttributeTypes() {
        return ResponseEntity.ok(ermService.listExtendedAttributeTypes());
    }
}
