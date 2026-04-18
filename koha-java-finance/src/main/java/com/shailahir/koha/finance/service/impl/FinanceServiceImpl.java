package com.shailahir.koha.finance.service.impl;

import com.shailahir.koha.finance.dto.*;
import com.shailahir.koha.finance.repository.FinanceRepository;
import com.shailahir.koha.finance.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final FinanceRepository repo;

    @Override
    public Page<CashRegisterDto> listCashRegisters(Pageable pageable) {
        return repo.findAllCashRegisters(pageable);
    }

    @Override
    public CashRegisterDto getCashRegister(Long id) {
        return repo.findCashRegisterById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cash register not found"));
    }

    @Override
    public List<CashupDto> listCashups(Long cashRegisterId) {
        return repo.findCashupsByRegisterId(cashRegisterId);
    }

    @Override
    public CashupDto getCashup(Long cashupId) {
        return repo.findCashupById(cashupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cashup not found"));
    }

    @Override
    public PatronAccountDto getPatronAccount(Long patronId) {
        return repo.findPatronAccount(patronId).orElseGet(() -> {
            PatronAccountDto empty = new PatronAccountDto();
            empty.setPatronId(patronId);
            return empty;
        });
    }

    @Override
    public Page<AccountLineDto> listPatronCredits(Long patronId, Pageable pageable) {
        return repo.findCreditsByPatron(patronId, pageable);
    }

    @Override
    public AccountLineDto addPatronCredit(Long patronId, AccountLineDto credit) {
        // Credits are negative amounts
        if (credit.getAmount() != null && credit.getAmount().signum() > 0) {
            credit.setAmount(credit.getAmount().negate());
        }
        return repo.insertAccountLine(patronId, credit);
    }

    @Override
    public AccountLineDto getPatronCredit(Long patronId, Long creditId) {
        AccountLineDto line = repo.findAccountLineById(creditId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit not found"));
        if (!line.getPatronId().equals(patronId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit not found for patron");
        }
        return line;
    }

    @Override
    public Page<AccountLineDto> listPatronDebits(Long patronId, Pageable pageable) {
        return repo.findDebitsByPatron(patronId, pageable);
    }

    @Override
    public AccountLineDto addPatronDebit(Long patronId, AccountLineDto debit) {
        // Debits are positive amounts
        if (debit.getAmount() != null && debit.getAmount().signum() < 0) {
            debit.setAmount(debit.getAmount().negate());
        }
        return repo.insertAccountLine(patronId, debit);
    }

    @Override
    public AccountLineDto getPatronDebit(Long patronId, Long debitId) {
        AccountLineDto line = repo.findAccountLineById(debitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit not found"));
        if (!line.getPatronId().equals(patronId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit not found for patron");
        }
        return line;
    }
}

