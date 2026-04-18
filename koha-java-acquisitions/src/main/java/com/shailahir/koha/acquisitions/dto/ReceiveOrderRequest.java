package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request body for POST /acquisitions/orders/{ordernumber}/receive.
 * Maps to the POST parameters of finishreceive.pl.
 */
@Data
@JacksonXmlRootElement
public class ReceiveOrderRequest {

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    /** Budget/fund to charge */
    @JsonProperty("budget_id")
    private Long budgetId;

    /** Quantity already received before this receipt */
    @JsonProperty("orig_quantity_received")
    private Integer origQuantityReceived;

    /** New total quantity received (after this receipt) */
    @JsonProperty("quantity_received")
    private Integer quantityReceived;

    /** Total ordered quantity (used when linked to a subscription) */
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unitprice")
    private BigDecimal unitprice;

    @JsonProperty("replacementprice")
    private BigDecimal replacementprice;

    @JsonProperty("datereceived")
    private String datereceived;

    @JsonProperty("tax_rate")
    private BigDecimal taxRate;

    @JsonProperty("order_internalnote")
    private String orderInternalnote;

    /** Unit price as recorded on the vendor invoice */
    @JsonProperty("invoice_unitprice")
    private BigDecimal invoiceUnitprice;

    @JsonProperty("invoice_currency")
    private String invoiceCurrency;

    /**
     * Item numbers to mark as received (when basket.create_items='ordering').
     * Mirrors items_to_receive[] CGI param.
     */
    @JsonProperty("items_to_receive")
    private List<Long> itemsToReceive;

    /**
     * Suggestion id, if this order originated from a suggestion.
     * The reason field will be saved on the suggestion.
     */
    @JsonProperty("suggestion_id")
    private Long suggestionId;

    @JsonProperty("suggestion_reason")
    private String suggestionReason;
}

