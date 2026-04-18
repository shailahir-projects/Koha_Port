package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** DTO for a single invoice adjustment (aqinvoice_adjustments row). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class InvoiceAdjustmentDto {

    @JsonProperty("adjustment_id")
    private Long adjustmentId;

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("adjustment")
    private BigDecimal adjustment;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("note")
    private String note;

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("encumber_open")
    private Boolean encumberOpen;
}

