package com.shailahir.koha.patron.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.AccountLineDto;
import com.shailahir.koha.patron.dto.PaymentDto;
import com.shailahir.koha.patron.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for patron account/financial operations.
 * Mirrors: members/boraccount.pl, members/pay.pl, members/paycollect.pl,
 *          members/mancredit.pl, members/maninvoice.pl, members/cancel-charge.pl,
 *          members/accountline-details.pl, members/printfeercpt.pl,
 *          members/printinvoice.pl, members/print_overdues.pl
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class PatronAccountController {

    private final AccountService accountService;

    /** members/boraccount.pl - outstanding account lines */
    @GetMapping("/patrons/{patron_id}/account")
    public ResponseEntity<List<AccountLineDto>> getPatronAccount(
            @PathVariable("patron_id") Long patronId) {
        log.debug("Entering getPatronAccount - {}", patronId);
        return ResponseEntity.ok(accountService.getAccountLines(patronId));
    }

    /** members/accountline-details.pl - single account line detail */
    @GetMapping("/patrons/{patron_id}/account/{accountlines_id}")
    public ResponseEntity<AccountLineDto> getAccountLine(
            @PathVariable("patron_id") Long patronId,
            @PathVariable("accountlines_id") Long accountlinesId) {
        log.debug("Entering getAccountLine - {}, {}", patronId, accountlinesId);
        return ResponseEntity.ok(accountService.getAccountLine(patronId, accountlinesId));
    }

    /** members/mancredit.pl - add manual credit */
    @PostMapping("/patrons/{patron_id}/account/credits")
    public ResponseEntity<AccountLineDto> addManualCredit(
            @PathVariable("patron_id") Long patronId,
            @RequestBody AccountLineDto dto) {
        log.debug("Entering addManualCredit - {}, {}", patronId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.addManualCredit(patronId, dto));
    }

    /** members/maninvoice.pl - add manual invoice/charge */
    @PostMapping("/patrons/{patron_id}/account/debits")
    public ResponseEntity<AccountLineDto> addManualInvoice(
            @PathVariable("patron_id") Long patronId,
            @RequestBody AccountLineDto dto) {
        log.debug("Entering addManualInvoice - {}, {}", patronId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.addManualInvoice(patronId, dto));
    }

    /** members/cancel-charge.pl - cancel/void a charge */
    @DeleteMapping("/patrons/{patron_id}/account/{accountlines_id}")
    public ResponseEntity<Void> cancelCharge(
            @PathVariable("patron_id") Long patronId,
            @PathVariable("accountlines_id") Long accountlinesId) {
        log.debug("Entering cancelCharge - {}, {}", patronId, accountlinesId);
        accountService.cancelCharge(accountlinesId);
        return ResponseEntity.noContent().build();
    }

    /** members/pay.pl + members/paycollect.pl - apply payment */
    @PostMapping("/patrons/{patron_id}/account/payments")
    public ResponseEntity<Void> applyPayment(
            @PathVariable("patron_id") Long patronId,
            @RequestBody PaymentDto dto) {
        log.debug("Entering applyPayment - {}, {}", patronId, dto);
        accountService.applyPayment(patronId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** members/statistics.pl - patron financial summary */
    @GetMapping("/patrons/{patron_id}/account/summary")
    public ResponseEntity<java.util.Map<String, Object>> getAccountSummary(
            @PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(accountService.getAccountSummary(patronId));
    }
}

