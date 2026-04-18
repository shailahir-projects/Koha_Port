package com.shailahir.koha.admin.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class CashRegisterDto {
    private Long cashRegisterId;
    private String name;
    private String description;
    private String branch;
    private String libraryId;
    private Double currentBalance;
    private Double initialAmount;
    private java.math.BigDecimal initialFloat;
    private String accountType;
    private Boolean archived;
}

