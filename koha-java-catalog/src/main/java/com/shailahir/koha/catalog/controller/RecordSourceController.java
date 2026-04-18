package com.shailahir.koha.catalog.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.catalog.dto.RecordSourceDto;
import com.shailahir.koha.catalog.service.RecordSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

/**
 * REST controller for record sources.
 * Maps to Swagger paths: /record_sources, /record_sources/{record_source_id}
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RecordSourceController {

    private final RecordSourceService recordSourceService;

    @GetMapping("/record_sources", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<RecordSourceDto>> listRecordSources(
            @RequestHeader(value = "x-koha-embed", required = false) java.util.List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        log.debug("Entering listRecordSources - {}, {}, {}", embed, query, pageable);
        return ResponseEntity.ok(recordSourceService.listRecordSources(query, pageable));
    }

    @PostMapping("/record_sources", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RecordSourceDto> addRecordSource(@RequestBody RecordSourceDto recordSource) {
        log.debug("Entering addRecordSource - {}", recordSource);
        return ResponseEntity.status(HttpStatus.CREATED).body(recordSourceService.addRecordSource(recordSource));
    }

    @GetMapping("/record_sources/{record_source_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RecordSourceDto> getRecordSource(@PathVariable("record_source_id") Long recordSourceId) {
        log.debug("Entering getRecordSource - {}", recordSourceId);
        return ResponseEntity.ok(recordSourceService.getRecordSource(recordSourceId));
    }

    @PutMapping("/record_sources/{record_source_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RecordSourceDto> updateRecordSource(
            @PathVariable("record_source_id") Long recordSourceId,
            @RequestBody RecordSourceDto recordSource) {
        log.debug("Entering updateRecordSource - {}, {}", recordSourceId, recordSource);
        return ResponseEntity.ok(recordSourceService.updateRecordSource(recordSourceId, recordSource));
    }

    @DeleteMapping("/record_sources/{record_source_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteRecordSource(@PathVariable("record_source_id") Long recordSourceId) {
        log.debug("Entering deleteRecordSource - {}", recordSourceId);
        recordSourceService.deleteRecordSource(recordSourceId);
        return ResponseEntity.noContent().build();
    }
}

