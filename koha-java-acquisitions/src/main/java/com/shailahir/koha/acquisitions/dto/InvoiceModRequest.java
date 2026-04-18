package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request body for PUT /acquisitions/invoices/{id} (op=cud-mod). */
@Data
@JacksonXmlRootElement
public class InvoiceModRequest {

    @JsonProperty("invoicenumber")
    private String invoicenumber;

    @JsonProperty("shipmentdate")
    private LocalDate shipmentdate;

    @JsonProperty("billingdate")
    private LocalDate billingdate;

    @JsonProperty("shipmentcost")
    private BigDecimal shipmentcost;

    @JsonProperty("shipmentcost_budgetid")
    private Long shipmentcostBudgetid;

    /** When true, reopen the invoice after modifying */
    @JsonProperty("reopen")
    private Boolean reopen;

    /** When true, close the invoice after modifying */
    @JsonProperty("close")
    private Boolean close;

    /** List of invoice ids to merge INTO this invoice */
    @JsonProperty("merge_sources")
    private java.util.List<Long> mergeSources;
}

