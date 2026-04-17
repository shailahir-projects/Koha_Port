package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Enriched order line for the basket detail view.
 * Mirrors the hash built by get_order_infos() in basket.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketOrderLineDto {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("isbn")
    private String isbn;

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

    @JsonProperty("sort1_authcat")
    private String sort1Authcat;

    @JsonProperty("sort2_authcat")
    private String sort2Authcat;

    @JsonProperty("listprice")
    private BigDecimal listprice;

    @JsonProperty("ecost_tax_excluded")
    private BigDecimal ecostTaxExcluded;

    @JsonProperty("ecost_tax_included")
    private BigDecimal ecostTaxIncluded;

    @JsonProperty("unitprice_tax_excluded")
    private BigDecimal unitpriceTaxExcluded;

    @JsonProperty("unitprice_tax_included")
    private BigDecimal unitpriceTaxIncluded;

    @JsonProperty("total_tax_excluded")
    private BigDecimal totalTaxExcluded;

    @JsonProperty("total_tax_included")
    private BigDecimal totalTaxIncluded;

    @JsonProperty("tax_rate")
    private BigDecimal taxRate;

    @JsonProperty("tax_value")
    private BigDecimal taxValue;

    @JsonProperty("uncertainprice")
    private Boolean uncertainprice;

    @JsonProperty("orderstatus")
    private String orderstatus;

    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    // ── Biblio-level flags (mirrors get_order_infos) ──

    @JsonProperty("can_del_bib")
    private Boolean canDelBib;

    /** Number of items on this biblio NOT linked to this order */
    @JsonProperty("items_elsewhere")
    private Integer itemsElsewhere;

    @JsonProperty("left_item")
    private Boolean leftItem;

    @JsonProperty("left_biblio")
    private Boolean leftBiblio;

    @JsonProperty("biblios_count")
    private Integer bibliosCount;

    @JsonProperty("left_subscription")
    private Boolean leftSubscription;

    @JsonProperty("subscriptions_count")
    private Integer subscriptionsCount;

    @JsonProperty("left_holds")
    private Boolean leftHolds;

    @JsonProperty("holds_count")
    private Integer holdsCount;

    @JsonProperty("left_holds_on_order")
    private Boolean leftHoldsOnOrder;

    @JsonProperty("holds_on_order")
    private Integer holdsOnOrder;

    /** True when the biblio record has been deleted from the catalogue */
    @JsonProperty("deleted_biblio")
    private Boolean deletedBiblio;

    @JsonProperty("suggestion_id")
    private Long suggestionId;

    @JsonProperty("invoiceid")
    private Long invoiceid;
}

