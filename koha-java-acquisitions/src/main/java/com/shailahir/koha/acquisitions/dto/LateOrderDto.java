package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single late order row.
 * Mirrors the data from Koha::Acquisition::Orders->filter_by_lates().
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LateOrderDto {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

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

    @JsonProperty("ecost_tax_included")
    private BigDecimal ecostTaxIncluded;

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("budget_name")
    private String budgetName;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("vendor_name")
    private String vendorName;

    /** Date the basket was closed */
    @JsonProperty("closedate")
    private LocalDate closedate;

    /** Estimated delivery date set on the order */
    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    /** Number of days late (today - closedate or estimated_delivery_date) */
    @JsonProperty("days_late")
    private Long daysLate;

    /** Date of last claim alert, if any */
    @JsonProperty("claimed_date")
    private LocalDate claimedDate;

    /** Number of times the order has been claimed */
    @JsonProperty("claims_count")
    private Integer claimsCount;
}

