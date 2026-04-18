package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request body for creating or modifying a basket group (op=cud-attachbasket).
 */
@Data
@JacksonXmlRootElement
public class BasketGroupRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("billingplace")
    private String billingplace;

    @JsonProperty("deliveryplace")
    private String deliveryplace;

    @JsonProperty("freedeliveryplace")
    private String freedeliveryplace;

    @JsonProperty("deliverycomment")
    private String deliverycomment;

    @JsonProperty("closed")
    private Boolean closed;

    /** List of basketno values to assign to this group */
    @JsonProperty("basket_list")
    private List<Long> basketList;
}

