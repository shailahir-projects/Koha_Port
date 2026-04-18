package com.shailahir.koha.acquisitions.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC repository for invoice file operations.
 * Mirrors Koha::Misc::Files (tabletag='aqinvoices'):
 * GetFilesInfo(), GetFile(), AddFile(), DelFile().
 *
 * The underlying table is misc_files with columns:
 *   file_id, tabletag, recordid, file_name, file_type,
 *   file_description, file_content, date_uploaded
 */
@Repository
@RequiredArgsConstructor
public class InvoiceFilesRepository {

    private final JdbcTemplate jdbc;

    // ── GetFilesInfo ───────────────────────────────────────────────────────────

    /**
     * Returns metadata for all files attached to an invoice (no content).
     * Mirrors Koha::Misc::Files->GetFilesInfo().
     */
    public List<Map<String, Object>> getFilesInfo(Long invoiceid) {
        return jdbc.queryForList("""
                SELECT file_id, tabletag, recordid, file_name, file_type,
                       file_description, date_uploaded
                  FROM misc_files
                 WHERE tabletag = 'aqinvoices'
                   AND recordid = ?
                 ORDER BY file_id
                """, invoiceid);
    }

    // ── GetFile ────────────────────────────────────────────────────────────────

    /**
     * Returns a single file including its binary content.
     * Mirrors Koha::Misc::Files->GetFile(id => $file_id).
     */
    public Optional<Map<String, Object>> getFile(Long fileId, Long invoiceid) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("""
                            SELECT file_id, tabletag, recordid, file_name, file_type,
                                   file_description, file_content, date_uploaded
                              FROM misc_files
                             WHERE file_id  = ?
                               AND tabletag = 'aqinvoices'
                               AND recordid = ?
                            """, fileId, invoiceid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── AddFile ────────────────────────────────────────────────────────────────

    /**
     * Stores a new file attached to the invoice.
     * Mirrors Koha::Misc::Files->AddFile(name, type, content, description).
     *
     * @return the new file_id
     */
    public Long addFile(Long invoiceid, String fileName, String fileType,
                        byte[] fileContent, String description) {
        String sql = """
                INSERT INTO misc_files
                    (tabletag, recordid, file_name, file_type, file_content,
                     file_description, date_uploaded)
                VALUES ('aqinvoices', ?, ?, ?, ?, ?, NOW())
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, invoiceid);
            ps.setString(2, fileName);
            ps.setString(3, fileType);
            ps.setBytes(4, fileContent);
            ps.setString(5, description);
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("file_id")).longValue();
    }

    // ── DelFile ────────────────────────────────────────────────────────────────

    /**
     * Deletes a file by id (scoped to this invoice for safety).
     * Mirrors Koha::Misc::Files->DelFile(id => $file_id).
     */
    public void deleteFile(Long fileId, Long invoiceid) {
        jdbc.update("""
                DELETE FROM misc_files
                 WHERE file_id  = ?
                   AND tabletag = 'aqinvoices'
                   AND recordid = ?
                """, fileId, invoiceid);
    }
}

