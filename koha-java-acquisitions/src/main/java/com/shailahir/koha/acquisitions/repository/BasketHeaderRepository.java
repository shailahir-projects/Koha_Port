package com.shailahir.koha.acquisitions.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.ContractDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JDBC repository for aqcontract and basket-header update operations.
 * Mirrors GetContracts, GetContract, ModBasketHeader, NewBasket.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BasketHeaderRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<ContractDto> CONTRACT_MAPPER = (rs, rn) -> ContractDto.builder()
            .contractnumber(rs.getLong("contractnumber"))
            .contractname(rs.getString("contractname"))
            .contractdescription(rs.getString("contractdescription"))
            .booksellerid(rs.getObject("booksellerid") != null ? rs.getLong("booksellerid") : null)
            .contractstartdate(rs.getObject("contractstartdate", LocalDate.class))
            .contractenddate(rs.getObject("contractenddate", LocalDate.class))
            .build();

    // ── Contracts ──────────────────────────────────────────────────────────────

    /**
     * Returns active contracts for a vendor.
     * Mirrors GetContracts({ booksellerid => ..., activeonly => 1 }).
     * "Active" = contractenddate >= today or NULL.
     */
    public List<ContractDto> findActiveContractsByVendor(Long booksellerid) {
        log.debug("Entering findActiveContractsByVendor - {}", booksellerid);
        String sql = """
                SELECT contractnumber, contractname, contractdescription,
                       booksellerid, contractstartdate, contractenddate
                  FROM aqcontract
                 WHERE booksellerid = ?
                   AND (contractenddate >= CURRENT_DATE OR contractenddate IS NULL)
                 ORDER BY contractname
                """;
        return jdbc.query(sql, CONTRACT_MAPPER, booksellerid);
    }

    public Optional<ContractDto> findContractById(Long contractnumber) {
        log.debug("Entering findContractById - {}", contractnumber);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT * FROM aqcontract WHERE contractnumber = ?",
                            CONTRACT_MAPPER, contractnumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Basket header update (ModBasketHeader) ─────────────────────────────────

    /**
     * Updates the header fields of an existing basket.
     * Mirrors ModBasketHeader() in C4::Acquisition.
     */
    public void updateHeader(Long basketno, String basketname, String note,
                             String booksellernote, Long contractnumber,
                             Long booksellerid, String deliveryplace,
                             String billingplace, Boolean isStanding,
                             String createItems) {
        log.debug("Entering updateHeader - {}, {}, {}, {}, {}, {}, {}, {}, {}, {}", basketno, basketname, note, booksellernote, contractnumber, booksellerid, deliveryplace, billingplace, isStanding, createItems);
        jdbc.update("""
                UPDATE aqbasket
                   SET basketname    = ?,
                       note          = ?,
                       booksellernote = ?,
                       contractnumber = ?,
                       booksellerid  = ?,
                       deliveryplace = ?,
                       billingplace  = ?,
                       is_standing   = ?,
                       create_items  = ?
                 WHERE basketno = ?
                """,
                basketname,
                note,
                booksellernote,
                contractnumber,
                booksellerid,
                deliveryplace,
                billingplace,
                Boolean.TRUE.equals(isStanding) ? 1 : 0,
                createItems,
                basketno);
    }

    // ── New basket (NewBasket) ─────────────────────────────────────────────────

    /**
     * Creates a new basket and returns its basketno.
     * Mirrors NewBasket() in C4::Acquisition.
     */
    public Long createBasket(Long booksellerid, Long authorisedby,
                             String basketname, String note, String booksellernote,
                             Long contractnumber, String deliveryplace,
                             String billingplace, Boolean isStanding,
                             String createItems) {
        log.debug("Entering createBasket - {}, {}, {}, {}, {}, {}, {}, {}, {}, {}", booksellerid, authorisedby, basketname, note, booksellernote, contractnumber, deliveryplace, billingplace, isStanding, createItems);
        String sql = """
                INSERT INTO aqbasket
                    (booksellerid, authorisedby, basketname, note, booksellernote,
                     contractnumber, deliveryplace, billingplace, is_standing,
                     create_items, creationdate)
                VALUES (?,?,?,?,?,?,?,?,?,?,NOW())
                """;
        org.springframework.jdbc.support.KeyHolder kh =
                new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbc.update(con -> {
            java.sql.PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, booksellerid);
            ps.setObject(2, authorisedby);
            ps.setString(3, basketname != null ? basketname : "");
            ps.setString(4, note != null ? note : "");
            ps.setString(5, booksellernote != null ? booksellernote : "");
            ps.setObject(6, contractnumber);
            ps.setString(7, deliveryplace != null ? deliveryplace : "");
            ps.setString(8, billingplace != null ? billingplace : "");
            ps.setInt(9, Boolean.TRUE.equals(isStanding) ? 1 : 0);
            ps.setString(10, createItems != null ? createItems : "");
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("basketno")).longValue();
    }

    // ── Vendor name ────────────────────────────────────────────────────────────

    public Optional<String> findVendorName(Long booksellerid) {
        log.debug("Entering findVendorName - {}", booksellerid);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT name FROM aqbooksellers WHERE id = ?",
                            String.class, booksellerid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── EDI / PO number read-only check ───────────────────────────────────────

    /**
     * Returns true when the basket was created from an EDI QUOTE and the vendor's
     * EDI account has po_is_basketname enabled.
     * Mirrors the basket_name_readonly logic in basketheader.pl.
     */
    public boolean isBasketNameReadonly(Long basketno) {
        log.debug("Entering isBasketNameReadonly - {}", basketno);
        try {
            // Check if this basket has an associated EDI QUOTE message
            Integer edifactMsg = jdbc.queryForObject(
                    """
                    SELECT COUNT(*) FROM edifact_messages
                     WHERE basketno = ? AND message_type = 'QUOTE'
                    """, Integer.class, basketno);
            if (edifactMsg == null || edifactMsg == 0) return false;

            // Get the vendor_id for this basket
            Long booksellerid = jdbc.queryForObject(
                    "SELECT booksellerid FROM aqbasket WHERE basketno = ?", Long.class, basketno);
            if (booksellerid == null) return false;

            // Check if the vendor EDI account has po_is_basketname = 1
            Integer poIsBasketname = jdbc.queryForObject(
                    "SELECT po_is_basketname FROM vendor_edi_accounts WHERE vendor_id = ? LIMIT 1",
                    Integer.class, booksellerid);
            return poIsBasketname != null && poIsBasketname == 1;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true when the basket has an EDI order and the vendor EDI account
     * has po_is_basketname enabled (used during save to prevent name override).
     */
    public boolean hasEdiOrderWithPoBasketname(Long basketno, Long booksellerid) {
        log.debug("Entering hasEdiOrderWithPoBasketname - {}, {}", basketno, booksellerid);
        try {
            Integer edifactMsg = jdbc.queryForObject(
                    """
                    SELECT COUNT(*) FROM edifact_messages
                     WHERE basketno = ? AND message_type IN ('ORDERS','QUOTE')
                    """, Integer.class, basketno);
            if (edifactMsg == null || edifactMsg == 0) return false;

            Integer poIsBasketname = jdbc.queryForObject(
                    "SELECT po_is_basketname FROM vendor_edi_accounts WHERE vendor_id = ? LIMIT 1",
                    Integer.class, booksellerid);
            return poIsBasketname != null && poIsBasketname == 1;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the current basket name for a given basketno. */
    public Optional<String> findBasketName(Long basketno) {
        log.debug("Entering findBasketName - {}", basketno);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT basketname FROM aqbasket WHERE basketno = ?",
                            String.class, basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}


