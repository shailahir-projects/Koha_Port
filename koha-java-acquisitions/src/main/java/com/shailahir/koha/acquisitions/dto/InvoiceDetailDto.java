package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Full invoice detail DTO — mirrors GetInvoiceDetails() output.
 * Used by GET /acquisitions/invoices/{id}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class InvoiceDetailDto {

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("invoicenumber")
    private String invoicenumber;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("suppliername")
    private String suppliername;

    @JsonProperty("shipmentdate")
    private LocalDate shipmentdate;

    @JsonProperty("billingdate")
    private LocalDate billingdate;

    @JsonProperty("closedate")
    private LocalDate closedate;

    @JsonProperty("shipmentcost")
    private BigDecimal shipmentcost;

    @JsonProperty("shipmentcost_budgetid")
    private Long shipmentcostBudgetid;

    @JsonProperty("message_id")
    private Long messageId;

    /** Enriched order lines (mirrors orders_loop in invoice.pl) */
    @JsonProperty("orders")
    private List<InvoiceOrderLineDto> orders;

    /** Per-tax-rate footer totals */
    @JsonProperty("foot_loop")
    private List<java.util.Map<String, Object>> footLoop;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("total_tax_excluded")
    private BigDecimal totalTaxExcluded;

    @JsonProperty("total_tax_included")
    private BigDecimal totalTaxIncluded;

    @JsonProperty("total_tax_value")
    private BigDecimal totalTaxValue;

    @JsonProperty("total_tax_excluded_shipment")
    private BigDecimal totalTaxExcludedShipment;

    @JsonProperty("total_tax_included_shipment")
    private BigDecimal totalTaxIncludedShipment;

    @JsonProperty("invoiceincgst")
    private Boolean invoiceincgst;
}

