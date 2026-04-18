package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;
import java.math.BigDecimal;

@Data
@JacksonXmlRootElement
public class PaymentDto {
    private BigDecimal amount;
    private String paymentType;
    private String branchcode;
    private String note;
}

