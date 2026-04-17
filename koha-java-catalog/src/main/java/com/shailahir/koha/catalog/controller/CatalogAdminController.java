package com.shailahir.koha.catalog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Catalog admin controller covering:
 * - /import_batches/{id}/records/{record_id}/matches/chosen (PUT, DELETE)
 * - /import_batch_profiles (GET, POST)
 * - /import_batch_profiles/{id} (PUT, DELETE)
 * - /item_types (GET)
 */
@RestController
public class CatalogAdminController {

    // ── import_batches ──
    @PutMapping("/import_batches/{import_batch_id}/records/{import_record_id}/matches/chosen")
    public ResponseEntity<Map<String, Object>> setChosen(
            @PathVariable("import_batch_id") Long batchId,
            @PathVariable("import_record_id") Long recordId,
            @RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of());
    }

    @DeleteMapping("/import_batches/{import_batch_id}/records/{import_record_id}/matches/chosen")
    public ResponseEntity<Void> unsetChosen(
            @PathVariable("import_batch_id") Long batchId,
            @PathVariable("import_record_id") Long recordId) {
        return ResponseEntity.noContent().build();
    }

    // ── import_batch_profiles ──
    @GetMapping("/import_batch_profiles")
    public ResponseEntity<Page<Map<String, Object>>> listImportBatchProfiles(Pageable pageable) {
        return ResponseEntity.ok(new PageImpl<>(List.of()));
    }

    @PostMapping("/import_batch_profiles")
    public ResponseEntity<Map<String, Object>> addImportBatchProfile(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of());
    }

    @PutMapping("/import_batch_profiles/{import_batch_profile_id}")
    public ResponseEntity<Map<String, Object>> editImportBatchProfile(
            @PathVariable("import_batch_profile_id") Long id,
            @RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(Map.of());
    }

    @DeleteMapping("/import_batch_profiles/{import_batch_profile_id}")
    public ResponseEntity<Void> deleteImportBatchProfile(@PathVariable("import_batch_profile_id") Long id) {
        return ResponseEntity.noContent().build();
    }

    // ── item_types ──
    @GetMapping("/item_types")
    public ResponseEntity<List<Map<String, Object>>> listItemTypes(Pageable pageable) {
        return ResponseEntity.ok(List.of());
    }
}
