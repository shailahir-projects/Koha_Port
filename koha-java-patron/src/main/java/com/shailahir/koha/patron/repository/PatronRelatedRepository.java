package com.shailahir.koha.patron.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.CheckoutDto;
import com.shailahir.koha.patron.dto.HoldDto;
import com.shailahir.koha.patron.dto.HoldGroupDto;
import com.shailahir.koha.patron.dto.IllRequestDto;
import com.shailahir.koha.patron.dto.ExtendedAttributeDto;
import com.shailahir.koha.patron.dto.RecallDto;
import com.shailahir.koha.patron.dto.VirtualShelfDto;
import com.shailahir.koha.patron.dto.ClubHoldDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Repository for patron-related sub-entities: checkouts, holds, extended attributes,
 * ILL requests, recalls, virtual shelves, clubs.
 * Mirrors: members/holdshistory.pl, members/readingrec.pl, members/recallshistory.pl,
 *          members/ill-requests.pl, members/routing-lists.pl
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PatronRelatedRepository {

    private final JdbcTemplate jdbc;

    // ── Checkouts ──────────────────────────────────────────────────────────────

    private static final RowMapper<CheckoutDto> CHECKOUT_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        CheckoutDto dto = new CheckoutDto();
        dto.setIssueId(rs.getLong("issue_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setItemId(rs.getLong("itemnumber"));
        dto.setIssueDate(rs.getObject("issuedate", java.time.LocalDateTime.class));
        dto.setDate(rs.getObject("issuedate", java.time.LocalDateTime.class));
        dto.setDueDate(rs.getObject("date_due", java.time.LocalDateTime.class));
        dto.setReturnDate(rs.getObject("returndate", java.time.LocalDateTime.class));
        dto.setBranchCode(rs.getString("branchcode"));
        dto.setRenewals(rs.getInt("renewals_count"));
        dto.setAutoRenew(rs.getObject("auto_renew", Boolean.class));
        dto.setAutoRenewError(rs.getString("auto_renew_error"));
        dto.setCheckinLibrary(rs.getString("return_branchcode"));
        return dto;
    };

    public List<CheckoutDto> findCurrentCheckoutsByPatronId(Long patronId, Pageable pageable) {
        log.debug("Entering findCurrentCheckoutsByPatronId - {}, {}", patronId, pageable);
        return jdbc.query("""
            SELECT * FROM issues WHERE borrowernumber = ?
            ORDER BY issuedate DESC LIMIT ? OFFSET ?
            """, CHECKOUT_ROW_MAPPER, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    // ── Holds ──────────────────────────────────────────────────────────────────

    private static final RowMapper<HoldDto> HOLD_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        HoldDto dto = new HoldDto();
        dto.setHoldId(rs.getLong("reserve_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setBiblionumber(rs.getLong("biblionumber"));
        dto.setItemnumber(rs.getObject("itemnumber", Long.class));
        dto.setBranchCode(rs.getString("branchcode"));
        dto.setStatus(rs.getString("found"));
        dto.setPriority(rs.getInt("priority"));
        dto.setReservdate(rs.getObject("reservedate", java.time.LocalDate.class));
        dto.setExpirationDate(rs.getObject("expirationdate", java.time.LocalDateTime.class));
        dto.setItemtype(rs.getString("itemtype"));
        dto.setPickupLibraryId(rs.getString("branchcode"));
        dto.setLowestPriority(rs.getBoolean("lowestPriority"));
        dto.setSuspend(rs.getBoolean("suspend"));
        dto.setSuspendUntil(rs.getObject("suspend_until", java.time.LocalDate.class));
        dto.setWaitingDate(rs.getObject("waitingdate", java.time.LocalDateTime.class));
        return dto;
    };

    public List<HoldDto> findActiveHoldsByPatronId(Long patronId, Pageable pageable) {
        log.debug("Entering findActiveHoldsByPatronId - {}, {}", patronId, pageable);
        return jdbc.query("""
            SELECT * FROM reserves WHERE borrowernumber = ?
            ORDER BY priority LIMIT ? OFFSET ?
            """, HOLD_ROW_MAPPER, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    // ── Hold Groups ────────────────────────────────────────────────────────────

    private static final RowMapper<HoldGroupDto> HOLD_GROUP_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        HoldGroupDto dto = new HoldGroupDto();
        dto.setHoldGroupId(rs.getLong("reserve_group_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setBranchcode(rs.getString("branchcode"));
        return dto;
    };

    public List<HoldGroupDto> findHoldGroupsByPatronId(Long patronId) {
        log.debug("Entering findHoldGroupsByPatronId - {}", patronId);
        return jdbc.query(
            "SELECT * FROM reserve_groups WHERE borrowernumber = ?",
            HOLD_GROUP_ROW_MAPPER, patronId);
    }

    public HoldGroupDto insertHoldGroup(Long patronId, HoldGroupDto dto) {
        log.debug("Entering insertHoldGroup - {}, {}", patronId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO reserve_groups (borrowernumber, branchcode) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, patronId);
            ps.setString(2, dto.getBranchcode());
            return ps;
        }, kh);
        dto.setHoldGroupId(((Number) kh.getKeys().get("reserve_group_id")).longValue());
        dto.setPatronId(patronId);
        return dto;
    }

    public void deleteHoldGroup(Long holdGroupId) {
        log.debug("Entering deleteHoldGroup - {}", holdGroupId);
        jdbc.update("DELETE FROM reserve_groups WHERE reserve_group_id = ?", holdGroupId);
    }

    public void cancelHoldGroup(Long patronId, Long holdGroupId) {
        log.debug("Entering cancelHoldGroup - {}, {}", patronId, holdGroupId);
        jdbc.update("""
            UPDATE reserves SET cancellationdate = NOW(), found = NULL
            WHERE borrowernumber = ? AND reserve_group_id = ?
            """, patronId, holdGroupId);
    }

    // ── Extended Attributes ────────────────────────────────────────────────────

    private static final RowMapper<ExtendedAttributeDto> ATTR_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        ExtendedAttributeDto dto = new ExtendedAttributeDto();
        dto.setExtendedAttributeId(rs.getLong("id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setCode(rs.getString("code"));
        dto.setValue(rs.getString("attribute"));
        dto.setAttribute(rs.getString("attribute"));
        return dto;
    };

    public List<ExtendedAttributeDto> findAttributesByPatronId(Long patronId) {
        log.debug("Entering findAttributesByPatronId - {}", patronId);
        return jdbc.query(
            "SELECT * FROM borrower_attributes WHERE borrowernumber = ? ORDER BY code",
            ATTR_ROW_MAPPER, patronId);
    }

    public ExtendedAttributeDto insertAttribute(Long patronId, ExtendedAttributeDto dto) {
        log.debug("Entering insertAttribute - {}, {}", patronId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO borrower_attributes (borrowernumber, code, attribute) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, patronId);
            ps.setString(2, dto.getCode());
            ps.setString(3, dto.getValue() != null ? dto.getValue() : dto.getAttribute());
            return ps;
        }, kh);
        dto.setExtendedAttributeId(((Number) kh.getKeys().get("id")).longValue());
        dto.setPatronId(patronId);
        return dto;
    }

    public ExtendedAttributeDto updateAttribute(Long patronId, Long attrId, ExtendedAttributeDto dto) {
        log.debug("Entering updateAttribute - {}, {}, {}", patronId, attrId, dto);
        jdbc.update("UPDATE borrower_attributes SET attribute = ? WHERE id = ? AND borrowernumber = ?",
            dto.getValue() != null ? dto.getValue() : dto.getAttribute(), attrId, patronId);
        dto.setExtendedAttributeId(attrId);
        dto.setPatronId(patronId);
        return dto;
    }

    public void deleteAttribute(Long patronId, Long attrId) {
        log.debug("Entering deleteAttribute - {}, {}", patronId, attrId);
        jdbc.update("DELETE FROM borrower_attributes WHERE id = ? AND borrowernumber = ?", attrId, patronId);
    }

    public void overwriteAttributes(Long patronId, List<ExtendedAttributeDto> dtos) {
        log.debug("Entering overwriteAttributes - {}, {}", patronId, dtos);
        jdbc.update("DELETE FROM borrower_attributes WHERE borrowernumber = ?", patronId);
        for (ExtendedAttributeDto dto : dtos) {
            insertAttribute(patronId, dto);
        }
    }

    // ── ILL Requests ───────────────────────────────────────────────────────────

    private static final RowMapper<IllRequestDto> ILL_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        IllRequestDto dto = new IllRequestDto();
        dto.setIllRequestId(rs.getLong("illrequest_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setBranchcode(rs.getString("branchcode"));
        dto.setStatus(rs.getString("status"));
        dto.setPlaced(rs.getString("placed"));
        dto.setUpdated(rs.getString("updated"));
        dto.setCompleted(rs.getString("completed"));
        dto.setMedium(rs.getString("medium"));
        dto.setAccessurl(rs.getString("accessurl"));
        dto.setCost(rs.getString("cost"));
        dto.setOrderid(rs.getString("orderid"));
        return dto;
    };

    public List<IllRequestDto> findIllRequestsByPatronId(Long patronId, Pageable pageable) {
        log.debug("Entering findIllRequestsByPatronId - {}, {}", patronId, pageable);
        return jdbc.query("""
            SELECT * FROM illrequests WHERE borrowernumber = ?
            ORDER BY placed DESC LIMIT ? OFFSET ?
            """, ILL_ROW_MAPPER, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    // ── Recalls ────────────────────────────────────────────────────────────────

    private static final RowMapper<RecallDto> RECALL_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        RecallDto dto = new RecallDto();
        dto.setRecallId(rs.getLong("recall_id"));
        dto.setPatronId(rs.getLong("patron_id"));
        dto.setBiblioId(rs.getLong("biblio_id"));
        dto.setItemId(rs.getObject("item_id", Long.class));
        dto.setBranchCode(rs.getString("pickup_library_id"));
        dto.setCreatedDate(rs.getObject("created_date", java.time.LocalDateTime.class));
        dto.setStatus(rs.getString("status"));
        dto.setExpirationDate(rs.getObject("expiration_date", java.time.LocalDateTime.class));
        return dto;
    };

    public List<RecallDto> findRecallsByPatronId(Long patronId) {
        log.debug("Entering findRecallsByPatronId - {}", patronId);
        return jdbc.query(
            "SELECT * FROM recalls WHERE patron_id = ? ORDER BY created_date DESC",
            RECALL_ROW_MAPPER, patronId);
    }

    // ── Virtual Shelves (Lists) ────────────────────────────────────────────────

    private static final RowMapper<VirtualShelfDto> SHELF_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        VirtualShelfDto dto = new VirtualShelfDto();
        dto.setShelfnumber(rs.getLong("shelfnumber"));
        dto.setShelfname(rs.getString("shelfname"));
        dto.setOwner(rs.getString("owner"));
        dto.setCategory(rs.getString("category"));
        dto.setSortfield(rs.getString("sortfield"));
        dto.setLastmodified(rs.getObject("lastmodified", java.time.LocalDateTime.class));
        dto.setAllow_change_from_owner(rs.getObject("allow_change_from_owner", Boolean.class));
        dto.setAllow_change_from_others(rs.getObject("allow_change_from_others", Boolean.class));
        return dto;
    };

    public List<VirtualShelfDto> findPublicShelves(Pageable pageable) {
        log.debug("Entering findPublicShelves - {}", pageable);
        return jdbc.query("""
            SELECT * FROM virtualshelves WHERE category = 2
            ORDER BY shelfname LIMIT ? OFFSET ?
            """, SHELF_ROW_MAPPER, pageable.getPageSize(), pageable.getOffset());
    }

    // ── Clubs ──────────────────────────────────────────────────────────────────

    public ClubHoldDto insertClubHold(Long clubId, ClubHoldDto dto) {
        log.debug("Entering insertClubHold - {}, {}", clubId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO club_holds (club_id, biblio_id, item_id, branchcode, date_created)
                VALUES (?, ?, ?, ?, NOW())
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, clubId);
            ps.setObject(2, dto.getBiblionumber() != null ? dto.getBiblionumber() : dto.getBiblioId());
            ps.setObject(3, dto.getItemId());
            ps.setString(4, dto.getBranchCode());
            return ps;
        }, kh);
        dto.setClubHoldId(((Number) kh.getKeys().get("id")).longValue());
        dto.setClubId(clubId);
        return dto;
    }
}




