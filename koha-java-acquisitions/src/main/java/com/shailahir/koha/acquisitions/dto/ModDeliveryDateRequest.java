package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for moddeliverydate.pl – update only the estimated delivery date
 * of a single order whose basket is already closed.
 */
@Data
public class ModDeliveryDateRequest {

    /**
     * The new estimated delivery date, or null to clear it.
     */
    @JsonProperty("estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;
}

