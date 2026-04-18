package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.InvoiceAdjustmentDto;
import com.shailahir.koha.acquisitions.dto.InvoiceOrderLineDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for aqinvoices operations.
 * Ported from invoice.pl and invoices.pl.
 */
public interface IInvoiceRepository {

    List<Map<String, Object>> searchInvoices(Long booksellerid, String invoicenumber,
            LocalDate shipmentDateFrom, LocalDate shipmentDateTo,
            LocalDate billingDateFrom, LocalDate billingDateTo,
            Boolean isCloseDate, Long budget, String basketSearch,
            int offset, int limit);

    Optional<Map<String, Object>> findInvoice(Long invoiceid);
    List<InvoiceOrderLineDto> findOrdersByInvoice(Long invoiceid);
    void closeInvoice(Long invoiceid);
    void reopenInvoice(Long invoiceid);
    void modifyInvoice(Long invoiceid, String invoicenumber, LocalDate shipmentdate,
            LocalDate billingdate, BigDecimal shipmentcost, Long shipmentcostBudgetid);
    void mergeInvoices(Long targetInvoiceid, List<Long> sourceIds);
    void deleteInvoice(Long invoiceid);

    List<InvoiceAdjustmentDto> findAdjustments(Long invoiceid);
    Optional<InvoiceAdjustmentDto> findAdjustmentById(Long adjustmentId);
    Long createAdjustment(InvoiceAdjustmentDto dto);
    void updateAdjustment(InvoiceAdjustmentDto dto);
    void deleteAdjustment(Long adjustmentId);
    boolean vendorInvoiceIncGst(Long booksellerid);
}

