package com.shailahir.koha.reporting.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.reporting.dto.*;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for saved reports data access.
 * Mirrors: reports/guided_reports.pl, Koha/Report.pm
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportingRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<SavedReportDto> REPORT_MAPPER = (rs, rn) -> {
        SavedReportDto dto = new SavedReportDto();
        dto.setReportId(rs.getLong("id"));
        dto.setReportName(rs.getString("report_name"));
        dto.setType(rs.getString("type"));
        dto.setNotes(rs.getString("notes"));
        dto.setSqlText(rs.getString("savedsql"));
        dto.setIsPublic(rs.getBoolean("public"));
        dto.setCreatedAt(rs.getObject("date_created", java.time.LocalDateTime.class));
        dto.setUpdatedAt(rs.getObject("last_modified", java.time.LocalDateTime.class));
        dto.setCreatedBy(rs.getString("borrowernumber"));
        dto.setGroup(rs.getString("report_group"));
        dto.setSubGroup(rs.getString("report_subgroup"));
        return dto;
    };

    public Page<SavedReportDto> findAllReports(String query, Pageable pageable) {
        log.debug("Entering findAllReports - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE report_name ILIKE ? OR notes ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM saved_sql" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<SavedReportDto> list = jdbc.query(
                "SELECT * FROM saved_sql" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                REPORT_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<SavedReportDto> findReportById(Long id) {
        log.debug("Entering findReportById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM saved_sql WHERE id = ?", REPORT_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public SavedReportDto insertReport(SavedReportDto dto) {
        log.debug("Entering insertReport - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO saved_sql (report_name, type, notes, savedsql, public, date_created, last_modified, report_group, report_subgroup)
                    VALUES (?,?,?,?,?,NOW(),NOW(),?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getReportName());
            ps.setString(2, dto.getType());
            ps.setString(3, dto.getNotes());
            ps.setString(4, dto.getSqlText());
            ps.setObject(5, dto.getIsPublic());
            ps.setString(6, dto.getGroup());
            ps.setString(7, dto.getSubGroup());
            return ps;
        }, kh);
        dto.setReportId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public SavedReportDto updateReport(Long id, SavedReportDto dto) {
        log.debug("Entering updateReport - {}, {}", id, dto);
        jdbc.update("""
                UPDATE saved_sql SET report_name=?, type=?, notes=?, savedsql=?, public=?, last_modified=NOW(), report_group=?, report_subgroup=?
                WHERE id=?
                """, dto.getReportName(), dto.getType(), dto.getNotes(), dto.getSqlText(),
                dto.getIsPublic(), dto.getGroup(), dto.getSubGroup(), id);
        dto.setReportId(id);
        return dto;
    }

    public void deleteReport(Long id) {
        log.debug("Entering deleteReport - {}", id);
        jdbc.update("DELETE FROM saved_sql WHERE id = ?", id);
    }

    public ReportResultDto executeReport(Long reportId, Map<String, String> params) {
        log.debug("Entering executeReport - {}, {}", reportId, params);
        SavedReportDto report = findReportById(reportId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Report not found"));
        ReportResultDto result = new ReportResultDto();
        result.setReportId(reportId);
        try {
            String sql = report.getSqlText();
            // Replace placeholders <<param>> with values if provided
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sql = sql.replace("<<" + entry.getKey() + ">>", entry.getValue());
                    sql = sql.replace("[[" + entry.getKey() + "]]", entry.getValue());
                }
            }
            List<Map<String, Object>> rows = jdbc.queryForList(sql);
            List<String> columns = rows.isEmpty() ? new ArrayList<>() : new ArrayList<>(rows.get(0).keySet());
            // Convert to ordered maps for JSON serialization
            List<Map<String, Object>> orderedRows = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Map<String, Object> orderedRow = new LinkedHashMap<>(row);
                orderedRows.add(orderedRow);
            }
            result.setColumns(columns);
            result.setRows(orderedRows);
            result.setTotalRows(orderedRows.size());
        } catch (Exception e) {
            result.setError(e.getMessage());
            result.setRows(new ArrayList<>());
            result.setColumns(new ArrayList<>());
            result.setTotalRows(0);
        }
        return result;
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

