package com.shailahir.koha.patron.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.AccountLineDto;
import com.shailahir.koha.patron.dto.PaymentDto;
import com.shailahir.koha.patron.repository.AccountRepository;
import com.shailahir.koha.patron.repository.PatronRepository;
import com.shailahir.koha.patron.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for patron account/financial operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PatronRepository patronRepository;

    @Override
    public List<AccountLineDto> getAccountLines(Long patronId) {
        log.debug("Entering getAccountLines - {}", patronId);
        return accountRepository.findByPatronId(patronId);
    }

    @Override
    public AccountLineDto getAccountLine(Long patronId, Long accountlinesId) {
        log.debug("Entering getAccountLine - {}, {}", patronId, accountlinesId);
        return accountRepository.findById(accountlinesId);
    }

    @Override
    @Transactional
    public AccountLineDto addManualCredit(Long patronId, AccountLineDto dto) {
        log.debug("Entering addManualCredit - {}, {}", patronId, dto);
        return accountRepository.addManualCredit(patronId, dto);
    }

    @Override
    @Transactional
    public AccountLineDto addManualInvoice(Long patronId, AccountLineDto dto) {
        log.debug("Entering addManualInvoice - {}, {}", patronId, dto);
        return accountRepository.addManualInvoice(patronId, dto);
    }

    @Override
    @Transactional
    public void cancelCharge(Long accountlinesId) {
        log.debug("Entering cancelCharge - {}", accountlinesId);
        accountRepository.cancelCharge(accountlinesId);
    }

    @Override
    @Transactional
    public void applyPayment(Long patronId, PaymentDto dto) {
        log.debug("Entering applyPayment - {}, {}", patronId, dto);
        accountRepository.applyPayment(patronId, dto.getAmount(), dto.getPaymentType(), dto.getBranchcode());
    }

    @Override
    public Map<String, Object> getAccountSummary(Long patronId) {
        log.debug("Entering getAccountSummary - {}", patronId);
        BigDecimal outstanding = accountRepository.getTotalOutstanding(patronId);
        return Map.of(
            "patron_id", patronId,
            "total_outstanding", outstanding,
            "account_lines", accountRepository.findByPatronId(patronId).size()
        );
    }
}

