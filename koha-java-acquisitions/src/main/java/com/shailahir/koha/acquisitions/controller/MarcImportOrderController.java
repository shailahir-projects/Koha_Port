package com.shailahir.koha.acquisitions.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.service.MarcImportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * REST controller for MARC ISO2709 import → acquisition order workflow.
 * Ports all three steps of addorderiso2709.pl.
 *
 * <pre>
 *  Step 1  GET  /acquisitions/marc-import/batches
 *  Step 2  GET  /acquisitions/marc-import/batches/{batch_id}
 *  Step 3  POST /acquisitions/marc-import/baskets/{basketno}/import
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/acquisitions/marc-import")
@RequiredArgsConstructor
public class MarcImportOrderController {

    private final MarcImportOrderService marcImportOrderService;

    /**
     * Step 1 — List all usable import batches.
     * <p>
     * Mirrors the first screen of addorderiso2709.pl where the librarian
     * selects which ISO2709 file / import batch to pull records from.
     *
     * @return list of batches in status 'staged' (not yet fully imported)
     */
    @GetMapping("/batches", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ImportBatchDto>> listImportBatches() {
        log.debug("Entering listImportBatches");
        return ResponseEntity.ok(marcImportOrderService.listImportBatches());
    }

    /**
     * Step 2 — Get batch details including all biblio records.
     * <p>
     * Mirrors the second screen (op=batch_details) of addorderiso2709.pl.
     * Each biblio record is enriched with a duplicate-detection flag
     * ({@code already_exists} + {@code matched_biblionumber}).
     *
     * @param batchId the import_batch_id
     * @return batch metadata and list of biblio records
     */
    @GetMapping("/batches/{batch_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ImportBatchDetailDto> getBatchDetail(@PathVariable("batch_id") Long batchId) {
        log.debug("Entering getBatchDetail - {}", batchId);
        return ResponseEntity.ok(marcImportOrderService.getBatchDetail(batchId));
    }

    /**
     * Step 3 — Import selected records and create acquisition orders.
     * <p>
     * Mirrors {@code op=cud-import_records} in addorderiso2709.pl.
     * For every selected import record:
     * <ul>
     *   <li>Performs duplicate detection</li>
     *   <li>Creates or reuses a biblio based on the batch's overlay_action</li>
     *   <li>Creates an aqorders row with the supplied pricing / budget / note data</li>
     *   <li>Creates items when {@code basket.create_items = 'ordering'}</li>
     *   <li>Marks the import_record as 'imported'</li>
     * </ul>
     * When all records in the batch are imported the batch itself is set to 'imported'.
     *
     * @param basketno target basket number
     * @param request  user form data (selected record IDs, per-record overrides, defaults)
     * @return import result summary
     */
    @PostMapping("/baskets/{basketno}/import", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<MarcImportResult> importAndCreateOrders(
            @PathVariable("basketno") Long basketno,
            @RequestBody MarcImportOrderRequest request) {
        log.debug("Entering importAndCreateOrders - {}, {}", basketno, request);
        MarcImportResult result = marcImportOrderService.importAndCreateOrders(basketno, request);
        return ResponseEntity.ok(result);
    }
}

