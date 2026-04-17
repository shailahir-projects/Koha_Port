package com.shailahir.koha.circulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CheckoutDto {
    private Long checkoutId;
    private Long patronId;
    private Long itemId;
    private LocalDateTime dueDate;
    private String branchCode;
    private LocalDateTime issueDate;
    private Integer renewals;
    private Boolean checkedIn;
    private Boolean autoRenew;
    private String autoRenewError;
}

