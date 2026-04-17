package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CashRegisterDto {
    private Long cashRegisterId;
    private String name;
    private String branch;
    private Double currentBalance;
    private Double initialAmount;
    private Boolean archived;
}

