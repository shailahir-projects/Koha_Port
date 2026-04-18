package com.shailahir.koha.finance.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class CashRegisterDto {
    private Long cashRegisterId;
    private String name;
    private String description;
    private BigDecimal initialFloat;
    private Boolean archived;
    private String libraryId;
}

