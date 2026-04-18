package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CheckoutDto {
    private Long checkoutId;
    private Long issueId;
    private Long patronId;
    private Long itemId;
    private LocalDateTime dueDate;
    private LocalDateTime date;
    private String branchCode;
    private String branchcode;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;
    private Integer renewals;
    private Boolean checkedIn;
    private Boolean autoRenew;
    private String autoRenewError;
    private String checkinLibrary;
}

