package com.shailahir.koha.catalog.controller;

import com.shailahir.koha.catalog.dto.RecordSourceDto;
import com.shailahir.koha.catalog.service.RecordSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for record sources.
 * Maps to Swagger paths: /record_sources, /record_sources/{record_source_id}
 */
@RestController
@RequiredArgsConstructor
public class RecordSourceController {

    private final RecordSourceService recordSourceService;

    @GetMapping("/record_sources")
    public ResponseEntity<Page<RecordSourceDto>> listRecordSources(
            @RequestHeader(value = "x-koha-embed", required = false) java.util.List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(recordSourceService.listRecordSources(query, pageable));
    }

    @PostMapping("/record_sources")
    public ResponseEntity<RecordSourceDto> addRecordSource(@RequestBody RecordSourceDto recordSource) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordSourceService.addRecordSource(recordSource));
    }

    @GetMapping("/record_sources/{record_source_id}")
    public ResponseEntity<RecordSourceDto> getRecordSource(@PathVariable("record_source_id") Long recordSourceId) {
        return ResponseEntity.ok(recordSourceService.getRecordSource(recordSourceId));
    }

    @PutMapping("/record_sources/{record_source_id}")
    public ResponseEntity<RecordSourceDto> updateRecordSource(
            @PathVariable("record_source_id") Long recordSourceId,
            @RequestBody RecordSourceDto recordSource) {
        return ResponseEntity.ok(recordSourceService.updateRecordSource(recordSourceId, recordSource));
    }

    @DeleteMapping("/record_sources/{record_source_id}")
    public ResponseEntity<Void> deleteRecordSource(@PathVariable("record_source_id") Long recordSourceId) {
        recordSourceService.deleteRecordSource(recordSourceId);
        return ResponseEntity.noContent().build();
    }
}

