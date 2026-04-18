package com.shailahir.koha.finance.controller;

import com.shailahir.koha.finance.dto.*;
import com.shailahir.koha.finance.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Finance controller covering:
 * - /cash_registers (GET)
 * - /cash_registers/{id}/cashups (GET)
 * - /cashups/{cashup_id} (GET)
 * - /patrons/{patron_id}/account (GET)
 * - /patrons/{patron_id}/account/credits (GET, POST)
 * - /patrons/{patron_id}/account/credits/{credit_id} (GET)
 * - /patrons/{patron_id}/account/debits (GET, POST)
 * - /patrons/{patron_id}/account/debits/{debit_id} (GET)
 */
@RestController
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/cash_registers")
    public ResponseEntity<Page<CashRegisterDto>> listCashRegisters(Pageable pageable) {
        return ResponseEntity.ok(financeService.listCashRegisters(pageable));
    }

    @GetMapping("/cash_registers/{cash_register_id}/cashups")
    public ResponseEntity<List<CashupDto>> listCashups(@PathVariable("cash_register_id") Long cashRegisterId) {
        return ResponseEntity.ok(financeService.listCashups(cashRegisterId));
    }

    @GetMapping("/cashups/{cashup_id}")
    public ResponseEntity<CashupDto> getCashup(@PathVariable("cashup_id") Long cashupId) {
        return ResponseEntity.ok(financeService.getCashup(cashupId));
    }

    @GetMapping("/patrons/{patron_id}/account")
    public ResponseEntity<PatronAccountDto> getPatronAccount(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(financeService.getPatronAccount(patronId));
    }

    @GetMapping("/patrons/{patron_id}/account/credits")
    public ResponseEntity<Page<AccountLineDto>> listPatronCredits(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        return ResponseEntity.ok(financeService.listPatronCredits(patronId, pageable));
    }

    @PostMapping("/patrons/{patron_id}/account/credits")
    public ResponseEntity<AccountLineDto> addPatronCredit(@PathVariable("patron_id") Long patronId, @RequestBody AccountLineDto credit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.addPatronCredit(patronId, credit));
    }

    @GetMapping("/patrons/{patron_id}/account/credits/{credit_id}")
    public ResponseEntity<AccountLineDto> getPatronCredit(@PathVariable("patron_id") Long patronId, @PathVariable("credit_id") Long creditId) {
        return ResponseEntity.ok(financeService.getPatronCredit(patronId, creditId));
    }

    @GetMapping("/patrons/{patron_id}/account/debits")
    public ResponseEntity<Page<AccountLineDto>> listPatronDebits(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        return ResponseEntity.ok(financeService.listPatronDebits(patronId, pageable));
    }

    @PostMapping("/patrons/{patron_id}/account/debits")
    public ResponseEntity<AccountLineDto> addPatronDebit(@PathVariable("patron_id") Long patronId, @RequestBody AccountLineDto debit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.addPatronDebit(patronId, debit));
    }

    @GetMapping("/patrons/{patron_id}/account/debits/{debit_id}")
    public ResponseEntity<AccountLineDto> getPatronDebit(@PathVariable("patron_id") Long patronId, @PathVariable("debit_id") Long debitId) {
        return ResponseEntity.ok(financeService.getPatronDebit(patronId, debitId));
    }
}
