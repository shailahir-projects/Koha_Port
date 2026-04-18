package com.shailahir.koha.serials.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubscriptionDto {
    private Long subscriptionId;
    private Long biblioId;
    private Long vendorId;
    private String libraryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String notes;
    private String internalnotes;
    private Long frequencyId;
    private Long numberpatternId;
    private String callnumber;
    private String location;
    private String itemType;
    private BigDecimal cost;
    private LocalDateTime createdDate;
    private LocalDateTime lastValue;
}

