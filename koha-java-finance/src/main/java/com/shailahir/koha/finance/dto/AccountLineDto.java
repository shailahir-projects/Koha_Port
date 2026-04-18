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
public class AccountLineDto {
    private Long accountLineId;
    private Long patronId;
    private String type;
    private String description;
    private BigDecimal amount;
    private BigDecimal amountOutstanding;
    private String debitType;
    private String creditType;
    private String status;
    private LocalDateTime date;
    private Long itemId;
    private Long issueId;
    private Long orderId;
    private Long cashRegisterId;
    private Long cashupId;
    private String note;
    private Boolean reversed;
    private LocalDateTime reversalDate;
}

