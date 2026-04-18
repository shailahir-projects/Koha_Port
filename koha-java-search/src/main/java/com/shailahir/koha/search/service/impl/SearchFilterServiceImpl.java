package com.shailahir.koha.search.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.search.dto.SearchFilterDto;
import com.shailahir.koha.search.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Service implementation for search filters.
 * Mirrors: admin/search_filters.pl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchFilterServiceImpl implements SearchFilterService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<SearchFilterDto> MAPPER = (rs, rn) -> {
        SearchFilterDto dto = new SearchFilterDto();
        dto.setSearchFilterId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setQuery(rs.getString("query"));
        dto.setLibraryId(rs.getString("branchcode"));
        dto.setActive(rs.getBoolean("active"));
        return dto;
    };

    @Override
    public Page<SearchFilterDto> listFilters(Pageable pageable) {
        log.debug("Entering listFilters - {}", pageable);
        try {
            int total = jdbc.queryForObject("SELECT COUNT(*) FROM search_filters", Integer.class);
            List<SearchFilterDto> list = jdbc.query(
                "SELECT * FROM search_filters ORDER BY name LIMIT ? OFFSET ?",
                MAPPER, pageable.getPageSize(), pageable.getOffset());
            return new PageImpl<>(list, pageable, total);
        } catch (Exception e) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    @Override
    @Transactional
    public SearchFilterDto addSearchFilter(SearchFilterDto dto) {
        log.debug("Entering addSearchFilter - {}", dto);
        try {
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO search_filters (name, query, branchcode, active) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, dto.getName());
                ps.setString(2, dto.getQuery());
                ps.setString(3, dto.getLibraryId());
                ps.setBoolean(4, dto.getActive() != null && dto.getActive());
                return ps;
            }, kh);
            dto.setSearchFilterId(((Number) kh.getKeys().get("id")).longValue());
        } catch (Exception e) {
            dto.setSearchFilterId(-1L);
        }
        return dto;
    }

    @Override
    public SearchFilterDto getSearchFilter(Long id) {
        log.debug("Entering getSearchFilter - {}", id);
        try {
            return jdbc.queryForObject("SELECT * FROM search_filters WHERE id = ?", MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new java.util.NoSuchElementException("Search filter not found: " + id);
        }
    }

    @Override
    @Transactional
    public SearchFilterDto updateSearchFilter(Long id, SearchFilterDto dto) {
        log.debug("Entering updateSearchFilter - {}, {}", id, dto);
        jdbc.update("UPDATE search_filters SET name=?, query=?, active=? WHERE id=?",
            dto.getName(), dto.getQuery(), dto.getActive(), id);
        dto.setSearchFilterId(id);
        return dto;
    }

    @Override
    @Transactional
    public void deleteSearchFilter(Long id) {
        log.debug("Entering deleteSearchFilter - {}", id);
        jdbc.update("DELETE FROM search_filters WHERE id = ?", id);
    }
}

