package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.BudgetRepository;
import com.shailahir.koha.acquisitions.repository.MarcImportRepository;
import com.shailahir.koha.acquisitions.repository.OrderRepository;
import com.shailahir.koha.acquisitions.service.MarcImportOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Full implementation of addorderiso2709.pl business logic.
 * Ports the three workflow steps:
 *   1. list batches
 *   2. batch details
 *   3. cud-import_records → create orders
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarcImportOrderServiceImpl implements MarcImportOrderService {

    private final MarcImportRepository marcImportRepo;
    private final OrderRepository      orderRepo;
    private final BudgetRepository     budgetRepo;

    // ── Step 1: list batches ───────────────────────────────────────────────────

    @Override
    public List<ImportBatchDto> listImportBatches() {
        return marcImportRepo.findImportBatches();
    }

    // ── Step 2: batch detail ───────────────────────────────────────────────────

    @Override
    public ImportBatchDetailDto getBatchDetail(Long importBatchId) {
        ImportBatchDto batch = marcImportRepo.findBatchById(importBatchId)
                .orElseThrow(() -> new NoSuchElementException("Import batch not found: " + importBatchId));

        List<ImportBiblioDto> biblios = marcImportRepo.findBibliosByBatchId(importBatchId);

        // Enrich with duplicate detection against catalogue
        for (ImportBiblioDto b : biblios) {
            Optional<Long> match = marcImportRepo.findMatchingBiblionumber(b.getIsbn(), b.getTitle(), b.getAuthor());
            match.ifPresent(bn -> {
                b.setMatchedBiblionumber(bn);
                b.setAlreadyExists(true);
            });
        }

        // Matcher info
        String matcherCode = null;
        String matcherDesc = null;
        if (batch.getMatcherId() != null) {
            String[] info = marcImportRepo.findMatcherInfo(batch.getMatcherId()).orElse(null);
            if (info != null) {
                matcherCode = info[0];
                matcherDesc = info[1];
            }
        }

        return ImportBatchDetailDto.builder()
                .batch(batch)
                .biblios(biblios)
                .numResults(biblios.size())
                .overlayAction(batch.getOverlayAction())
                .nomatchAction(batch.getNomatchAction())
                .itemAction(batch.getItemAction())
                .currentMatcherId(batch.getMatcherId())
                .currentMatcherCode(matcherCode)
                .currentMatcherDescription(matcherDesc)
                .build();
    }

    // ── Step 3: import records and create orders ───────────────────────────────

    @Override
    @Transactional
    public MarcImportResult importAndCreateOrders(Long basketno, MarcImportOrderRequest request) {

        Long importBatchId = request.getImportBatchId();

        // Validate batch exists
        ImportBatchDto batch = marcImportRepo.findBatchById(importBatchId)
                .orElseThrow(() -> new NoSuchElementException("Import batch not found: " + importBatchId));

        // Validate basket exists
        BasketDto basket = orderRepo.findBasketById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));

        boolean createItemsOnOrdering = "ordering".equalsIgnoreCase(basket.getCreateItems());
        boolean isStanding = Boolean.TRUE.equals(basket.getIsStanding());

        // Resolve default budget_id (mirrors: $budget_id = @$budgets[0]->{budget_id})
        Long defaultBudgetId = request.getAllBudgetId() != null
                ? request.getAllBudgetId()
                : marcImportRepo.findFirstActiveBudgetId()
                        .orElseThrow(() -> new IllegalStateException("No active budget defined"));

        // Build per-record override map for O(1) lookup
        Map<Long, MarcImportOrderRequest.RecordOverride> overrideMap = request.getRecordOverrides() != null
                ? request.getRecordOverrides().stream()
                        .collect(Collectors.toMap(MarcImportOrderRequest.RecordOverride::getImportRecordId, o -> o))
                : Map.of();

        // Set of selected import_record_ids
        Set<Long> selectedIds = request.getImportRecordIdSelected() != null
                ? Set.copyOf(request.getImportRecordIdSelected())
                : Set.of();

        // Retrieve all records in the batch
        List<ImportBiblioDto> allRecords = marcImportRepo.findBibliosByBatchId(importBatchId);

        int imported = 0;
        int skipped  = 0;
        boolean duplinbatch = false;
        List<Long> createdOrdernumbers = new ArrayList<>();

        for (ImportBiblioDto record : allRecords) {
            Long importRecordId = record.getImportRecordId();

            // ── Skip if not selected ───────────────────────────────────────────
            if (!selectedIds.isEmpty() && !selectedIds.contains(importRecordId)) {
                skipped++;
                continue;
            }

            // ── Duplicate detection (mirrors FindDuplicate / matcher logic) ────
            Optional<Long> matchedBn = marcImportRepo.findMatchingBiblionumber(
                    record.getIsbn(), record.getTitle(), record.getAuthor());

            Long biblionumber;
            String overlayAction = batch.getOverlayAction();

            if (matchedBn.isPresent()) {
                duplinbatch = true;
                if ("replace".equalsIgnoreCase(overlayAction) || "create_new".equalsIgnoreCase(overlayAction)) {
                    // Use the existing biblio — no new biblio creation needed
                    biblionumber = matchedBn.get();
                    log.info("MARC import: using existing biblio {} for record {}", biblionumber, importRecordId);
                } else {
                    // 'ignore' — skip the record entirely
                    log.info("MARC import: skipping record {} (duplicate, overlay_action={})", importRecordId, overlayAction);
                    skipped++;
                    continue;
                }
            } else {
                // No match — create a new biblio
                biblionumber = budgetRepo.insertBiblio(
                        record.getTitle(),
                        record.getAuthor(),
                        record.getIsbn(),
                        record.getIssn(),           // ean field repurposed for issn
                        record.getPublishercode(),
                        record.getPublicationyear(),
                        null, null,                 // itemtype / editionstatement
                        null);                      // series
                log.info("MARC import: created new biblio {} for record {}", biblionumber, importRecordId);
            }

            // ── Per-record overrides ───────────────────────────────────────────
            MarcImportOrderRequest.RecordOverride ov = overrideMap.get(importRecordId);

            Long budgetId = ov != null && ov.getBudgetId() != null ? ov.getBudgetId() : defaultBudgetId;
            Integer quantity = ov != null && ov.getQuantity() != null ? ov.getQuantity()
                    : (record.getQuantity() != null ? record.getQuantity() : 1);
            BigDecimal price = ov != null && ov.getPrice() != null ? ov.getPrice()
                    : (record.getPrice() != null ? record.getPrice() : BigDecimal.ZERO);
            BigDecimal replacementPrice = ov != null ? ov.getReplacementPrice() : null;
            BigDecimal discount = ov != null ? ov.getDiscount() : null;
            String sort1 = ov != null && ov.getSort1() != null ? ov.getSort1()
                    : (request.getAllSort1() != null ? request.getAllSort1() : "");
            String sort2 = ov != null && ov.getSort2() != null ? ov.getSort2()
                    : (request.getAllSort2() != null ? request.getAllSort2() : "");

            // ── Build and persist order ────────────────────────────────────────
            OrderDto order = OrderDto.builder()
                    .basketno(basketno)
                    .biblionumber(biblionumber)
                    .budgetId(budgetId)
                    .quantity(quantity)
                    .currency(request.getAllCurrency())
                    .listprice(price)
                    .ecost(price)
                    .unitprice(price)
                    .rrp(price)
                    .replacementprice(replacementPrice)
                    .discount(discount)
                    .uncertainprice(price == null || price.compareTo(BigDecimal.ZERO) == 0)
                    .orderInternalnote(request.getAllOrderInternalnote())
                    .orderVendornote(request.getAllOrderVendornote())
                    .sort1(sort1)
                    .sort2(sort2)
                    .build();

            // Derive tax-split fields with 0% tax (tax rate is set later on receive)
            derivePrices(order);

            Long ordernumber = orderRepo.insert(order);
            order.setOrdernumber(ordernumber);
            createdOrdernumbers.add(ordernumber);

            // ── Item creation (basket.create_items = 'ordering') ───────────────
            if (createItemsOnOrdering && !isStanding && ov != null && ov.getItems() != null) {
                Optional<Long> biblioitemnumber = budgetRepo.getBiblioitemnumber(biblionumber);
                Long bib = biblionumber;
                for (MarcImportOrderRequest.ItemOverride item : ov.getItems()) {
                    Long itemnumber = budgetRepo.insertItem(
                            bib,
                            biblioitemnumber.orElse(null),
                            item.getBarcode(),
                            item.getHomebranch(),
                            item.getHoldingbranch(),
                            item.getItype(),
                            item.getLocation(),
                            item.getReplacementprice(),
                            item.getItemcallnumber());
                    orderRepo.linkItemToOrder(ordernumber, itemnumber);
                }
            }

            // ── Mark import record as imported ─────────────────────────────────
            marcImportRepo.markRecordImported(importRecordId);
            imported++;
        }

        // ── Mark batch as 'imported' if all records are now imported ───────────
        // Mirrors: SetImportBatchStatus($import_batch_id, 'imported') if count matches
        int totalRecords    = marcImportRepo.countAllRecords(importBatchId);
        int importedRecords = marcImportRepo.countRecordsByStatus(importBatchId, "imported");
        boolean batchFullyImported = totalRecords > 0 && importedRecords == totalRecords;
        if (batchFullyImported) {
            marcImportRepo.updateBatchStatus(importBatchId, "imported");
            log.info("MARC import: batch {} fully imported, status set to 'imported'", importBatchId);
        }

        return MarcImportResult.builder()
                .imported(imported)
                .skipped(skipped)
                .duplicatesInBatch(duplinbatch)
                .batchFullyImported(batchFullyImported)
                .createdOrdernumbers(createdOrdernumbers)
                .build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Populate tax-split price fields on the order DTO assuming 0% tax on ordering
     * (tax is applied later on receive). Mirrors populate_with_prices_for_ordering.
     */
    private void derivePrices(OrderDto order) {
        BigDecimal price = order.getEcost() != null ? order.getEcost() : BigDecimal.ZERO;
        order.setEcostTaxExcluded(price);
        order.setEcostTaxIncluded(price);
        order.setRrpTaxExcluded(order.getRrp() != null ? order.getRrp() : price);
        order.setRrpTaxIncluded(order.getRrp() != null ? order.getRrp() : price);
        BigDecimal up = order.getUnitprice() != null ? order.getUnitprice() : price;
        order.setUnitprice(up);
        order.setUnitpriceTaxExcluded(up);
        order.setUnitpriceTaxIncluded(up);
    }
}

