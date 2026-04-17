package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Enriched basket info for the vendor baskets page.
 * Mirrors the hash built by GetBasketsInfosByBookseller() in C4::Acquisition,
 * including the per-basket item/biblio counts and basket group linkage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketInfoDto {

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("basketgroupid")
    private Long basketgroupid;

    @JsonProperty("basketgroupname")
    private String basketgroupname;

    @JsonProperty("authorisedby")
    private Long authorisedby;

    @JsonProperty("authorisedbyname")
    private String authorisedbyname;

    @JsonProperty("closedate")
    private String closedate;

    @JsonProperty("creationdate")
    private String creationdate;

    @JsonProperty("is_standing")
    private Boolean isStanding;

    @JsonProperty("branch")
    private String branch;

    /** Total number of items ordered across all orders in this basket */
    @JsonProperty("total_items")
    private Integer totalItems;

    /** Total number of distinct biblios in this basket */
    @JsonProperty("total_biblios")
    private Integer totalBiblios;

    /** Items not yet received (quantity - quantityreceived) */
    @JsonProperty("expected_items")
    private Integer expectedItems;

    /** Total cost (ecost * quantity) for display */
    @JsonProperty("total_cost")
    private BigDecimal totalCost;
}

