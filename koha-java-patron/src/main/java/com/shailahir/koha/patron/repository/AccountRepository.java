package com.shailahir.koha.patron.repository;

import com.shailahir.koha.patron.dto.AccountLineDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Repository for patron account/financial data.
 * Mirrors: members/boraccount.pl, members/pay.pl, members/paycollect.pl,
 *          members/mancredit.pl, members/maninvoice.pl, members/cancel-charge.pl,
 *          members/accountline-details.pl
 */
@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<AccountLineDto> ROW_MAPPER = (rs, rowNum) -> {
        AccountLineDto dto = new AccountLineDto();
        dto.setAccountlines_id(rs.getLong("accountlines_id"));
        dto.setBorrowernumber(rs.getLong("borrowernumber"));
        dto.setItemnumber(rs.getObject("itemnumber", Long.class));
        dto.setDate(rs.getObject("date", java.time.LocalDate.class));
        dto.setAmount(rs.getBigDecimal("amount"));
        dto.setDescription(rs.getString("description"));
        dto.setAccounttype(rs.getString("accounttype"));
        dto.setStatus(rs.getString("status"));
        dto.setPaymenttype(rs.getString("payment_type"));
        dto.setAmountoutstanding(rs.getBigDecimal("amountoutstanding"));
        dto.setLastincrement(rs.getBigDecimal("lastincrement"));
        dto.setNote(rs.getString("note"));
        dto.setManagerId(rs.getObject("manager_id", Long.class));
        dto.setInterfaceId(rs.getString("interface"));
        dto.setBranchcode(rs.getString("branchcode"));
        dto.setIssueid(rs.getObject("issue_id", Long.class));
        return dto;
    };

    public List<AccountLineDto> findByPatronId(Long patronId) {
        return jdbc.query(
            "SELECT * FROM accountlines WHERE borrowernumber = ? ORDER BY date DESC",
            ROW_MAPPER, patronId);
    }

    public AccountLineDto findById(Long accountlinesId) {
        return jdbc.queryForObject(
            "SELECT * FROM accountlines WHERE accountlines_id = ?",
            ROW_MAPPER, accountlinesId);
    }

    /**
     * Manual credit (mancredit.pl) - add a credit to patron account.
     */
    public AccountLineDto addManualCredit(Long patronId, AccountLineDto dto) {
        String sql = """
            INSERT INTO accountlines (borrowernumber, itemnumber, date, amount, description,
                accounttype, status, amountoutstanding, note, manager_id, interface, branchcode)
            VALUES (?, ?, NOW(), ?, ?, 'MANU', 'Manual Credit', ?, ?, ?, 'intranet', ?)
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, patronId);
            ps.setObject(2, dto.getItemnumber());
            ps.setBigDecimal(3, dto.getAmount().negate()); // credit is negative
            ps.setString(4, dto.getDescription());
            ps.setBigDecimal(5, dto.getAmount().negate());
            ps.setString(6, dto.getNote());
            ps.setObject(7, dto.getManagerId());
            ps.setString(8, dto.getBranchcode());
            return ps;
        }, kh);
        dto.setAccountlines_id(((Number) kh.getKeys().get("accountlines_id")).longValue());
        return dto;
    }

    /**
     * Manual invoice (maninvoice.pl) - add a debit/charge to patron account.
     */
    public AccountLineDto addManualInvoice(Long patronId, AccountLineDto dto) {
        String sql = """
            INSERT INTO accountlines (borrowernumber, itemnumber, date, amount, description,
                accounttype, amountoutstanding, note, manager_id, interface, branchcode)
            VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, 'intranet', ?)
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, patronId);
            ps.setObject(2, dto.getItemnumber());
            ps.setBigDecimal(3, dto.getAmount());
            ps.setString(4, dto.getDescription());
            ps.setString(5, dto.getAccounttype() != null ? dto.getAccounttype() : "MANF");
            ps.setBigDecimal(6, dto.getAmount());
            ps.setString(7, dto.getNote());
            ps.setObject(8, dto.getManagerId());
            ps.setString(9, dto.getBranchcode());
            return ps;
        }, kh);
        dto.setAccountlines_id(((Number) kh.getKeys().get("accountlines_id")).longValue());
        return dto;
    }

    /**
     * Cancel/void a charge (cancel-charge.pl)
     */
    public void cancelCharge(Long accountlinesId) {
        jdbc.update("""
            UPDATE accountlines SET amountoutstanding = 0, status = 'VOID'
            WHERE accountlines_id = ?
            """, accountlinesId);
    }

    /**
     * Record a payment against outstanding charges.
     * Mirrors: members/pay.pl, members/paycollect.pl
     */
    public void applyPayment(Long patronId, java.math.BigDecimal amount, String paymentType, String branchcode) {
        // Fetch outstanding debits ordered by date
        List<AccountLineDto> debits = jdbc.query("""
            SELECT * FROM accountlines WHERE borrowernumber = ? AND amountoutstanding > 0
            ORDER BY date ASC
            """, ROW_MAPPER, patronId);

        java.math.BigDecimal remaining = amount;
        for (AccountLineDto debit : debits) {
            if (remaining.compareTo(java.math.BigDecimal.ZERO) <= 0) break;
            java.math.BigDecimal pay = remaining.min(debit.getAmountoutstanding());
            jdbc.update("UPDATE accountlines SET amountoutstanding = amountoutstanding - ? WHERE accountlines_id = ?",
                pay, debit.getAccountlines_id());
            remaining = remaining.subtract(pay);
        }

        // Insert payment record
        jdbc.update("""
            INSERT INTO accountlines (borrowernumber, date, amount, description, accounttype,
                amountoutstanding, payment_type, interface, branchcode)
            VALUES (?, NOW(), ?, 'Payment', 'Pay', 0, ?, 'intranet', ?)
            """, patronId, amount.negate(), paymentType, branchcode);
    }

    public java.math.BigDecimal getTotalOutstanding(Long patronId) {
        java.math.BigDecimal total = jdbc.queryForObject(
            "SELECT COALESCE(SUM(amountoutstanding), 0) FROM accountlines WHERE borrowernumber = ?",
            java.math.BigDecimal.class, patronId);
        return total != null ? total : java.math.BigDecimal.ZERO;
    }
}

