package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Result of a finish-receive operation.
 * Mirrors the state after ModReceiveOrder() completes in finishreceive.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class ReceiveOrderResult {

    @JsonProperty("original_ordernumber")
    private Long originalOrdernumber;

    /**
     * The ordernumber of the received portion.
     * When partial receipt causes an order split, this is the NEW ordernumber;
     * when the full quantity is received it equals original_ordernumber.
     */
    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("datereceived")
    private String datereceived;

    @JsonProperty("quantity_received")
    private Integer quantityReceived;

    @JsonProperty("unitprice_tax_excluded")
    private BigDecimal unitpriceTaxExcluded;

    @JsonProperty("unitprice_tax_included")
    private BigDecimal unitpriceTaxIncluded;

    @JsonProperty("tax_value_on_receiving")
    private BigDecimal taxValueOnReceiving;
}

