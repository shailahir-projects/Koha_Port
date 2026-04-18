package com.shailahir.koha.catalog.controller;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import com.shailahir.koha.catalog.service.ImportBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/import_batches")
@RequiredArgsConstructor
public class ImportBatchController {
    private final ImportBatchService importBatchService;
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ImportBatchDto>> listImportBatches(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(importBatchService.listImportBatches(query, pageable));
    }
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ImportBatchDto> getImportBatch(@PathVariable Long id) {
        return ResponseEntity.ok(importBatchService.getImportBatch(id));
    }
}
