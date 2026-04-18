package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Full parcel (invoice) detail – mirrors parcel.pl. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class ParcelDetailDto {

    @JsonProperty("invoiceid")
    private Long invoiceid;

    @JsonProperty("invoicenumber")
    private String invoicenumber;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("booksellername")
    private String booksellername;

    @JsonProperty("shipmentdate")
    private LocalDate shipmentdate;

    @JsonProperty("billingdate")
    private LocalDate billingdate;

    @JsonProperty("closedate")
    private LocalDate closedate;

    @JsonProperty("shipmentcost")
    private BigDecimal shipmentcost;

    @JsonProperty("shipmentcost_budgetid")
    private Long shipmentcostBudgetid;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("total_received")
    private Integer totalReceived;

    @JsonProperty("total_ecost")
    private BigDecimal totalEcost;

    @JsonProperty("orders")
    private List<OrderDto> orders;
}

