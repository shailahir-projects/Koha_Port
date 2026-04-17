package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for a basket (aqbasket row), enriched with related data for the basket detail view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto {

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("booksellername")
    private String booksellername;

    @JsonProperty("authorisedby")
    private Long authorisedby;

    @JsonProperty("authorisedbyname")
    private String authorisedbyname;

    @JsonProperty("is_standing")
    private Boolean isStanding;

    @JsonProperty("create_items")
    private String createItems;

    @JsonProperty("closedate")
    private String closedate;

    @JsonProperty("creationdate")
    private String creationdate;

    @JsonProperty("note")
    private String note;

    @JsonProperty("booksellernote")
    private String booksellernote;

    @JsonProperty("contractnumber")
    private Long contractnumber;

    @JsonProperty("contractname")
    private String contractname;

    @JsonProperty("basketgroupid")
    private Long basketgroupid;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("deliveryplace")
    private String deliveryplace;

    @JsonProperty("billingplace")
    private String billingplace;

    /** Estimated delivery date computed from closedate + vendor deliverytime */
    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    /** IDs of patrons assigned to this basket */
    @JsonProperty("users_ids")
    private String usersIds;

    /** Whether the basket can be closed (false when is_standing or no orders) */
    @JsonProperty("unclosable")
    private Boolean unclosable;

    /** Whether any order in the basket has an uncertain price */
    @JsonProperty("uncertainprices")
    private Boolean uncertainprices;

    /** Whether the user has at least one budget they can use */
    @JsonProperty("has_budgets")
    private Boolean hasBudgets;
}

