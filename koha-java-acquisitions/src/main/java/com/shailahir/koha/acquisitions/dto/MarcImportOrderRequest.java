package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request body for POST /acquisitions/marc-import/baskets/{basketno}/import.
 * Maps to the CGI parameters collected in the cud-import_records op of addorderiso2709.pl.
 */
@Data
@JacksonXmlRootElement
public class MarcImportOrderRequest {

    @JsonProperty("import_batch_id")
    private Long importBatchId;

    /** IDs of the import records the user selected for ordering */
    @JsonProperty("import_record_id_selected")
    private List<Long> importRecordIdSelected;

    /** Matcher to use for duplicate detection (null = no matching) */
    @JsonProperty("matcher_id")
    private Long matcherId;

    /** Default budget_id to use when no per-record budget supplied */
    @JsonProperty("all_budget_id")
    private Long allBudgetId;

    @JsonProperty("all_sort1")
    private String allSort1;

    @JsonProperty("all_sort2")
    private String allSort2;

    @JsonProperty("all_order_internalnote")
    private String allOrderInternalnote;

    @JsonProperty("all_order_vendornote")
    private String allOrderVendornote;

    @JsonProperty("all_currency")
    private String allCurrency;

    /** Per-record overrides, keyed by import_record_id */
    @JsonProperty("record_overrides")
    private List<RecordOverride> recordOverrides;

    @Data
    public static class RecordOverride {

        @JsonProperty("import_record_id")
        private Long importRecordId;

        @JsonProperty("budget_id")
        private Long budgetId;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("price")
        private BigDecimal price;

        @JsonProperty("replacement_price")
        private BigDecimal replacementPrice;

        @JsonProperty("discount")
        private BigDecimal discount;

        @JsonProperty("sort1")
        private String sort1;

        @JsonProperty("sort2")
        private String sort2;

        /** Item-level fields for basket.create_items = 'ordering' */
        @JsonProperty("items")
        private List<ItemOverride> items;
    }

    @Data
    public static class ItemOverride {
        @JsonProperty("homebranch")
        private String homebranch;

        @JsonProperty("holdingbranch")
        private String holdingbranch;

        @JsonProperty("itype")
        private String itype;

        @JsonProperty("location")
        private String location;

        @JsonProperty("ccode")
        private String ccode;

        @JsonProperty("notforloan")
        private String notforloan;

        @JsonProperty("barcode")
        private String barcode;

        @JsonProperty("itemprice")
        private BigDecimal itemprice;

        @JsonProperty("replacementprice")
        private BigDecimal replacementprice;

        @JsonProperty("itemcallnumber")
        private String itemcallnumber;

        @JsonProperty("enumchron")
        private String enumchron;

        @JsonProperty("uri")
        private String uri;

        @JsonProperty("copyno")
        private String copyno;

        @JsonProperty("nonpublic_note")
        private String nonpublicNote;

        @JsonProperty("public_note")
        private String publicNote;
    }
}

