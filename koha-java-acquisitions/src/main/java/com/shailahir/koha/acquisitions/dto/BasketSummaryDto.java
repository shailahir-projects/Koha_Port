package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Lightweight basket summary used inside BasketGroupDto.
 * Mirrors the basket hash used in displaybasketgroups / BasketTotal in basketgroup.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketSummaryDto {

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("basketgroupid")
    private Long basketgroupid;

    @JsonProperty("closedate")
    private String closedate;

    @JsonProperty("authorisedby")
    private Long authorisedby;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("is_standing")
    private Boolean isStanding;

    /** Order total for this basket (sum of ecost * qty, mirrors BasketTotal()) */
    @JsonProperty("total")
    private BigDecimal total;
}

