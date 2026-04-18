package com.shailahir.koha.patron.service;

import com.shailahir.koha.patron.dto.AccountLineDto;
import com.shailahir.koha.patron.dto.PaymentDto;

import java.util.List;
import java.util.Map;

public interface AccountService {
    List<AccountLineDto> getAccountLines(Long patronId);
    AccountLineDto getAccountLine(Long patronId, Long accountlinesId);
    AccountLineDto addManualCredit(Long patronId, AccountLineDto dto);
    AccountLineDto addManualInvoice(Long patronId, AccountLineDto dto);
    void cancelCharge(Long accountlinesId);
    void applyPayment(Long patronId, PaymentDto dto);
    Map<String, Object> getAccountSummary(Long patronId);
}

