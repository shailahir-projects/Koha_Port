package com.shailahir.koha.acquisitions.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.ImportBatchDto;
import com.shailahir.koha.acquisitions.dto.ImportBiblioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * JDBC repository for marc_import_batches and marc_import_records tables.
 * Mirrors the DB queries performed by Koha::MarcOrder and C4::ImportBatch.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MarcImportRepository {

    private final JdbcTemplate jdbc;

    // ── Row mappers ────────────────────────────────────────────────────────────

    private final RowMapper<ImportBatchDto> BATCH_MAPPER = (rs, rn) -> ImportBatchDto.builder()
            .importBatchId(rs.getLong("import_batch_id"))
            .matcherId(rs.getObject("matcher_id") != null ? rs.getLong("matcher_id") : null)
            .templateId(rs.getObject("template_id") != null ? rs.getLong("template_id") : null)
            .branchcode(rs.getString("branchcode"))
            .numRecords(rs.getInt("num_records"))
            .numItems(rs.getInt("num_items"))
            .uploadTimestamp(rs.getTimestamp("upload_timestamp") != null
                    ? rs.getTimestamp("upload_timestamp").toLocalDateTime() : null)
            .overlayAction(rs.getString("overlay_action"))
            .nomatchAction(rs.getString("nomatch_action"))
            .itemAction(rs.getString("item_action"))
            .importStatus(rs.getString("import_status"))
            .batchType(rs.getString("batch_type"))
            .recordType(rs.getString("record_type"))
            .fileName(rs.getString("file_name"))
            .comments(rs.getString("comments"))
            .build();

    private final RowMapper<ImportBiblioDto> BIBLIO_MAPPER = (rs, rn) -> ImportBiblioDto.builder()
            .importRecordId(rs.getLong("import_record_id"))
            .importBatchId(rs.getLong("import_batch_id"))
            .status(rs.getString("status"))
            .overlayStatus(rs.getString("overlay_status"))
            .matchedBiblionumber(rs.getObject("matched_biblionumber") != null
                    ? rs.getLong("matched_biblionumber") : null)
            .title(rs.getString("title"))
            .author(rs.getString("author"))
            .isbn(rs.getString("isbn"))
            .issn(rs.getString("issn"))
            .publishercode(rs.getString("publishercode"))
            .publicationyear(rs.getString("publicationyear"))
            .alreadyExists(rs.getObject("matched_biblionumber") != null)
            .build();

    // ── Import batch queries ───────────────────────────────────────────────────

    /**
     * Returns all import batches of type 'batch' that are in a usable state
     * (not 'imported' or 'reverted'), ordered by upload time descending.
     * Mirrors Koha::MarcOrder->import_batches_list().
     */
    public List<ImportBatchDto> findImportBatches() {
        log.debug("Entering findImportBatches");
        String sql = """
                SELECT *
                  FROM marc_import_batches
                 WHERE batch_type = 'batch'
                   AND import_status NOT IN ('imported', 'reverted')
                 ORDER BY upload_timestamp DESC
                """;
        return jdbc.query(sql, BATCH_MAPPER);
    }

    public Optional<ImportBatchDto> findBatchById(Long importBatchId) {
        log.debug("Entering findBatchById - {}", importBatchId);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM marc_import_batches WHERE import_batch_id = ?",
                            BATCH_MAPPER, importBatchId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Import record (biblio) queries ─────────────────────────────────────────

    /**
     * Returns all import records for the given batch joined with bibliographic
     * title/author/isbn data from marc_import_biblios.
     * Mirrors Koha::MarcOrder->import_biblios_list().
     */
    public List<ImportBiblioDto> findBibliosByBatchId(Long importBatchId) {
        log.debug("Entering findBibliosByBatchId - {}", importBatchId);
        String sql = """
                SELECT r.import_record_id,
                       r.import_batch_id,
                       r.status,
                       r.overlay_status,
                       r.matched_biblionumber,
                       b.title,
                       b.author,
                       b.isbn,
                       b.issn,
                       b.publishercode,
                       b.publicationyear
                  FROM marc_import_records r
                  LEFT JOIN marc_import_biblios b ON b.import_record_id = r.import_record_id
                 WHERE r.import_batch_id = ?
                 ORDER BY r.import_record_id
                """;
        return jdbc.query(sql, BIBLIO_MAPPER, importBatchId);
    }

    public Optional<ImportBiblioDto> findImportRecord(Long importRecordId) {
        log.debug("Entering findImportRecord - {}", importRecordId);
        String sql = """
                SELECT r.import_record_id,
                       r.import_batch_id,
                       r.status,
                       r.overlay_status,
                       r.matched_biblionumber,
                       b.title,
                       b.author,
                       b.isbn,
                       b.issn,
                       b.publishercode,
                       b.publicationyear
                  FROM marc_import_records r
                  LEFT JOIN marc_import_biblios b ON b.import_record_id = r.import_record_id
                 WHERE r.import_record_id = ?
                """;
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, BIBLIO_MAPPER, importRecordId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Status updates ─────────────────────────────────────────────────────────

    /**
     * Mark a single import record as 'imported'.
     */
    public void markRecordImported(Long importRecordId) {
        log.debug("Entering markRecordImported - {}", importRecordId);
        jdbc.update("UPDATE marc_import_records SET status = 'imported' WHERE import_record_id = ?",
                importRecordId);
    }

    /**
     * Update the import batch status (e.g. to 'imported').
     * Mirrors SetImportBatchStatus() from C4::ImportBatch.
     */
    public void updateBatchStatus(Long importBatchId, String status) {
        log.debug("Entering updateBatchStatus - {}, {}", importBatchId, status);
        jdbc.update("UPDATE marc_import_batches SET import_status = ? WHERE import_batch_id = ?",
                status, importBatchId);
    }

    /**
     * Count records in the batch with the given status.
     */
    public int countRecordsByStatus(Long importBatchId, String status) {
        log.debug("Entering countRecordsByStatus - {}, {}", importBatchId, status);
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM marc_import_records WHERE import_batch_id = ? AND status = ?",
                Integer.class, importBatchId, status);
        return count != null ? count : 0;
    }

    /**
     * Count all records in the batch.
     */
    public int countAllRecords(Long importBatchId) {
        log.debug("Entering countAllRecords - {}", importBatchId);
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM marc_import_records WHERE import_batch_id = ?",
                Integer.class, importBatchId);
        return count != null ? count : 0;
    }

    // ── Matcher queries ────────────────────────────────────────────────────────

    /**
     * Returns the matcher code and description for a given matcher_id.
     * Returns null when no matcher is configured.
     */
    public Optional<String[]> findMatcherInfo(Long matcherId) {
        log.debug("Entering findMatcherInfo - {}", matcherId);
        if (matcherId == null) return Optional.empty();
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT matcher_code, description FROM marc_matchers WHERE matcher_id = ?",
                            (rs, rn) -> new String[]{rs.getString("matcher_code"), rs.getString("description")},
                            matcherId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Look for a matching existing biblio using ISBN (simple matcher).
     * Returns the biblionumber of the first match.
     */
    public Optional<Long> findMatchingBiblionumber(String isbn, String title, String author) {
        log.debug("Entering findMatchingBiblionumber - {}, {}, {}", isbn, title, author);
        if (isbn != null && !isbn.isBlank()) {
            try {
                Long bn = jdbc.queryForObject(
                        "SELECT biblionumber FROM biblioitems WHERE isbn LIKE ? LIMIT 1",
                        Long.class, isbn.strip() + "%");
                if (bn != null) return Optional.of(bn);
            } catch (EmptyResultDataAccessException ignored) {
            }
        }
        if (title != null && !title.isBlank()) {
            try {
                Long bn = jdbc.queryForObject(
                        """
                        SELECT biblionumber FROM biblio
                         WHERE LOWER(title) = LOWER(?)
                           AND LOWER(COALESCE(author,'')) = LOWER(COALESCE(?,''))
                         LIMIT 1
                        """,
                        Long.class, title.strip(), author != null ? author.strip() : "");
                if (bn != null) return Optional.of(bn);
            } catch (EmptyResultDataAccessException ignored) {
            }
        }
        return Optional.empty();
    }

    // ── Default budget helper ──────────────────────────────────────────────────

    /**
     * Returns the budget_id of the first active budget.
     * Used as a fallback when no per-record budget is specified
     * (mirrors the `$budget_id = @$budgets[0]->{'budget_id'}` pattern).
     */
    public Optional<Long> findFirstActiveBudgetId() {
        log.debug("Entering findFirstActiveBudgetId");
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            """
                            SELECT b.budget_id FROM aqbudgets b
                              JOIN aqbudgetperiods p ON p.budget_period_id = b.budget_period_id
                             WHERE p.budget_period_active = 1
                             ORDER BY b.budget_id
                             LIMIT 1
                            """,
                            Long.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the budget_id for a given budget_code (used when per-record budget_code is supplied).
     */
    public Optional<Long> findBudgetIdByCode(String budgetCode) {
        log.debug("Entering findBudgetIdByCode - {}", budgetCode);
        if (budgetCode == null || budgetCode.isBlank()) return Optional.empty();
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT budget_id FROM aqbudgets WHERE budget_code = ? LIMIT 1",
                            Long.class, budgetCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

