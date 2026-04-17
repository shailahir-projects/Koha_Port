package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Filter parameters for order history search.
 * Mirrors the %filters hash built in duplicate_orders.pl and passed to GetHistory().
 */
@Data
public class OrderHistoryFilter {

    @JsonProperty("basket")
    private String basket;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ean")
    private String ean;

    @JsonProperty("basketgroupname")
    private String basketgroupname;

    @JsonProperty("booksellerinvoicenumber")
    private String booksellerinvoicenumber;

    @JsonProperty("budget")
    private String budget;

    /**
     * Filter by order status. Use "any" to include cancelled orders.
     * Mirrors orderstatus CGI param (and get_canceled_order logic).
     */
    @JsonProperty("orderstatus")
    private String orderstatus;

    @JsonProperty("ordernumber")
    private String ordernumber;

    @JsonProperty("search_children_too")
    private Boolean searchChildrenToo;

    /** Patron borrowernumbers that created the orders */
    @JsonProperty("created_by")
    private List<Long> createdBy;

    /** ISO date string for start of placed-on range (defaults to 1 year ago) */
    @JsonProperty("from_placed_on")
    private String fromPlacedOn;

    /** ISO date string for end of placed-on range (defaults to today) */
    @JsonProperty("to_placed_on")
    private String toPlacedOn;

    // ── Extra filters present in histsearch.pl but not duplicate_orders.pl ──

    @JsonProperty("issn")
    private String issn;

    @JsonProperty("internalnote")
    private String internalnote;

    @JsonProperty("vendornote")
    private String vendornote;

    /** Filter to standing orders only when true */
    @JsonProperty("is_standing")
    private Boolean isStanding;

    /** Branch code of the managing library */
    @JsonProperty("managing_library")
    private String managingLibrary;

    /**
     * Additional field filters for searchable aqbasket additional fields.
     * Each entry is {@code { "id": fieldId, "value": "..." }}.
     * Mirrors the additional_field_filters list in histsearch.pl.
     */
    @JsonProperty("additional_fields")
    private List<Map<String, Object>> additionalFields;
}

