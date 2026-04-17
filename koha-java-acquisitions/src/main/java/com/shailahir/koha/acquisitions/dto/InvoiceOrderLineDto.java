package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Single order line within an invoice detail response.
 * Mirrors the hash built by get_infos() in invoice.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceOrderLineDto {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("parent_ordernumber")
    private Long parentOrdernumber;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("quantityreceived")
    private Integer quantityreceived;

    @JsonProperty("order_received")
    private Boolean orderReceived;

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("budget_name")
    private String budgetName;

    @JsonProperty("unitprice_tax_excluded")
    private BigDecimal unitpriceTaxExcluded;

    @JsonProperty("unitprice_tax_included")
    private BigDecimal unitpriceTaxIncluded;

    @JsonProperty("tax_rate_on_receiving")
    private BigDecimal taxRateOnReceiving;

    @JsonProperty("tax_value_on_receiving")
    private BigDecimal taxValueOnReceiving;

    @JsonProperty("total_tax_excluded")
    private BigDecimal totalTaxExcluded;

    @JsonProperty("total_tax_included")
    private BigDecimal totalTaxIncluded;

    @JsonProperty("invoice_unitprice")
    private BigDecimal invoiceUnitprice;

    @JsonProperty("invoice_currency")
    private String invoiceCurrency;

    @JsonProperty("datereceived")
    private LocalDate datereceived;

    @JsonProperty("orderstatus")
    private String orderstatus;
}

