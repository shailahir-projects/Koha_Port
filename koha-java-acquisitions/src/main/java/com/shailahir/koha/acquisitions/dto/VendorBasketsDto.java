package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for GET /acquisitions/vendors/{id}/baskets.
 * Mirrors the template variables assembled in booksellers.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBasketsDto {

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("vendor_type")
    private String vendorType;

    @JsonProperty("basketcount")
    private Integer basketcount;

    @JsonProperty("subscriptioncount")
    private Integer subscriptioncount;

    /** Enriched basket list (filtered to baskets the user can manage) */
    @JsonProperty("baskets")
    private List<BasketInfoDto> baskets;

    /** Whether the logged-in user has at least one usable budget */
    @JsonProperty("has_budgets")
    private Boolean hasBudgets;
}

