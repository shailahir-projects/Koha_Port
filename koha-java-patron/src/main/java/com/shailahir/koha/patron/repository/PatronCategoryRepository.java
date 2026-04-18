package com.shailahir.koha.patron.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.PatronCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for patron categories.
 * Mirrors: members/memberentry.pl (category selection)
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PatronCategoryRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<PatronCategoryDto> ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        PatronCategoryDto dto = new PatronCategoryDto();
        dto.setCategorycode(rs.getString("categorycode"));
        dto.setDescription(rs.getString("description"));
        dto.setEnrolmentperiod(rs.getObject("enrolmentperiod", Integer.class));
        dto.setEnrolmentperioddate(rs.getObject("enrolmentperioddate", java.time.LocalDate.class));
        dto.setPasswordExpiry(rs.getObject("password_expiry_days", Integer.class));
        dto.setUpperagelimit(rs.getObject("upperagelimit", Integer.class));
        dto.setDateofbirthrequired(rs.getObject("dateofbirthrequired", Integer.class));
        dto.setFinenoticerequired(rs.getObject("finnoticerequired", Integer.class));
        dto.setIssuelimit(rs.getObject("issuelimit", Integer.class));
        dto.setReservefee(rs.getBigDecimal("reservefee"));
        dto.setHidelostitems(rs.getBoolean("hidelostitems"));
        dto.setCategorytype(rs.getString("categorytype"));
        dto.setBlockExpiredPatronOpacActions(rs.getString("BlockExpiredPatronOpacActions"));
        dto.setDefaultPrivacy(rs.getString("default_privacy"));
        dto.setMaximumHolds(rs.getObject("max_holds", Integer.class));
        dto.setExcludeFromLocalHoldsPriority(rs.getObject("exclude_from_local_holds_priority", Boolean.class));
        dto.setSmsprovider(rs.getString("sms_provider_id"));
        dto.setBranchcode(rs.getString("branchcode"));
        return dto;
    };

    public List<PatronCategoryDto> findAll() {
        log.debug("Entering findAll");
        return jdbc.query("SELECT * FROM categories ORDER BY categorycode", ROW_MAPPER);
    }

    public Optional<PatronCategoryDto> findByCategorycode(String categorycode) {
        log.debug("Entering findByCategorycode - {}", categorycode);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM categories WHERE categorycode = ?", ROW_MAPPER, categorycode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

