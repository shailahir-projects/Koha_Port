package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for a basket (aqbasket row).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto {

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("authorisedby")
    private Long authorisedby;

    @JsonProperty("is_standing")
    private Boolean isStanding;

    @JsonProperty("create_items")
    private String createItems;

    @JsonProperty("closedate")
    private String closedate;

    @JsonProperty("note")
    private String note;

    @JsonProperty("contractnumber")
    private Long contractnumber;
}

