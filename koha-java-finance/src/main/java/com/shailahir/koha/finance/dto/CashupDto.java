package com.shailahir.koha.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CashupDto {
    private Long cashupId;
    private Long cashRegisterId;
    private BigDecimal amount;
    private LocalDateTime cashupTime;
    private String notes;
    private String managerId;
}

