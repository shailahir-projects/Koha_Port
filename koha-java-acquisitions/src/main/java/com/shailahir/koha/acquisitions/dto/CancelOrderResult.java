package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a cancel-order operation.
 * Mirrors the template variables set after $order->cancel() in cancelorder.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class CancelOrderResult {

    @JsonProperty("ordernumber")
    private Long ordernumber;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("biblionumber")
    private Long biblionumber;

    /** True when the cancellation succeeded with no errors */
    @JsonProperty("success")
    private boolean success;

    /**
     * Error code when cancellation failed.
     * Possible values (mirrors Perl object_messages):
     * <ul>
     *   <li>{@code error_order_not_found}        — ordernumber does not exist</li>
     *   <li>{@code error_order_already_cancelled} — datecancellationprinted already set</li>
     *   <li>{@code error_delitem}                 — could not delete linked items</li>
     *   <li>{@code error_delbiblio}               — could not delete biblio</li>
     * </ul>
     */
    @JsonProperty("error")
    private String error;

    /**
     * When delete_biblio was requested but the biblio could not be deleted,
     * this carries a human-readable explanation.
     */
    @JsonProperty("error_detail")
    private String errorDetail;
}

