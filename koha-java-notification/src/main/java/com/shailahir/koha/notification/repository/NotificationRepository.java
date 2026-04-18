package com.shailahir.koha.notification.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.notification.dto.*;
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
 * Repository for Notification data access.
 * Mirrors: Koha/Notice/Template.pm, additional_contents table
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final JdbcTemplate jdbc;

    // ── Notice Templates ──────────────────────────────────────────────────────

    private static final RowMapper<NoticeTemplateDto> NOTICE_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        NoticeTemplateDto dto = new NoticeTemplateDto();
        dto.setId(rs.getLong("id"));
        dto.setCode(rs.getString("code"));
        dto.setName(rs.getString("name"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setContentHtml(rs.getString("content_html"));
        dto.setModuleType(rs.getString("module"));
        dto.setMessageTransport(rs.getString("message_transport_type"));
        dto.setCreatedDate(rs.getObject("createddate", java.time.LocalDateTime.class));
        dto.setUpdatedDate(rs.getObject("last_updated", java.time.LocalDateTime.class));
        dto.setIsDefault(rs.getBoolean("is_default"));
        return dto;
    };

    public Page<NoticeTemplateDto> findAllNotices(String query, Pageable pageable) {
        log.debug("Entering findAllNotices - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE name ILIKE ? OR code ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM letter" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<NoticeTemplateDto> list = jdbc.query(
                "SELECT * FROM letter" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                NOTICE_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<NoticeTemplateDto> findNoticeById(Long id) {
        log.debug("Entering findNoticeById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM letter WHERE id = ?", NOTICE_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public NoticeTemplateDto insertNotice(NoticeTemplateDto dto) {
        log.debug("Entering insertNotice - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO letter (code, name, title, content, content_html, module, message_transport_type, is_default, createddate)
                    VALUES (?,?,?,?,?,?,?,?,NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getCode());
            ps.setString(2, dto.getName());
            ps.setString(3, dto.getTitle());
            ps.setString(4, dto.getContent());
            ps.setString(5, dto.getContentHtml());
            ps.setString(6, dto.getModuleType());
            ps.setString(7, dto.getMessageTransport());
            ps.setBoolean(8, dto.getIsDefault() != null && dto.getIsDefault());
            return ps;
        }, kh);
        dto.setId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public NoticeTemplateDto updateNotice(Long id, NoticeTemplateDto dto) {
        log.debug("Entering updateNotice - {}, {}", id, dto);
        jdbc.update("""
                UPDATE letter SET name=?, title=?, content=?, content_html=?, message_transport_type=?, last_updated=NOW()
                WHERE id=?
                """, dto.getName(), dto.getTitle(), dto.getContent(), dto.getContentHtml(), dto.getMessageTransport(), id);
        dto.setId(id);
        return dto;
    }

    public void deleteNotice(Long id) {
        log.debug("Entering deleteNotice - {}", id);
        jdbc.update("DELETE FROM letter WHERE id = ?", id);
    }

    // ── Additional Contents ───────────────────────────────────────────────────

    private static final RowMapper<AdditionalContentDto> CONTENT_MAPPER = (rs, rn) -> {
        log.debug("Entering = - {}, {}", rs, rn);
        AdditionalContentDto dto = new AdditionalContentDto();
        dto.setId(rs.getLong("id"));
        dto.setIdnew(rs.getString("idnew"));
        dto.setCode(rs.getString("code"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setContentHtml(rs.getString("content_html"));
        dto.setCategory(rs.getString("category"));
        dto.setLocation(rs.getString("location"));
        dto.setLang(rs.getString("lang"));
        dto.setCreatedDate(rs.getObject("createddate", java.time.LocalDateTime.class));
        dto.setUpdatedDate(rs.getObject("last_updated", java.time.LocalDateTime.class));
        return dto;
    };

    public Page<AdditionalContentDto> findAllContent(String query, Pageable pageable) {
        log.debug("Entering findAllContent - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE code ILIKE ? OR title ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM additional_contents" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<AdditionalContentDto> list = jdbc.query(
                "SELECT * FROM additional_contents" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                CONTENT_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<AdditionalContentDto> findContentById(Long id) {
        log.debug("Entering findContentById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM additional_contents WHERE id = ?", CONTENT_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AdditionalContentDto insertContent(AdditionalContentDto dto) {
        log.debug("Entering insertContent - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO additional_contents (idnew, code, title, content, content_html, category, location, lang, createddate)
                    VALUES (?,?,?,?,?,?,?,?,NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getIdnew());
            ps.setString(2, dto.getCode());
            ps.setString(3, dto.getTitle());
            ps.setString(4, dto.getContent());
            ps.setString(5, dto.getContentHtml());
            ps.setString(6, dto.getCategory());
            ps.setString(7, dto.getLocation());
            ps.setString(8, dto.getLang());
            return ps;
        }, kh);
        dto.setId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public AdditionalContentDto updateContent(Long id, AdditionalContentDto dto) {
        log.debug("Entering updateContent - {}, {}", id, dto);
        jdbc.update("""
                UPDATE additional_contents SET title=?, content=?, content_html=?, category=?, location=?, lang=?, last_updated=NOW()
                WHERE id=?
                """, dto.getTitle(), dto.getContent(), dto.getContentHtml(), dto.getCategory(), dto.getLocation(), dto.getLang(), id);
        dto.setId(id);
        return dto;
    }

    public void deleteContent(Long id) {
        log.debug("Entering deleteContent - {}", id);
        jdbc.update("DELETE FROM additional_contents WHERE id = ?", id);
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

