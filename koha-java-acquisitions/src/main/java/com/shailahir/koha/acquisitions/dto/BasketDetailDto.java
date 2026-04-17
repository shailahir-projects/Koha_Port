package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Full basket detail view — equivalent to the data assembled in basket.pl op=list.
 * Contains the basket metadata, all active orders with per-line enrichment,
 * cancelled orders, and footer totals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDetailDto {

    @JsonProperty("basket")
    private BasketDto basket;

    /** Active (non-cancelled) order lines enriched with biblio/budget info */
    @JsonProperty("orders")
    private List<BasketOrderLineDto> orders;

    /** Cancelled order lines */
    @JsonProperty("cancelled_orders")
    private List<BasketOrderLineDto> cancelledOrders;

    /** Per-tax-rate footer totals */
    @JsonProperty("foot")
    private List<TaxFootDto> foot;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("total_tax_excluded")
    private BigDecimal totalTaxExcluded;

    @JsonProperty("total_tax_included")
    private BigDecimal totalTaxIncluded;

    @JsonProperty("total_tax_value")
    private BigDecimal totalTaxValue;

    @JsonProperty("currency")
    private String currency;

    /** Indicates duplicate records were found in the last ISO2709 import for this basket */
    @JsonProperty("duplinbatch")
    private String duplinbatch;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxFootDto {
        @JsonProperty("tax_rate")
        private BigDecimal taxRate;
        @JsonProperty("tax_value")
        private BigDecimal taxValue;
        @JsonProperty("quantity")
        private Integer quantity;
        @JsonProperty("total_tax_excluded")
        private BigDecimal totalTaxExcluded;
        @JsonProperty("total_tax_included")
        private BigDecimal totalTaxIncluded;
    }
}

