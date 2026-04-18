package com.shailahir.koha.ill.repository;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Repository
@RequiredArgsConstructor
public class IllRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<IllRequestDto> REQUEST_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
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
        log.debug("Entering findAllRequests - {}, {}", query, pageable);
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
        log.debug("Entering findRequestById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM illrequests WHERE illrequest_id = ?", REQUEST_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public IllRequestDto insertRequest(IllRequestDto dto) {
        log.debug("Entering insertRequest - {}", dto);
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
        log.debug("Entering updateRequest - {}, {}", id, dto);
        jdbc.update("""
                UPDATE illrequests SET status=?, borrowernote=?, notesopac=?, notesstaff=?, updated=NOW()
                WHERE illrequest_id=?
                """, dto.getStatus(), dto.getBorrowerNote(), dto.getNotesOpac(), dto.getNotesStaff(), id);
        dto.setIllRequestId(id);
        return dto;
    }

    public void deleteRequest(Long id) {
        log.debug("Entering deleteRequest - {}", id);
        jdbc.update("DELETE FROM illrequests WHERE illrequest_id = ?", id);
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    private static final RowMapper<IllRequestCommentDto> COMMENT_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        IllRequestCommentDto dto = new IllRequestCommentDto();
        dto.setCommentId(rs.getLong("id"));
        dto.setIllRequestId(rs.getLong("illrequest_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setComment(rs.getString("comment"));
        dto.setTimestamp(rs.getObject("timestamp", java.time.LocalDateTime.class));
        return dto;
    };

    public List<IllRequestCommentDto> findCommentsByRequestId(Long requestId) {
        log.debug("Entering findCommentsByRequestId - {}", requestId);
        return jdbc.query(
                "SELECT * FROM illrequestattributes WHERE illrequest_id = ? AND type = 'COMMENT' ORDER BY id",
                COMMENT_MAPPER, requestId);
    }

    public IllRequestCommentDto insertComment(Long requestId, IllRequestCommentDto dto) {
        log.debug("Entering insertComment - {}, {}", requestId, dto);
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

    // ── Batches ───────────────────────────────────────────────────────────────

    private static final RowMapper<IllBatchDto> BATCH_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        IllBatchDto dto = new IllBatchDto();
        dto.setIllBatchId(rs.getLong("ill_batch_id"));
        dto.setName(rs.getString("name"));
        dto.setBackend(rs.getString("backend"));
        dto.setPatronId(rs.getObject("patron_id", Long.class));
        dto.setLibraryId(rs.getString("library_id"));
        dto.setStatusCode(rs.getString("status_code"));
        return dto;
    };

    public Page<IllBatchDto> findAllBatches(String query, Pageable pageable) {
        log.debug("Entering findAllBatches - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE name ILIKE ? OR backend ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM illbatches" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<IllBatchDto> list = jdbc.query(
                "SELECT * FROM illbatches" + where + " ORDER BY ill_batch_id DESC LIMIT ? OFFSET ?",
                BATCH_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<IllBatchDto> findBatchById(Long id) {
        log.debug("Entering findBatchById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM illbatches WHERE ill_batch_id = ?", BATCH_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public IllBatchDto insertBatch(IllBatchDto dto) {
        log.debug("Entering insertBatch - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO illbatches (name, backend, patron_id, library_id, status_code)
                    VALUES (?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getBackend());
            ps.setObject(3, dto.getPatronId());
            ps.setString(4, dto.getLibraryId());
            ps.setString(5, dto.getStatusCode());
            return ps;
        }, kh);
        dto.setIllBatchId(((Number) kh.getKeys().get("ill_batch_id")).longValue());
        return dto;
    }

    public IllBatchDto updateBatch(Long id, IllBatchDto dto) {
        log.debug("Entering updateBatch - {}, {}", id, dto);
        jdbc.update("""
                UPDATE illbatches SET name=?, backend=?, patron_id=?, library_id=?, status_code=?
                WHERE ill_batch_id=?
                """, dto.getName(), dto.getBackend(), dto.getPatronId(), dto.getLibraryId(), dto.getStatusCode(), id);
        dto.setIllBatchId(id);
        return dto;
    }

    public void deleteBatch(Long id) {
        log.debug("Entering deleteBatch - {}", id);
        jdbc.update("DELETE FROM illbatches WHERE ill_batch_id = ?", id);
    }

    // ── Batch Statuses ────────────────────────────────────────────────────────

    private static final RowMapper<IllBatchStatusDto> BATCH_STATUS_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        IllBatchStatusDto dto = new IllBatchStatusDto();
        dto.setId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setCode(rs.getString("code"));
        dto.setSystem(rs.getBoolean("is_system"));
        return dto;
    };

    public List<IllBatchStatusDto> findAllBatchStatuses() {
        log.debug("Entering findAllBatchStatuses");
        return jdbc.query("SELECT * FROM illbatch_statuses ORDER BY id", BATCH_STATUS_MAPPER);
    }

    public Optional<IllBatchStatusDto> findBatchStatusByCode(String code) {
        log.debug("Entering findBatchStatusByCode - {}", code);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM illbatch_statuses WHERE code = ?", BATCH_STATUS_MAPPER, code));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public IllBatchStatusDto insertBatchStatus(IllBatchStatusDto dto) {
        log.debug("Entering insertBatchStatus - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO illbatch_statuses (name, code, is_system)
                    VALUES (?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getCode());
            ps.setObject(3, dto.getSystem());
            return ps;
        }, kh);
        dto.setId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public IllBatchStatusDto updateBatchStatus(String code, IllBatchStatusDto dto) {
        log.debug("Entering updateBatchStatus - {}, {}", code, dto);
        jdbc.update("UPDATE illbatch_statuses SET name=?, is_system=? WHERE code=?",
                dto.getName(), dto.getSystem(), code);
        dto.setCode(code);
        return dto;
    }

    public void deleteBatchStatus(String code) {
        log.debug("Entering deleteBatchStatus - {}", code);
        jdbc.update("DELETE FROM illbatch_statuses WHERE code = ?", code);
    }

    // ── Backends ──────────────────────────────────────────────────────────────

    private static final RowMapper<IllBackendDto> BACKEND_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        IllBackendDto dto = new IllBackendDto();
        dto.setBackendId(rs.getString("backend"));
        dto.setName(rs.getString("backend"));
        dto.setRequestCount(rs.getLong("request_count"));
        return dto;
    };

    public List<IllBackendDto> findAllBackends() {
        log.debug("Entering findAllBackends");
        return jdbc.query("""
                SELECT backend, COUNT(*) AS request_count
                FROM illrequests
                WHERE backend IS NOT NULL AND backend <> ''
                GROUP BY backend
                ORDER BY backend
                """, BACKEND_MAPPER);
    }

    public Optional<IllBackendDto> findBackendById(String id) {
        log.debug("Entering findBackendById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject("""
                    SELECT backend, COUNT(*) AS request_count
                    FROM illrequests
                    WHERE backend = ?
                    GROUP BY backend
                    """, BACKEND_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    private static final RowMapper<IllUserDto> USER_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        IllUserDto dto = new IllUserDto();
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setCardnumber(rs.getString("cardnumber"));
        dto.setFirstname(rs.getString("firstname"));
        dto.setSurname(rs.getString("surname"));
        dto.setEmail(rs.getString("email"));
        dto.setLibraryId(rs.getString("branchcode"));
        return dto;
    };

    public Page<IllUserDto> findAllUsers(String query, Pageable pageable) {
        log.debug("Entering findAllUsers - {}, {}", query, pageable);
        String where = "";
        Object[] params = new Object[]{};
        if (query != null && !query.isBlank()) {
            where = " WHERE cardnumber ILIKE ? OR firstname ILIKE ? OR surname ILIKE ?";
            params = new Object[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"};
        }
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM borrowers" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<IllUserDto> list = jdbc.query(
                "SELECT borrowernumber, cardnumber, firstname, surname, email, branchcode FROM borrowers"
                        + where + " ORDER BY borrowernumber DESC LIMIT ? OFFSET ?",
                USER_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    private Object[] appendPaging(Object[] params, Pageable pageable) {
        log.debug("Entering appendPaging - {}, {}", params, pageable);
        Object[] pageParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pageParams, 0, params.length);
        pageParams[params.length] = pageable.getPageSize();
        pageParams[params.length + 1] = pageable.getOffset();
        return pageParams;
    }
}

