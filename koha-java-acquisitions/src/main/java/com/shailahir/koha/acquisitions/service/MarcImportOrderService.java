package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.*;

import java.util.List;

/**
 * Service interface for MARC ISO2709 import → acquisition order creation.
 * Ports the three operations of addorderiso2709.pl into Java.
 */
public interface MarcImportOrderService {

    /**
     * Step 1 — List all usable import batches.
     * Mirrors Koha::MarcOrder->import_batches_list().
     */
    List<ImportBatchDto> listImportBatches();

    /**
     * Step 2 — Return detailed content of a single import batch:
     * batch metadata + list of biblio records with match status.
     * Mirrors Koha::MarcOrder->import_biblios_list($batch_id).
     *
     * @param importBatchId the batch to inspect
     * @return batch detail DTO
     */
    ImportBatchDetailDto getBatchDetail(Long importBatchId);

    /**
     * Step 3 (cud-import_records) — For each selected import record:
     * <ol>
     *   <li>Skip if not in the selected list</li>
     *   <li>Duplicate detection (ISBN / title+author match against catalogue)</li>
     *   <li>If overlay_action = 'replace' and a match exists: update existing biblio;
     *       otherwise create a new biblio</li>
     *   <li>Create an aqorders row (quantity, price, budget, notes, sort fields)</li>
     *   <li>Create item rows when basket.create_items = 'ordering'</li>
     *   <li>Mark the import_record as 'imported'</li>
     *   <li>If all records in the batch are imported: mark batch status 'imported'</li>
     * </ol>
     * Mirrors Koha::MarcOrder->import_record_and_create_order_lines() + the
     * SetImportBatchStatus() call at the end of the cud-import_records block.
     *
     * @param basketno basket to add orders to
     * @param request  user selections from the form
     * @return import result summary
     */
    MarcImportResult importAndCreateOrders(Long basketno, MarcImportOrderRequest request);
}

