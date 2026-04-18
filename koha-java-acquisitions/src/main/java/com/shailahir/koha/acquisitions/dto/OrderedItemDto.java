package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Ordered item row – one row in the ordered.pl table. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class OrderedItemDto {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("quantityreceived")
    private Integer quantityreceived;

    @JsonProperty("ecost")
    private BigDecimal ecost;

    @JsonProperty("total_ecost")
    private BigDecimal totalEcost;

    @JsonProperty("datecreated")
    private LocalDate datecreated;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("booksellername")
    private String booksellername;

    @JsonProperty("orderstatus")
    private String orderstatus;
}

