package com.shailahir.koha.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PatronAccountDto {
    private Long patronId;
    private BigDecimal balance;
    private BigDecimal outstanding;
    private BigDecimal credit;
    private BigDecimal debit;
}

