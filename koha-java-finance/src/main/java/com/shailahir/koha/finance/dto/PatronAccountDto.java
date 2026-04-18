package com.shailahir.koha.finance.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class PatronAccountDto {
    private Long patronId;
    private BigDecimal balance;
    private BigDecimal outstanding;
    private BigDecimal credit;
    private BigDecimal debit;
}

