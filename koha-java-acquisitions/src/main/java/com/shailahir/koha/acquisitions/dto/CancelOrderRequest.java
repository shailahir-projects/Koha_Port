package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Request body for POST /acquisitions/orders/{ordernumber}/cancel.
 * Maps to the cud-confirmcancel CGI parameters in cancelorder.pl.
 */
@Data
public class CancelOrderRequest {

    /**
     * Optional cancellation reason stored in aqorders.cancellationreason.
     * Mirrors the 'reason' CGI parameter.
     */
    @JsonProperty("reason")
    private String reason;

    /**
     * When true, attempt to delete the attached biblio after cancellation
     * (only succeeds when no other orders, items, holds or subscriptions reference it).
     * Mirrors the 'del_biblio' CGI parameter.
     */
    @JsonProperty("delete_biblio")
    private Boolean deleteBiblio;
}

