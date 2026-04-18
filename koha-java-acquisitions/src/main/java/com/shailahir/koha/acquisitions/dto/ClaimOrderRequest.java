package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request body for POST /acquisitions/late-orders/claim.
 * Mirrors the cud-send_alert form parameters in lateorders.pl.
 */
@Data
public class ClaimOrderRequest {

    /** Order numbers to send claim alerts for */
    @JsonProperty("ordernumbers")
    private List<Long> ordernumbers;

    /**
     * The letter code to use for the claim alert.
     * Mirrors $input->param("letter_code").
     */
    @JsonProperty("letter_code")
    private String letterCode;
}

