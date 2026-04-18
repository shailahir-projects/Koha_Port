package com.shailahir.koha.ill.repository;

import com.shailahir.koha.ill.dto.*;
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
 * Repository for ILL (Interlibrary Loan) data access.
 * Mirrors: ill/*, Koha/Illrequest.pm
 */
@Repository
@RequiredArgsConstructor
public class IllRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<IllRequestDto> REQUEST_MAPPER = (rs, rn) -> {
        IllRequestDto dto = new IllRequestDto();
        dto.setIllRequestId(rs.getLong("illrequest_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setBranchCode(rs.getString("branchcode"));
        dto.setStatus(rs.getString("status"));
        dto.setType(rs.getString("backend"));
        dto.setBorrowerNote(rs.getString("borrowernote"));
        dto.setNotesOpac(rs.getString("notesopac"));
        dto.setNotesStaff(rs.getString("notesstaff"));
        dto.setPlaced(rs.getObject("placed", java.time.LocalDateTime.class));
        dto.setUpdated(rs.getObject("updated", java.time.LocalDateTime.class));
        dto.setBiblio(rs.getObject("biblio_id", Long.class));
        dto.setExternalId(rs.getString("external_id"));
        dto.setPrice(rs.getString("price"));
        dto.setCurrency(rs.getString("price_paid"));
        return dto;
    };

    public Page<IllRequestDto> findAllRequests(String query, Pageable pageable) {
        String where = (query != null && !query.isBlank()) ? " WHERE status ILIKE ? OR backend ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM illrequests" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<IllRequestDto> list = jdbc.query(
                "SELECT * FROM illrequests" + where + " ORDER BY illrequest_id DESC LIMIT ? OFFSET ?",
                REQUEST_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<IllRequestDto> findRequestById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM illrequests WHERE illrequest_id = ?", REQUEST_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public IllRequestDto insertRequest(IllRequestDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO illrequests (borrowernumber, branchcode, status, backend, borrowernote,
                        notesopac, notesstaff, placed, updated, biblio_id, external_id, price)
                    VALUES (?,?,?,?,?,?,?,NOW(),NOW(),?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, dto.getPatronId());
            ps.setString(2, dto.getBranchCode());
            ps.setString(3, dto.getStatus());
            ps.setString(4, dto.getType());
            ps.setString(5, dto.getBorrowerNote());
            ps.setString(6, dto.getNotesOpac());
            ps.setString(7, dto.getNotesStaff());
            ps.setObject(8, dto.getBiblio());
            ps.setString(9, dto.getExternalId());
            ps.setString(10, dto.getPrice());
            return ps;
        }, kh);
        dto.setIllRequestId(((Number) kh.getKeys().get("illrequest_id")).longValue());
        return dto;
    }

    public IllRequestDto updateRequest(Long id, IllRequestDto dto) {
        jdbc.update("""
                UPDATE illrequests SET status=?, borrowernote=?, notesopac=?, notesstaff=?, updated=NOW()
                WHERE illrequest_id=?
                """, dto.getStatus(), dto.getBorrowerNote(), dto.getNotesOpac(), dto.getNotesStaff(), id);
        dto.setIllRequestId(id);
        return dto;
    }

    public void deleteRequest(Long id) {
        jdbc.update("DELETE FROM illrequests WHERE illrequest_id = ?", id);
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    private static final RowMapper<IllRequestCommentDto> COMMENT_MAPPER = (rs, rn) -> {
        IllRequestCommentDto dto = new IllRequestCommentDto();
        dto.setCommentId(rs.getLong("id"));
        dto.setIllRequestId(rs.getLong("illrequest_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setComment(rs.getString("comment"));
        dto.setTimestamp(rs.getObject("timestamp", java.time.LocalDateTime.class));
        return dto;
    };

    public List<IllRequestCommentDto> findCommentsByRequestId(Long requestId) {
        return jdbc.query(
                "SELECT * FROM illrequestattributes WHERE illrequest_id = ? AND type = 'COMMENT' ORDER BY id",
                COMMENT_MAPPER, requestId);
    }

    public IllRequestCommentDto insertComment(Long requestId, IllRequestCommentDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO illcomments (illrequest_id, borrowernumber, comment, timestamp)
                    VALUES (?,?,?,NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, requestId);
            ps.setObject(2, dto.getPatronId());
            ps.setString(3, dto.getComment());
            return ps;
        }, kh);
        dto.setCommentId(((Number) kh.getKeys().get("id")).longValue());
        dto.setIllRequestId(requestId);
        return dto;
    }

    private Object[] appendPaging(Object[] params, Pageable pageable) {
        Object[] pageParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pageParams, 0, params.length);
        pageParams[params.length] = pageable.getPageSize();
        pageParams[params.length + 1] = pageable.getOffset();
        return pageParams;
    }
}

