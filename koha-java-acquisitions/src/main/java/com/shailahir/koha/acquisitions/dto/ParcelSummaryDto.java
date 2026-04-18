package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Summary row for parcels.pl list. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class ParcelSummaryDto {

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("invoicenumber")
    private String invoicenumber;

    @JsonProperty("shipmentdate")
    private LocalDate shipmentdate;

    @JsonProperty("billingdate")
    private LocalDate billingdate;

    @JsonProperty("closedate")
    private LocalDate closedate;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("total_received")
    private Integer totalReceived;

    @JsonProperty("total_ecost")
    private BigDecimal totalEcost;
}

