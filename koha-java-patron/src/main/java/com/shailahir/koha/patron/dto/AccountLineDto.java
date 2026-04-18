package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountLineDto {
    @JsonProperty("accountlines_id")
    private Long accountlines_id;
    @JsonProperty("patron_id")
    private Long borrowernumber;
    @JsonProperty("item_id")
    private Long itemnumber;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private String accounttype;
    private String status;
    @JsonProperty("payment_type")
    private String paymenttype;
    @JsonProperty("amount_outstanding")
    private BigDecimal amountoutstanding;
    private BigDecimal lastincrement;
    private String note;
    @JsonProperty("manager_id")
    private Long managerId;
    @JsonProperty("interface")
    private String interfaceId;
    private String branchcode;
    @JsonProperty("issue_id")
    private Long issueid;
}

