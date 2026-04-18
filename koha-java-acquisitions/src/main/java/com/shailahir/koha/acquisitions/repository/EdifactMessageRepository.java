package com.shailahir.koha.acquisitions.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.EdifactMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC repository for EDIFACT message operations.
 * Mirrors the EdifactMessage resultset operations in edifactmsgs.pl.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EdifactMessageRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<EdifactMessageDto> MSG_MAPPER = (rs, rn) -> EdifactMessageDto.builder()
            .id(rs.getLong("id"))
            .messageType(rs.getString("message_type"))
            .transferDate(rs.getTimestamp("transfer_date") != null
                    ? rs.getTimestamp("transfer_date").toLocalDateTime() : null)
            .vendorId(rs.getObject("vendor_id") != null ? rs.getLong("vendor_id") : null)
            .basketno(rs.getObject("basketno") != null ? rs.getLong("basketno") : null)
            .status(rs.getString("status"))
            .filename(rs.getString("filename"))
            .deleted(rs.getBoolean("deleted"))
            .rawMsg(rs.getString("raw_msg"))
            .build();

    // ── List ───────────────────────────────────────────────────────────────────

    /** Returns all non-deleted EDIFACT messages, newest first. */
    public List<EdifactMessageDto> findAll() {
        log.debug("Entering findAll");
        return jdbc.query("""
                SELECT m.*, v.name AS vendor_name
                  FROM edifact_messages m
                  LEFT JOIN aqbooksellers v ON v.id = m.vendor_id
                 WHERE m.deleted = 0 OR m.deleted IS NULL
                 ORDER BY m.transfer_date DESC
                """,
                (rs, rn) -> {
                    EdifactMessageDto dto = MSG_MAPPER.mapRow(rs, rn);
                    dto.setVendorName(rs.getString("vendor_name"));
                    return dto;
                });
    }

    // ── Find by id ─────────────────────────────────────────────────────────────

    public Optional<EdifactMessageDto> findById(Long id) {
        log.debug("Entering findById - {}", id);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT * FROM edifact_messages WHERE id = ?",
                            MSG_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Soft-delete (op=cud-delete) ────────────────────────────────────────────

    /**
     * Soft-deletes an EDIFACT message by setting deleted = 1.
     * Mirrors: $msg->deleted(1); $msg->update;
     */
    public void softDelete(Long id) {
        log.debug("Entering softDelete - {}", id);
        jdbc.update("UPDATE edifact_messages SET deleted = 1 WHERE id = ?", id);
    }

    // ── Invoice import (op=import) ─────────────────────────────────────────────

    /**
     * Marks an EDIFACT INVOICE message as processed by setting status = 'processed'.
     * The full process_invoice() logic in Koha::EDI creates aqinvoices rows and
     * updates order quantities — that belongs in an EDI microservice.
     * This method records that the import was triggered and updates the message status.
     * Mirrors the status change that process_invoice() applies.
     */
    public void markAsProcessed(Long id) {
        log.debug("Entering markAsProcessed - {}", id);
        jdbc.update("UPDATE edifact_messages SET status = 'processed' WHERE id = ?", id);
    }

    /**
     * Returns the raw EDI message content needed by process_invoice().
     */
    public Optional<String> getRawMsg(Long id) {
        log.debug("Entering getRawMsg - {}", id);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT raw_msg FROM edifact_messages WHERE id = ?",
                            String.class, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

