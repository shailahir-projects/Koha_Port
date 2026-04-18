package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.InvoiceFilesRepository;
import com.shailahir.koha.acquisitions.repository.InvoiceRepository;
import com.shailahir.koha.acquisitions.service.InvoiceService;
import com.shailahir.koha.acquisitions.transformer.InvoiceTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link InvoiceService}.
 * Ports invoice.pl, invoices.pl, invoice-files.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final InvoiceFilesRepository filesRepo;
    private final InvoiceTransformer transformer;

    @Override
    public List<Map<String, Object>> searchInvoices(Long booksellerid, String invoicenumber,
            LocalDate shipmentDateFrom, LocalDate shipmentDateTo,
            LocalDate billingDateFrom, LocalDate billingDateTo,
            Boolean isClosed, Long budget, String basketSearch,
            int page, int size) {
        log.debug("Searching invoices booksellerid={}", booksellerid);
        // Delegate to repository with its actual signature
        return invoiceRepo.searchInvoices(
                invoicenumber, booksellerid,
                shipmentDateFrom, shipmentDateTo,
                billingDateFrom, billingDateTo,
                null, null, null, null, null,
                null, budget, null);
    }

    @Override
    public Optional<InvoiceDetailDto> getInvoice(Long invoiceid) {
        log.debug("Entering getInvoice - {}", invoiceid);
        return invoiceRepo.findInvoice(invoiceid).map(transformer::fromRow);
    }

    @Override
    public List<InvoiceOrderLineDto> getOrdersByInvoice(Long invoiceid) {
        log.debug("Entering getOrdersByInvoice - {}", invoiceid);
        return invoiceRepo.findOrdersByInvoice(invoiceid);
    }

    @Override
    @Transactional
    public void closeInvoice(Long invoiceid) {
        log.info("Closing invoice {}", invoiceid);
        invoiceRepo.closeInvoice(invoiceid);
        invoiceRepo.logAction("CLOSE_INVOICE", invoiceid, "Closed by user");
    }

    @Override
    @Transactional
    public void reopenInvoice(Long invoiceid) {
        log.info("Reopening invoice {}", invoiceid);
        invoiceRepo.reopenInvoice(invoiceid);
        invoiceRepo.logAction("REOPEN_INVOICE", invoiceid, "Reopened by user");
    }

    @Override
    @Transactional
    public void modifyInvoice(Long invoiceid, InvoiceModRequest req) {
        log.info("Modifying invoice {}", invoiceid);
        invoiceRepo.modifyInvoice(invoiceid, req.getInvoicenumber(),
                req.getShipmentdate(), req.getBillingdate(),
                req.getShipmentcost(), req.getShipmentcostBudgetid());
        invoiceRepo.logAction("MODIFY_INVOICE", invoiceid, "Modified by user");
    }

    @Override
    @Transactional
    public void mergeInvoices(Long targetInvoiceid, List<Long> sourceIds) {
        log.info("Merging invoices {} into {}", sourceIds, targetInvoiceid);
        invoiceRepo.mergeInvoices(targetInvoiceid, sourceIds);
        invoiceRepo.logAction("MERGE_INVOICE", targetInvoiceid, "Merged from: " + sourceIds);
    }

    @Override
    @Transactional
    public void deleteInvoice(Long invoiceid) {
        log.info("Deleting invoice {}", invoiceid);
        invoiceRepo.deleteInvoice(invoiceid);
    }

    @Override
    public List<InvoiceAdjustmentDto> getAdjustments(Long invoiceid) {
        log.debug("Entering getAdjustments - {}", invoiceid);
        return invoiceRepo.findAdjustments(invoiceid);
    }

    @Override
    public Optional<InvoiceAdjustmentDto> getAdjustment(Long adjustmentId) {
        log.debug("Entering getAdjustment - {}", adjustmentId);
        return invoiceRepo.findAdjustmentById(adjustmentId);
    }

    @Override
    @Transactional
    public Long createAdjustment(InvoiceAdjustmentDto dto) {
        log.debug("Entering createAdjustment - {}", dto);
        return invoiceRepo.createAdjustment(dto);
    }

    @Override
    @Transactional
    public void updateAdjustment(InvoiceAdjustmentDto dto) {
        log.debug("Entering updateAdjustment - {}", dto);
        invoiceRepo.updateAdjustment(dto);
    }

    @Override
    @Transactional
    public void deleteAdjustment(Long adjustmentId) {
        log.debug("Entering deleteAdjustment - {}", adjustmentId);
        invoiceRepo.deleteAdjustment(adjustmentId);
    }

    @Override
    public List<Map<String, Object>> getFiles(Long invoiceid) {
        log.debug("Entering getFiles - {}", invoiceid);
        return filesRepo.getFilesInfo(invoiceid);
    }

    @Override
    public Optional<Map<String, Object>> getFile(Long fileId, Long invoiceid) {
        log.debug("Entering getFile - {}, {}", fileId, invoiceid);
        return filesRepo.getFile(fileId, invoiceid);
    }

    @Override
    @Transactional
    public Long addFile(Long invoiceid, String fileName, String fileType,
            String description, byte[] content, Long uploadedBy) {
        log.debug("Entering addFile - {}, {}, {}, {}, {}, {}", invoiceid, fileName, fileType, description, content, uploadedBy);
        // InvoiceFilesRepository.addFile(invoiceid, fileName, fileType, content, description)
        return filesRepo.addFile(invoiceid, fileName, fileType, content, description);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId, Long invoiceid) {
        log.debug("Entering deleteFile - {}, {}", fileId, invoiceid);
        filesRepo.deleteFile(fileId, invoiceid);
    }
}
