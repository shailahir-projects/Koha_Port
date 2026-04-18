package com.shailahir.koha.catalog.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class CheckoutDto {
    private Long checkoutId;
    private Long patronId;
    private Long itemId;
    private Long biblioId;
    private LocalDateTime dueDate;
    private String branchCode;
    private LocalDateTime issueDate;
    private LocalDateTime lastRenewedDate;
    private LocalDateTime returnDate;
    private Integer renewals;
    private Integer autoRenew;
    private String autoRenewError;
    private LocalDateTime timestamp;
    private String issuerId;
    private String note;
    private Boolean checkedIn;
}
