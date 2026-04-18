package com.shailahir.koha.holds.repository;

import com.shailahir.koha.holds.dto.HoldDto;
import com.shailahir.koha.holds.dto.LibraryDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for holds (reserves) data access.
 * Mirrors: reserve/placerequest.pl, reserve/cancelrequest.pl,
 *          reserve/modrequest.pl, reserve/request.pl, circ/waitingreserves.pl
 */
@Repository
@RequiredArgsConstructor
public class HoldsRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<HoldDto> HOLD_MAPPER = (rs, rn) -> {
        HoldDto dto = new HoldDto();
        dto.setHoldId(rs.getLong("reserve_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setBiblioId(rs.getLong("biblionumber"));
        dto.setItemId(rs.getObject("itemnumber", Long.class));
        dto.setBranchCode(rs.getString("branchcode"));
        dto.setStatus(rs.getString("found"));
        dto.setPriority(rs.getInt("priority"));
        dto.setReserveDate(rs.getObject("reservedate", LocalDateTime.class));
        dto.setExpirationDate(rs.getObject("expirationdate", LocalDateTime.class));
        dto.setItemtype(rs.getString("itemtype"));
        dto.setNotes(rs.getString("notes"));
        dto.setLowestPriority(rs.getBoolean("lowestPriority"));
        dto.setSuspend(rs.getBoolean("suspend"));
        dto.setSuspendUntil(rs.getObject("suspend_until", LocalDateTime.class));
        dto.setPickupLibraryId(rs.getString("branchcode"));
        return dto;
    };

    public Page<HoldDto> findAll(String query, Pageable pageable) {
        String where = query != null && !query.isBlank() ? " WHERE borrowernumber::text = ?" : "";
        Object[] params = query != null && !query.isBlank() ? new Object[]{query} : new Object[]{};
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM reserves" + where, Integer.class, params);
        Object[] pp = new Object[params.length + 2];
        System.arraycopy(params, 0, pp, 0, params.length);
        pp[params.length] = pageable.getPageSize();
        pp[params.length + 1] = pageable.getOffset();
        List<HoldDto> list = jdbc.query(
            "SELECT * FROM reserves" + where + " ORDER BY priority LIMIT ? OFFSET ?",
            HOLD_MAPPER, pp);
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<HoldDto> findById(Long holdId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM reserves WHERE reserve_id = ?", HOLD_MAPPER, holdId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public HoldDto insert(HoldDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO reserves (borrowernumber, biblionumber, itemnumber, branchcode,
                    priority, reservedate, expirationdate, itemtype, notes, suspend, suspend_until)
                VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, dto.getPatronId());
            ps.setLong(2, dto.getBiblioId());
            ps.setObject(3, dto.getItemId());
            ps.setString(4, dto.getBranchCode() != null ? dto.getBranchCode() : dto.getPickupLibraryId());
            ps.setInt(5, dto.getPriority() != null ? dto.getPriority() : 1);
            ps.setObject(6, dto.getExpirationDate());
            ps.setString(7, dto.getItemtype());
            ps.setString(8, dto.getNotes());
            ps.setBoolean(9, dto.getSuspend() != null && dto.getSuspend());
            ps.setObject(10, dto.getSuspendUntil());
            return ps;
        }, kh);
        dto.setHoldId(((Number) kh.getKeys().get("reserve_id")).longValue());
        return dto;
    }

    public HoldDto update(Long holdId, HoldDto dto) {
        jdbc.update("""
            UPDATE reserves SET branchcode=?, priority=?, expirationdate=?, notes=?,
                suspend=?, suspend_until=?, lowestPriority=?
            WHERE reserve_id=?
            """,
            dto.getPickupLibraryId() != null ? dto.getPickupLibraryId() : dto.getBranchCode(),
            dto.getPriority(), dto.getExpirationDate(), dto.getNotes(),
            dto.getSuspend() != null && dto.getSuspend(), dto.getSuspendUntil(),
            dto.getLowestPriority() != null && dto.getLowestPriority(),
            holdId);
        dto.setHoldId(holdId);
        return dto;
    }

    public void delete(Long holdId) {
        jdbc.update("DELETE FROM reserves WHERE reserve_id = ?", holdId);
    }

    public void updatePriority(Long holdId, Integer priority) {
        jdbc.update("UPDATE reserves SET priority = ? WHERE reserve_id = ?", priority, holdId);
    }

    public void suspend(Long holdId, LocalDateTime suspendUntil) {
        jdbc.update("UPDATE reserves SET suspend = true, suspend_until = ? WHERE reserve_id = ?", suspendUntil, holdId);
    }

    public void resume(Long holdId) {
        jdbc.update("UPDATE reserves SET suspend = false, suspend_until = NULL WHERE reserve_id = ?", holdId);
    }

    public void toggleLowestPriority(Long holdId) {
        jdbc.update("UPDATE reserves SET lowestPriority = NOT lowestPriority WHERE reserve_id = ?", holdId);
    }

    public void updatePickupLocation(Long holdId, String pickupLibraryId) {
        jdbc.update("UPDATE reserves SET branchcode = ? WHERE reserve_id = ?", pickupLibraryId, holdId);
    }

    public List<LibraryDto> findPickupLocations() {
        return jdbc.query(
            "SELECT branchcode, branchname FROM branches WHERE pickup_location = true ORDER BY branchname",
            (rs, rn) -> LibraryDto.builder()
                .libraryId(rs.getString("branchcode"))
                .name(rs.getString("branchname"))
                .build());
    }

    public void cancelArticleRequest(Long articleRequestId) {
        jdbc.update("UPDATE article_requests SET status = 'CANCELLED' WHERE id = ?", articleRequestId);
    }

    public void suspendBulk(List<Long> holdIds, LocalDateTime suspendUntil) {
        for (Long holdId : holdIds) {
            suspend(holdId, suspendUntil);
        }
    }

    public void cancelBulk(List<Long> holdIds) {
        for (Long holdId : holdIds) {
            delete(holdId);
        }
    }
}

