package com.shailahir.koha.finance.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.finance.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Repository for finance data access.
 * Mirrors: pos/*, Koha/Account.pm, Koha/CashRegister.pm
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FinanceRepository {

    private final JdbcTemplate jdbc;

    // ── Cash Registers ────────────────────────────────────────────────────────

    private static final RowMapper<CashRegisterDto> REGISTER_MAPPER = (rs, rn) -> {
        CashRegisterDto dto = new CashRegisterDto();
        dto.setCashRegisterId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setInitialFloat(rs.getBigDecimal("initial_float"));
        dto.setArchived(rs.getBoolean("archived"));
        dto.setLibraryId(rs.getString("branch"));
        return dto;
    };

    public Page<CashRegisterDto> findAllCashRegisters(Pageable pageable) {
        log.debug("Entering findAllCashRegisters - {}", pageable);
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM cash_registers", Integer.class);
        List<CashRegisterDto> list = jdbc.query(
                "SELECT * FROM cash_registers ORDER BY id LIMIT ? OFFSET ?",
                REGISTER_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<CashRegisterDto> findCashRegisterById(Long id) {
        log.debug("Entering findCashRegisterById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM cash_registers WHERE id = ?", REGISTER_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Cashups ───────────────────────────────────────────────────────────────

    private static final RowMapper<CashupDto> CASHUP_MAPPER = (rs, rn) -> {
        CashupDto dto = new CashupDto();
        dto.setCashupId(rs.getLong("id"));
        dto.setCashRegisterId(rs.getLong("register_id"));
        dto.setAmount(rs.getBigDecimal("amount"));
        dto.setCashupTime(rs.getObject("timestamp", java.time.LocalDateTime.class));
        dto.setNotes(rs.getString("notes"));
        dto.setManagerId(rs.getString("manager_id"));
        return dto;
    };

    public List<CashupDto> findCashupsByRegisterId(Long registerId) {
        log.debug("Entering findCashupsByRegisterId - {}", registerId);
        return jdbc.query(
                "SELECT * FROM cash_register_actions WHERE register_id = ? AND type = 'CASHUP' ORDER BY timestamp DESC",
                CASHUP_MAPPER, registerId);
    }

    public Optional<CashupDto> findCashupById(Long cashupId) {
        log.debug("Entering findCashupById - {}", cashupId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM cash_register_actions WHERE id = ? AND type = 'CASHUP'", CASHUP_MAPPER, cashupId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Patron Account ────────────────────────────────────────────────────────

    public Optional<PatronAccountDto> findPatronAccount(Long patronId) {
        log.debug("Entering findPatronAccount - {}", patronId);
        try {
            PatronAccountDto dto = jdbc.queryForObject("""
                    SELECT borrowernumber,
                        COALESCE(SUM(CASE WHEN amount < 0 THEN -amount ELSE 0 END), 0) as credit,
                        COALESCE(SUM(CASE WHEN amount > 0 THEN amount ELSE 0 END), 0) as debit,
                        COALESCE(SUM(amountoutstanding), 0) as outstanding,
                        COALESCE(SUM(amount), 0) as balance
                    FROM accountlines WHERE borrowernumber = ?
                    GROUP BY borrowernumber
                    """, (rs, rn) -> {
                PatronAccountDto a = new PatronAccountDto();
                a.setPatronId(rs.getLong("borrowernumber"));
                a.setBalance(rs.getBigDecimal("balance"));
                a.setOutstanding(rs.getBigDecimal("outstanding"));
                a.setCredit(rs.getBigDecimal("credit"));
                a.setDebit(rs.getBigDecimal("debit"));
                return a;
            }, patronId);
            return Optional.ofNullable(dto);
        } catch (EmptyResultDataAccessException e) {
            PatronAccountDto empty = new PatronAccountDto();
            empty.setPatronId(patronId);
            empty.setBalance(java.math.BigDecimal.ZERO);
            empty.setOutstanding(java.math.BigDecimal.ZERO);
            empty.setCredit(java.math.BigDecimal.ZERO);
            empty.setDebit(java.math.BigDecimal.ZERO);
            return Optional.of(empty);
        }
    }

    // ── Account Lines (Credits & Debits) ──────────────────────────────────────

    private static final RowMapper<AccountLineDto> LINE_MAPPER = (rs, rn) -> {
        AccountLineDto dto = new AccountLineDto();
        dto.setAccountLineId(rs.getLong("accountlines_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setDebitType(rs.getString("debit_type_code"));
        dto.setCreditType(rs.getString("credit_type_code"));
        dto.setDescription(rs.getString("description"));
        dto.setAmount(rs.getBigDecimal("amount"));
        dto.setAmountOutstanding(rs.getBigDecimal("amountoutstanding"));
        dto.setStatus(rs.getString("status"));
        dto.setDate(rs.getObject("date", java.time.LocalDateTime.class));
        dto.setItemId(rs.getObject("itemnumber", Long.class));
        dto.setIssueId(rs.getObject("issue_id", Long.class));
        dto.setNote(rs.getString("note"));
        return dto;
    };

    public Page<AccountLineDto> findCreditsByPatron(Long patronId, Pageable pageable) {
        log.debug("Entering findCreditsByPatron - {}, {}", patronId, pageable);
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM accountlines WHERE borrowernumber = ? AND amount < 0", Integer.class, patronId);
        List<AccountLineDto> list = jdbc.query(
                "SELECT * FROM accountlines WHERE borrowernumber = ? AND amount < 0 ORDER BY date DESC LIMIT ? OFFSET ?",
                LINE_MAPPER, patronId, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Page<AccountLineDto> findDebitsByPatron(Long patronId, Pageable pageable) {
        log.debug("Entering findDebitsByPatron - {}, {}", patronId, pageable);
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM accountlines WHERE borrowernumber = ? AND amount > 0", Integer.class, patronId);
        List<AccountLineDto> list = jdbc.query(
                "SELECT * FROM accountlines WHERE borrowernumber = ? AND amount > 0 ORDER BY date DESC LIMIT ? OFFSET ?",
                LINE_MAPPER, patronId, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<AccountLineDto> findAccountLineById(Long lineId) {
        log.debug("Entering findAccountLineById - {}", lineId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM accountlines WHERE accountlines_id = ?", LINE_MAPPER, lineId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AccountLineDto insertAccountLine(Long patronId, AccountLineDto dto) {
        log.debug("Entering insertAccountLine - {}, {}", patronId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO accountlines (borrowernumber, debit_type_code, credit_type_code, description,
                        amount, amountoutstanding, status, date, itemnumber, note)
                    VALUES (?,?,?,?,?,?,?,NOW(),?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, patronId);
            ps.setString(2, dto.getDebitType());
            ps.setString(3, dto.getCreditType());
            ps.setString(4, dto.getDescription());
            ps.setBigDecimal(5, dto.getAmount());
            ps.setBigDecimal(6, dto.getAmount());
            ps.setString(7, dto.getStatus());
            ps.setObject(8, dto.getItemId());
            ps.setString(9, dto.getNote());
            return ps;
        }, kh);
        dto.setAccountLineId(((Number) kh.getKeys().get("accountlines_id")).longValue());
        dto.setPatronId(patronId);
        return dto;
    }
}

