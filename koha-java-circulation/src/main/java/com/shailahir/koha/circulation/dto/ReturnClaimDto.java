package com.shailahir.koha.circulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReturnClaimDto {
    private Long claimId;
    private Long checkoutId;
    private Long itemId;
    private Long patronId;
    private String notes;
    private LocalDateTime created;
    private String resolution;
    private LocalDateTime resolvedOn;
}

