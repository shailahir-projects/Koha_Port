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
 * A single row from the order history search (GetHistory result).
 * Used in both the search result list and the selected/duplicated orders list.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class OrderHistoryDto {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("basketgroupid")
    private Long basketgroupid;

    @JsonProperty("basketgroupname")
    private String basketgroupname;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("issn")
    private String issn;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("quantityreceived")
    private Integer quantityreceived;

    @JsonProperty("ecost_tax_excluded")
    private BigDecimal ecostTaxExcluded;

    @JsonProperty("ecost_tax_included")
    private BigDecimal ecostTaxIncluded;

    @JsonProperty("listprice")
    private BigDecimal listprice;

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("budget_name")
    private String budgetName;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("orderstatus")
    private String orderstatus;

    @JsonProperty("datereceived")
    private LocalDate datereceived;

    @JsonProperty("datecancellationprinted")
    private LocalDate datecancellationprinted;

    @JsonProperty("order_internalnote")
    private String orderInternalnote;

    @JsonProperty("order_vendornote")
    private String orderVendornote;

    @JsonProperty("sort1")
    private String sort1;

    @JsonProperty("sort2")
    private String sort2;

    @JsonProperty("invoicenumber")
    private String invoicenumber;

    @JsonProperty("dateplaced")
    private LocalDate dateplaced;
}

