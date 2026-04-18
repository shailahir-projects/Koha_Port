package com.shailahir.koha.circulation.repository;

import com.shailahir.koha.circulation.dto.*;
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
import java.util.Map;
import java.util.Optional;

/**
 * Repository for circulation data access.
 * Mirrors: circ/circulation.pl, circ/returns.pl, circ/renew.pl,
 *          circ/bookings.pl, circ/smart-rules.pl, circ/rotas.pl,
 *          circ/claim-returned.pl, and others.
 */
@Repository
@RequiredArgsConstructor
public class CirculationRepository {

    private final JdbcTemplate jdbc;

    // ── Checkouts ──────────────────────────────────────────────────────────────

    private static final RowMapper<CheckoutDto> CHECKOUT_MAPPER = (rs, rn) -> {
        CheckoutDto dto = new CheckoutDto();
        dto.setCheckoutId(rs.getLong("issue_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setItemId(rs.getLong("itemnumber"));
        dto.setIssueDate(rs.getObject("issuedate", java.time.LocalDateTime.class));
        dto.setDueDate(rs.getObject("date_due", java.time.LocalDateTime.class));
        dto.setBranchCode(rs.getString("branchcode"));
        dto.setRenewals(rs.getInt("renewals_count"));
        dto.setAutoRenew(rs.getObject("auto_renew", Boolean.class));
        dto.setAutoRenewError(rs.getString("auto_renew_error"));
        return dto;
    };

    public Page<CheckoutDto> findAllCheckouts(String query, Pageable pageable) {
        String where = query != null && !query.isBlank() ? " WHERE borrowernumber::text ILIKE ?" : "";
        Object[] params = query != null && !query.isBlank()
            ? new Object[]{"%" + query + "%"} : new Object[]{};
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM issues" + where, Integer.class, params);
        Object[] pageParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pageParams, 0, params.length);
        pageParams[params.length] = pageable.getPageSize();
        pageParams[params.length + 1] = pageable.getOffset();
        List<CheckoutDto> list = jdbc.query(
            "SELECT * FROM issues" + where + " ORDER BY issuedate DESC LIMIT ? OFFSET ?",
            CHECKOUT_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<CheckoutDto> findCheckoutById(Long checkoutId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM issues WHERE issue_id = ?", CHECKOUT_MAPPER, checkoutId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public CheckoutDto insertCheckout(CheckoutDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO issues (borrowernumber, itemnumber, issuedate, date_due, branchcode,
                    renewals_count, auto_renew)
                VALUES (?, ?, NOW(), ?, ?, 0, ?)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, dto.getPatronId());
            ps.setLong(2, dto.getItemId());
            ps.setObject(3, dto.getDueDate());
            ps.setString(4, dto.getBranchCode());
            ps.setObject(5, dto.getAutoRenew());
            return ps;
        }, kh);
        dto.setCheckoutId(((Number) kh.getKeys().get("issue_id")).longValue());
        return dto;
    }

    public CheckoutDto renewCheckout(Long checkoutId) {
        jdbc.update("""
            UPDATE issues SET renewals_count = renewals_count + 1,
                date_due = date_due + INTERVAL '2 weeks'
            WHERE issue_id = ?
            """, checkoutId);
        return findCheckoutById(checkoutId).orElseThrow();
    }

    public List<CheckoutDto> getRenewals(Long checkoutId) {
        // Return renewal history from old_issues
        return jdbc.query("""
            SELECT * FROM old_issues WHERE issue_id = ? ORDER BY returndate DESC
            """, CHECKOUT_MAPPER, checkoutId);
    }

    public Map<String, Object> checkoutAvailability(Long patronId, Long itemId) {
        // Check if patron can check out the item
        Integer existingCheckouts = jdbc.queryForObject(
            "SELECT COUNT(*) FROM issues WHERE borrowernumber = ? AND itemnumber = ?",
            Integer.class, patronId, itemId);
        return Map.of(
            "patron_id", patronId,
            "item_id", itemId,
            "available", existingCheckouts == 0,
            "reason", existingCheckouts > 0 ? "already_checked_out" : null
        );
    }

    // ── Bookings ───────────────────────────────────────────────────────────────

    private static final RowMapper<BookingDto> BOOKING_MAPPER = (rs, rn) -> {
        BookingDto dto = new BookingDto();
        dto.setBookingId(rs.getLong("booking_id"));
        dto.setBiblioId(rs.getLong("biblio_id"));
        dto.setItemId(rs.getObject("item_id", Long.class));
        dto.setPatronId(rs.getLong("patron_id"));
        dto.setPickupLibraryId(rs.getString("pickup_library_id"));
        dto.setStartDate(rs.getObject("start_date", java.time.LocalDateTime.class));
        dto.setEndDate(rs.getObject("end_date", java.time.LocalDateTime.class));
        dto.setStatus(rs.getString("status"));
        return dto;
    };

    public Page<BookingDto> findAllBookings(String query, Pageable pageable) {
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM bookings", Integer.class);
        List<BookingDto> list = jdbc.query(
            "SELECT * FROM bookings ORDER BY start_date LIMIT ? OFFSET ?",
            BOOKING_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<BookingDto> findBookingById(Long bookingId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM bookings WHERE booking_id = ?", BOOKING_MAPPER, bookingId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public BookingDto insertBooking(BookingDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO bookings (biblio_id, item_id, patron_id, pickup_library_id, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, ?, 'new')
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, dto.getBiblioId());
            ps.setObject(2, dto.getItemId());
            ps.setLong(3, dto.getPatronId());
            ps.setString(4, dto.getPickupLibraryId());
            ps.setObject(5, dto.getStartDate());
            ps.setObject(6, dto.getEndDate());
            return ps;
        }, kh);
        dto.setBookingId(((Number) kh.getKeys().get("booking_id")).longValue());
        return dto;
    }

    public BookingDto updateBooking(Long bookingId, BookingDto dto) {
        jdbc.update("""
            UPDATE bookings SET pickup_library_id=?, start_date=?, end_date=?, status=?
            WHERE booking_id=?
            """, dto.getPickupLibraryId(), dto.getStartDate(), dto.getEndDate(), dto.getStatus(), bookingId);
        dto.setBookingId(bookingId);
        return dto;
    }

    public void deleteBooking(Long bookingId) {
        jdbc.update("DELETE FROM bookings WHERE booking_id = ?", bookingId);
    }

    // ── Circulation Rules ──────────────────────────────────────────────────────

    private static final RowMapper<CirculationRuleDto> RULE_MAPPER = (rs, rn) -> {
        CirculationRuleDto dto = new CirculationRuleDto();
        dto.setBranchcode(rs.getString("branchcode"));
        dto.setCategorycode(rs.getString("categorycode"));
        dto.setItemtype(rs.getString("itemtype"));
        // Rules are stored as individual columns; aggregate key rules
        dto.setRules(Map.of(
            "maxissueqty", rs.getObject("maxissueqty"),
            "maxreserveqty", rs.getObject("maxreserveqty"),
            "issuelength", rs.getObject("issuelength"),
            "lengthunit", rs.getObject("lengthunit"),
            "renewalsallowed", rs.getObject("renewalsallowed"),
            "renewalperiod", rs.getObject("renewalperiod"),
            "fine", rs.getObject("fine"),
            "chargeperiod", rs.getObject("chargeperiod")
        ));
        return dto;
    };

    public List<CirculationRuleDto> findAllCirculationRules() {
        return jdbc.query("SELECT * FROM issuingrules ORDER BY branchcode, categorycode, itemtype", RULE_MAPPER);
    }

    public void upsertCirculationRule(CirculationRuleDto rule) {
        // Simplified upsert
        jdbc.update("""
            INSERT INTO issuingrules (branchcode, categorycode, itemtype)
            VALUES (?, ?, ?)
            ON CONFLICT (branchcode, categorycode, itemtype) DO NOTHING
            """, rule.getBranchcode(), rule.getCategorycode(), rule.getItemtype());
    }

    // ── Return Claims ──────────────────────────────────────────────────────────

    private static final RowMapper<ReturnClaimDto> CLAIM_MAPPER = (rs, rn) -> {
        ReturnClaimDto dto = new ReturnClaimDto();
        dto.setClaimId(rs.getLong("id"));
        dto.setCheckoutId(rs.getObject("issue_id", Long.class));
        dto.setItemId(rs.getLong("itemnumber"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setNotes(rs.getString("notes"));
        dto.setCreated(rs.getObject("created_on", java.time.LocalDateTime.class));
        dto.setResolution(rs.getString("resolution"));
        dto.setResolvedOn(rs.getObject("resolved_on", java.time.LocalDateTime.class));
        return dto;
    };

    public ReturnClaimDto insertClaim(ReturnClaimDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO return_claims (issue_id, itemnumber, borrowernumber, notes)
                VALUES (?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, dto.getCheckoutId());
            ps.setLong(2, dto.getItemId());
            ps.setLong(3, dto.getPatronId());
            ps.setString(4, dto.getNotes());
            return ps;
        }, kh);
        dto.setClaimId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public Optional<ReturnClaimDto> findClaimById(Long claimId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM return_claims WHERE id = ?", CLAIM_MAPPER, claimId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public ReturnClaimDto updateClaimNotes(Long claimId, String notes) {
        jdbc.update("UPDATE return_claims SET notes = ? WHERE id = ?", notes, claimId);
        return findClaimById(claimId).orElseThrow();
    }

    public void deleteClaim(Long claimId) {
        jdbc.update("DELETE FROM return_claims WHERE id = ?", claimId);
    }

    public ReturnClaimDto resolveClaim(Long claimId, String resolution) {
        jdbc.update("UPDATE return_claims SET resolution = ?, resolved_on = NOW() WHERE id = ?", resolution, claimId);
        return findClaimById(claimId).orElseThrow();
    }

    // ── Rotas ──────────────────────────────────────────────────────────────────

    private static final RowMapper<RotaDto> ROTA_MAPPER = (rs, rn) -> {
        RotaDto dto = new RotaDto();
        dto.setRotaId(rs.getLong("rota_id"));
        dto.setTitle(rs.getString("title"));
        dto.setDescription(rs.getString("description"));
        dto.setActive(rs.getBoolean("active"));
        dto.setCyclical(rs.getBoolean("cyclical"));
        return dto;
    };

    private static final RowMapper<RotaStageDto> STAGE_MAPPER = (rs, rn) -> {
        RotaStageDto dto = new RotaStageDto();
        dto.setStageId(rs.getLong("stage_id"));
        dto.setRotaId(rs.getLong("rota_id"));
        dto.setBranchCode(rs.getString("branchcode"));
        dto.setDuration(rs.getObject("duration", Integer.class));
        dto.setPosition(rs.getObject("position", Integer.class));
        return dto;
    };

    public List<RotaDto> findAllRotas() {
        List<RotaDto> rotas = jdbc.query("SELECT * FROM stockrotation_rotas ORDER BY rota_id", ROTA_MAPPER);
        for (RotaDto rota : rotas) {
            rota.setStages(findStagesByRotaId(rota.getRotaId()));
        }
        return rotas;
    }

    public Optional<RotaDto> findRotaById(Long rotaId) {
        try {
            RotaDto dto = jdbc.queryForObject(
                "SELECT * FROM stockrotation_rotas WHERE rota_id = ?", ROTA_MAPPER, rotaId);
            if (dto != null) {
                dto.setStages(findStagesByRotaId(rotaId));
            }
            return Optional.ofNullable(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<RotaStageDto> findStagesByRotaId(Long rotaId) {
        return jdbc.query(
            "SELECT * FROM stockrotation_stages WHERE rota_id = ? ORDER BY position",
            STAGE_MAPPER, rotaId);
    }

    public RotaDto insertRota(RotaDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO stockrotation_rotas (title, description, active, cyclical) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getDescription());
            ps.setObject(3, dto.getActive());
            ps.setObject(4, dto.getCyclical());
            return ps;
        }, kh);
        dto.setRotaId(((Number) kh.getKeys().get("rota_id")).longValue());
        return dto;
    }

    public RotaDto updateRota(Long rotaId, RotaDto dto) {
        jdbc.update("UPDATE stockrotation_rotas SET title=?, description=?, active=?, cyclical=? WHERE rota_id=?",
            dto.getTitle(), dto.getDescription(), dto.getActive(), dto.getCyclical(), rotaId);
        dto.setRotaId(rotaId);
        return dto;
    }

    public void deleteRota(Long rotaId) {
        jdbc.update("DELETE FROM stockrotation_rotas WHERE rota_id = ?", rotaId);
    }

    public void moveStage(Long rotaId, Long stageId, Integer position) {
        jdbc.update("UPDATE stockrotation_stages SET position = ? WHERE stage_id = ? AND rota_id = ?",
            position, stageId, rotaId);
    }
}

