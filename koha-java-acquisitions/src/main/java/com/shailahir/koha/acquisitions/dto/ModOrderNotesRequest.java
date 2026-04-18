package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Request DTO for modordernotes.pl – update internal/vendor notes on an order.
 */
@Data
@JacksonXmlRootElement
public class ModOrderNotesRequest {

    @JsonProperty("order_internalnote")
    private String orderInternalnote;

    @JsonProperty("order_vendornote")
    private String orderVendornote;
}

