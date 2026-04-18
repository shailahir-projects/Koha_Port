package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for GET /acquisitions/baskets/{basketno}/header (op=add_form).
 * Contains the basket metadata, the active contracts for the vendor,
 * and the EDI read-only flag for the basket name.
 * Mirrors the template variables assembled in the add_form block of basketheader.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class BasketHeaderFormDto {

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("basketname")
    private String basketname;

    @JsonProperty("note")
    private String note;

    @JsonProperty("booksellernote")
    private String booksellernote;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("booksellername")
    private String booksellername;

    @JsonProperty("contractnumber")
    private Long contractnumber;

    @JsonProperty("is_standing")
    private Boolean isStanding;

    @JsonProperty("create_items")
    private String createItems;

    @JsonProperty("billingplace")
    private String billingplace;

    @JsonProperty("deliveryplace")
    private String deliveryplace;

    /** Active contracts for this vendor (for the contract selector) */
    @JsonProperty("contracts")
    private List<ContractDto> contracts;

    /**
     * When true the basket name field should be read-only.
     * Occurs when the basket was created from an EDI QUOTE and the vendor EDI
     * account has po_is_basketname enabled.
     * Mirrors basket_name_readonly in basketheader.pl.
     */
    @JsonProperty("basket_name_readonly")
    private Boolean basketNameReadonly;

    /** True when editing an existing basket, false for a new basket */
    @JsonProperty("is_an_edit")
    private Boolean isAnEdit;
}

