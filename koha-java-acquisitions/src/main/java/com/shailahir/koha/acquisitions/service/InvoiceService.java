package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.InvoiceAdjustmentDto;
import com.shailahir.koha.acquisitions.dto.InvoiceDetailDto;
import com.shailahir.koha.acquisitions.dto.InvoiceModRequest;
import com.shailahir.koha.acquisitions.dto.InvoiceOrderLineDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business logic for invoice lifecycle management.
 * Ports invoice.pl and invoices.pl.
 */
public interface InvoiceService {

    List<Map<String, Object>> searchInvoices(Long booksellerid, String invoicenumber,
            LocalDate shipmentDateFrom, LocalDate shipmentDateTo,
            LocalDate billingDateFrom, LocalDate billingDateTo,
            Boolean isClosed, Long budget, String basketSearch,
            int page, int size);

    Optional<InvoiceDetailDto> getInvoice(Long invoiceid);
    List<InvoiceOrderLineDto> getOrdersByInvoice(Long invoiceid);

    void closeInvoice(Long invoiceid);
    void reopenInvoice(Long invoiceid);
    void modifyInvoice(Long invoiceid, InvoiceModRequest req);
    void mergeInvoices(Long targetInvoiceid, List<Long> sourceIds);
    void deleteInvoice(Long invoiceid);

    // Adjustments
    List<InvoiceAdjustmentDto> getAdjustments(Long invoiceid);
    Optional<InvoiceAdjustmentDto> getAdjustment(Long adjustmentId);
    Long createAdjustment(InvoiceAdjustmentDto dto);
    void updateAdjustment(InvoiceAdjustmentDto dto);
    void deleteAdjustment(Long adjustmentId);

    // Invoice files
    List<Map<String, Object>> getFiles(Long invoiceid);
    Optional<Map<String, Object>> getFile(Long fileId, Long invoiceid);
    Long addFile(Long invoiceid, String fileName, String fileType,
            String description, byte[] content, Long uploadedBy);
    void deleteFile(Long fileId, Long invoiceid);
}

