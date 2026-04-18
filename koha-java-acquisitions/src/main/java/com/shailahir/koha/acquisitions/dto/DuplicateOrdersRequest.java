package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request body for POST /acquisitions/baskets/{basketno}/duplicate-orders (cud-do_duplicate).
 * Mirrors the form parameters submitted in the cud-do_duplicate block of duplicate_orders.pl.
 */
@Data
@JacksonXmlRootElement
public class DuplicateOrdersRequest {

    /** Order numbers to duplicate into the target basket */
    @JsonProperty("ordernumbers")
    private List<Long> ordernumbers;

    /**
     * Fields whose values should be copied from the original order rather than
     * overridden by the default values below.
     * Mirrors the copy_existing_value multi-param list.
     */
    @JsonProperty("copy_existing_value")
    private List<String> copyExistingValue;

    // ── Default override values (applied when field NOT in copy_existing_value) ──

    @JsonProperty("all_currency")
    private String allCurrency;

    @JsonProperty("all_budget_id")
    private Long allBudgetId;

    @JsonProperty("all_order_internalnote")
    private String allOrderInternalnote;

    @JsonProperty("all_order_vendornote")
    private String allOrderVendornote;

    @JsonProperty("all_sort1")
    private String allSort1;

    @JsonProperty("all_sort2")
    private String allSort2;
}

