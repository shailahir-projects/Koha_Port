package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

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
}

