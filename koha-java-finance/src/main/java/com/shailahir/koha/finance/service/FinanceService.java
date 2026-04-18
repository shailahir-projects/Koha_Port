package com.shailahir.koha.finance.service;

import com.shailahir.koha.finance.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FinanceService {
    Page<CashRegisterDto> listCashRegisters(Pageable pageable);
    CashRegisterDto getCashRegister(Long id);
    List<CashupDto> listCashups(Long cashRegisterId);
    CashupDto getCashup(Long cashupId);
    PatronAccountDto getPatronAccount(Long patronId);
    Page<AccountLineDto> listPatronCredits(Long patronId, Pageable pageable);
    AccountLineDto addPatronCredit(Long patronId, AccountLineDto credit);
    AccountLineDto getPatronCredit(Long patronId, Long creditId);
    Page<AccountLineDto> listPatronDebits(Long patronId, Pageable pageable);
    AccountLineDto addPatronDebit(Long patronId, AccountLineDto debit);
    AccountLineDto getPatronDebit(Long patronId, Long debitId);
}

