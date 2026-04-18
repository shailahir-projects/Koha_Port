package com.shailahir.koha.patron.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentDto {
    private BigDecimal amount;
    private String paymentType;
    private String branchcode;
    private String note;
}

