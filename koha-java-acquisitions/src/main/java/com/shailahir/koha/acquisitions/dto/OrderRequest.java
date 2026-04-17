package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request body for POST /acquisitions/orders and PUT /acquisitions/orders/{id}.
 * Maps to the CGI parameters collected in addorder.pl.
 */
@Data
public class OrderRequest {

    /** Existing order number (present for modify, absent for create) */
    @JsonProperty("ordernumber")
    private Long ordernumber;

    @NotNull
    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @NotNull
    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("listprice")
    private BigDecimal listprice;

    @JsonProperty("uncertainprice")
    private Boolean uncertainprice;

    @JsonProperty("tax_rate")
    private BigDecimal taxRate;

    @JsonProperty("discount")
    private BigDecimal discount;

    @JsonProperty("rrp")
    private BigDecimal rrp;

    @JsonProperty("replacementprice")
    private BigDecimal replacementprice;

    @JsonProperty("ecost")
    private BigDecimal ecost;

    @JsonProperty("unitprice")
    private BigDecimal unitprice;

    @JsonProperty("order_internalnote")
    private String orderInternalnote;

    @JsonProperty("order_vendornote")
    private String orderVendornote;

    @JsonProperty("sort1")
    private String sort1;

    @JsonProperty("sort2")
    private String sort2;

    @JsonProperty("subscriptionid")
    private Long subscriptionid;

    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    // ── Bibliographic fields (used when no biblionumber provided) ──
    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("ean")
    private String ean;

    @JsonProperty("publishercode")
    private String publishercode;

    @JsonProperty("publicationyear")
    private String publicationyear;

    @JsonProperty("itemtype")
    private String itemtype;

    @JsonProperty("editionstatement")
    private String editionstatement;

    @JsonProperty("series")
    private String series;

    // ── Suggestion linkage ──
    @JsonProperty("suggestionid")
    private Long suggestionid;

    /** Whether to skip duplicate-biblio check */
    @JsonProperty("confirm_not_duplicate")
    private Boolean confirmNotDuplicate;

    /** Whether to bypass budget-exceeded warning */
    @JsonProperty("confirm_budget_exceeding")
    private Boolean confirmBudgetExceeding;

    /** Colon-separated list of patron ids to notify about the order */
    @JsonProperty("users_ids")
    private String usersIds;

    /** Total order cost used for budget validation */
    @JsonProperty("total")
    private BigDecimal total;

    // ── Item creation fields (used when basket.create_items = 'ordering') ──
    @JsonProperty("items")
    private List<ItemData> items;

    @Data
    public static class ItemData {
        private String barcode;
        private String homebranch;
        private String holdingbranch;
        private String itype;
        private String location;
        private BigDecimal replacementprice;
        private String callnumber;
    }
}

