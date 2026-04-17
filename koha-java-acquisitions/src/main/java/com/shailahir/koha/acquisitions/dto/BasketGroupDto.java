package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for a basket group (aqbasketgroups row).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketGroupDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("deliveryplace")
    private String deliveryplace;

    @JsonProperty("freedeliveryplace")
    private String freedeliveryplace;

    @JsonProperty("deliverycomment")
    private String deliverycomment;

    @JsonProperty("billingplace")
    private String billingplace;

    @JsonProperty("closed")
    private Boolean closed;

    /** Baskets that belong to this group (populated on detail queries) */
    @JsonProperty("baskets")
    private List<BasketSummaryDto> baskets;

    @JsonProperty("basketsqty")
    private Integer basketsqty;
}

