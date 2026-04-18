package com.shailahir.koha.batch.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.batch.dto.JobDto;
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
 * Repository for Batch Job data access.
 * Mirrors: Koha/BackgroundJob.pm
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<JobDto> JOB_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        JobDto dto = new JobDto();
        dto.setJobId(rs.getLong("id"));
        dto.setType(rs.getString("type"));
        dto.setStatus(rs.getString("status"));
        dto.setData(rs.getString("data"));
        dto.setCreatedDate(rs.getObject("created_on", java.time.LocalDateTime.class));
        dto.setStartedDate(rs.getObject("started_on", java.time.LocalDateTime.class));
        dto.setCompletedDate(rs.getObject("ended_on", java.time.LocalDateTime.class));
        dto.setProgress(rs.getString("progress"));
        dto.setError(rs.getString("error"));
        dto.setUserId(rs.getObject("borrowernumber", Long.class));
        return dto;
    };

    public Page<JobDto> findAllJobs(String query, Pageable pageable) {
        log.debug("Entering findAllJobs - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE type ILIKE ? OR status ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM background_jobs" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<JobDto> list = jdbc.query(
                "SELECT * FROM background_jobs" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                JOB_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<JobDto> findJobById(Long id) {
        log.debug("Entering findJobById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM background_jobs WHERE id = ?", JOB_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public JobDto insertJob(JobDto dto) {
        log.debug("Entering insertJob - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO background_jobs (type, status, data, created_on, borrowernumber)
                    VALUES (?,?,?,NOW(),?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getType());
            ps.setString(2, dto.getStatus());
            ps.setString(3, dto.getData());
            ps.setObject(4, dto.getUserId());
            return ps;
        }, kh);
        dto.setJobId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public JobDto updateJob(Long id, JobDto dto) {
        log.debug("Entering updateJob - {}, {}", id, dto);
        jdbc.update("""
                UPDATE background_jobs SET status=?, progress=?, error=?, started_on=?, ended_on=NOW()
                WHERE id=?
                """, dto.getStatus(), dto.getProgress(), dto.getError(), dto.getStartedDate(), id);
        dto.setJobId(id);
        return dto;
    }

    public void deleteJob(Long id) {
        log.debug("Entering deleteJob - {}", id);
        jdbc.update("DELETE FROM background_jobs WHERE id = ?", id);
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

