package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/** Request DTO for transferorder.pl – move an order to a different basket. */
@Data
public class TransferOrderRequest {

    @JsonProperty("to_basketno")
    private Long toBasketno;
}

