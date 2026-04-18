package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO representing a single acquisition order row (aqorders).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class OrderDto {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("quantityreceived")
    private Integer quantityreceived;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("listprice")
    private BigDecimal listprice;

    @JsonProperty("uncertainprice")
    private Boolean uncertainprice;

    @JsonProperty("tax_rate_on_ordering")
    private BigDecimal taxRateOnOrdering;

    @JsonProperty("tax_rate_on_receiving")
    private BigDecimal taxRateOnReceiving;

    @JsonProperty("discount")
    private BigDecimal discount;

    @JsonProperty("rrp")
    private BigDecimal rrp;

    @JsonProperty("rrp_tax_excluded")
    private BigDecimal rrpTaxExcluded;

    @JsonProperty("rrp_tax_included")
    private BigDecimal rrpTaxIncluded;

    @JsonProperty("ecost")
    private BigDecimal ecost;

    @JsonProperty("ecost_tax_excluded")
    private BigDecimal ecostTaxExcluded;

    @JsonProperty("ecost_tax_included")
    private BigDecimal ecostTaxIncluded;

    @JsonProperty("unitprice")
    private BigDecimal unitprice;

    @JsonProperty("unitprice_tax_excluded")
    private BigDecimal unitpriceTaxExcluded;

    @JsonProperty("unitprice_tax_included")
    private BigDecimal unitpriceTaxIncluded;

    @JsonProperty("replacementprice")
    private BigDecimal replacementprice;

    @JsonProperty("order_internalnote")
    private String orderInternalnote;

    @JsonProperty("order_vendornote")
    private String orderVendornote;

    @JsonProperty("sort1")
    private String sort1;

    @JsonProperty("sort2")
    private String sort2;

    @JsonProperty("subscriptionid")
    private Long subscriptionid;

    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @JsonProperty("datecreated")
    private LocalDateTime datecreated;

    @JsonProperty("datereceived")
    private LocalDate datereceived;

    @JsonProperty("orderstatus")
    private String orderstatus;

    @JsonProperty("basketname")
    private String basketname;
}

