package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for closing a basket (op=cud-close).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class BasketCloseRequest {

    /** When true, skip the confirmation step and close immediately */
    @JsonProperty("confirm")
    private Boolean confirm;

    /**
     * When true, automatically create a basket group, attach the basket,
     * and close the group — mirrors the createbasketgroup CGI param.
     */
    @JsonProperty("create_basket_group")
    private Boolean createBasketGroup;

    /** Branch code used as deliveryplace/billingplace for the new basket group */
    @JsonProperty("branchcode")
    private String branchcode;
}

