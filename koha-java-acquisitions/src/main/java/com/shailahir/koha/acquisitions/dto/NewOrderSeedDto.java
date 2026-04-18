package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for neworderempty.pl / newordersubscription.pl /
 * newordersuggestion.pl – seed data used to pre-fill a new order form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class NewOrderSeedDto {

    // Basket / vendor context
    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("booksellername")
    private String booksellername;

    // Subscription context (newordersubscription)
    @JsonProperty("subscriptionid")
    private Long subscriptionid;

    @JsonProperty("subscription_title")
    private String subscriptionTitle;

    // Suggestion context (newordersuggestion)
    @JsonProperty("suggestionid")
    private Long suggestionid;

    @JsonProperty("suggested_title")
    private String suggestedTitle;

    @JsonProperty("suggested_author")
    private String suggestedAuthor;

    @JsonProperty("suggested_isbn")
    private String suggestedIsbn;

    @JsonProperty("suggested_publishercode")
    private String suggestedPublishercode;

    @JsonProperty("suggested_quantity")
    private Integer suggestedQuantity;

    @JsonProperty("suggested_price")
    private BigDecimal suggestedPrice;

    @JsonProperty("suggested_budget_id")
    private Long suggestedBudgetId;

    // Bibliographic seed (neworderempty with biblionumber)
    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("publishercode")
    private String publishercode;

    @JsonProperty("publicationyear")
    private String publicationyear;

    // Default order values
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("listprice")
    private BigDecimal listprice;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @JsonProperty("uncertainprice")
    private Boolean uncertainprice;
}

