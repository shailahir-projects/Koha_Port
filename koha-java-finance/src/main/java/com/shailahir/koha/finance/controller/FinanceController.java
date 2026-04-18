package com.shailahir.koha.finance.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.finance.dto.*;
import com.shailahir.koha.finance.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

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
@Slf4j
@RestController
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/cash_registers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<CashRegisterDto>> listCashRegisters(Pageable pageable) {
        log.debug("Entering listCashRegisters - {}", pageable);
        return ResponseEntity.ok(financeService.listCashRegisters(pageable));
    }

    @GetMapping("/cash_registers/{cash_register_id}/cashups", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CashupDto>> listCashups(@PathVariable("cash_register_id") Long cashRegisterId) {
        log.debug("Entering listCashups - {}", cashRegisterId);
        return ResponseEntity.ok(financeService.listCashups(cashRegisterId));
    }

    @GetMapping("/cashups/{cashup_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CashupDto> getCashup(@PathVariable("cashup_id") Long cashupId) {
        log.debug("Entering getCashup - {}", cashupId);
        return ResponseEntity.ok(financeService.getCashup(cashupId));
    }

    @GetMapping("/patrons/{patron_id}/account", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PatronAccountDto> getPatronAccount(@PathVariable("patron_id") Long patronId) {
        log.debug("Entering getPatronAccount - {}", patronId);
        return ResponseEntity.ok(financeService.getPatronAccount(patronId));
    }

    @GetMapping("/patrons/{patron_id}/account/credits", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<AccountLineDto>> listPatronCredits(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        log.debug("Entering listPatronCredits - {}, {}", patronId, pageable);
        return ResponseEntity.ok(financeService.listPatronCredits(patronId, pageable));
    }

    @PostMapping("/patrons/{patron_id}/account/credits", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AccountLineDto> addPatronCredit(@PathVariable("patron_id") Long patronId, @RequestBody AccountLineDto credit) {
        log.debug("Entering addPatronCredit - {}, {}", patronId, credit);
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.addPatronCredit(patronId, credit));
    }

    @GetMapping("/patrons/{patron_id}/account/credits/{credit_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AccountLineDto> getPatronCredit(@PathVariable("patron_id") Long patronId, @PathVariable("credit_id") Long creditId) {
        log.debug("Entering getPatronCredit - {}, {}", patronId, creditId);
        return ResponseEntity.ok(financeService.getPatronCredit(patronId, creditId));
    }

    @GetMapping("/patrons/{patron_id}/account/debits", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<AccountLineDto>> listPatronDebits(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        log.debug("Entering listPatronDebits - {}, {}", patronId, pageable);
        return ResponseEntity.ok(financeService.listPatronDebits(patronId, pageable));
    }

    @PostMapping("/patrons/{patron_id}/account/debits", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AccountLineDto> addPatronDebit(@PathVariable("patron_id") Long patronId, @RequestBody AccountLineDto debit) {
        log.debug("Entering addPatronDebit - {}, {}", patronId, debit);
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.addPatronDebit(patronId, debit));
    }

    @GetMapping("/patrons/{patron_id}/account/debits/{debit_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AccountLineDto> getPatronDebit(@PathVariable("patron_id") Long patronId, @PathVariable("debit_id") Long debitId) {
        log.debug("Entering getPatronDebit - {}, {}", patronId, debitId);
        return ResponseEntity.ok(financeService.getPatronDebit(patronId, debitId));
    }
}
