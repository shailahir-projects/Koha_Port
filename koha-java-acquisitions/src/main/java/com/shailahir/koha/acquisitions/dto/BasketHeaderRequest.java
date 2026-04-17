package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for POST /acquisitions/baskets (new) and
 * PUT /acquisitions/baskets/{basketno}/header (edit).
 * Maps to the cud-add_validate CGI parameters in basketheader.pl.
 */
@Data
public class BasketHeaderRequest {

    /** Required for edit; absent (null) for new basket creation */
    @JsonProperty("basketno")
    private Long basketno;

    @NotNull
    @JsonProperty("booksellerid")
    private Long booksellerid;

    /** Authorised-by patron id (used only on creation, mirrors $loggedinuser) */
    @JsonProperty("authorisedby")
    private Long authorisedby;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("note")
    private String note;

    @JsonProperty("booksellernote")
    private String booksellernote;

    @JsonProperty("contractnumber")
    private Long contractnumber;

    @JsonProperty("deliveryplace")
    private String deliveryplace;

    @JsonProperty("billingplace")
    private String billingplace;

    @JsonProperty("is_standing")
    private Boolean isStanding;

    @JsonProperty("create_items")
    private String createItems;
}

