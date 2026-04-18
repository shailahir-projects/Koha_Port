package com.shailahir.koha.serials.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.serials.dto.*;
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
import java.util.Map;
import java.util.Optional;

/**
 * Repository for Serials data access.
 * Mirrors: serials/*, Koha/Subscription.pm, Koha/Serial.pm
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SerialsRepository {

    private final JdbcTemplate jdbc;

    // ── Subscriptions ─────────────────────────────────────────────────────────

    private static final RowMapper<SubscriptionDto> SUBSCRIPTION_MAPPER = (rs, rn) -> {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setSubscriptionId(rs.getLong("subscriptionid"));
        dto.setBiblioId(rs.getLong("biblionumber"));
        dto.setVendorId(rs.getLong("aqbooksellerid"));
        dto.setLibraryId(rs.getString("branchcode"));
        dto.setStartDate(rs.getObject("startdate", java.time.LocalDate.class));
        dto.setEndDate(rs.getObject("enddate", java.time.LocalDate.class));
        dto.setStatus(rs.getString("closed"));
        dto.setNotes(rs.getString("notes"));
        dto.setInternalnotes(rs.getString("internalnotes"));
        dto.setFrequencyId(rs.getLong("periodicity"));
        dto.setNumberpatternId(rs.getLong("numberpattern"));
        dto.setCallnumber(rs.getString("callnumber"));
        dto.setLocation(rs.getString("location"));
        dto.setItemType(rs.getString("itemtype"));
        dto.setCreatedDate(rs.getObject("createddate", java.time.LocalDateTime.class));
        return dto;
    };

    public Page<SubscriptionDto> findAllSubscriptions(String query, Pageable pageable) {
        log.debug("Entering findAllSubscriptions - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE notes ILIKE ? OR internalnotes ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM subscription" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<SubscriptionDto> list = jdbc.query(
                "SELECT * FROM subscription" + where + " ORDER BY subscriptionid DESC LIMIT ? OFFSET ?",
                SUBSCRIPTION_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<SubscriptionDto> findSubscriptionById(Long id) {
        log.debug("Entering findSubscriptionById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM subscription WHERE subscriptionid = ?", SUBSCRIPTION_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public SubscriptionDto insertSubscription(SubscriptionDto dto) {
        log.debug("Entering insertSubscription - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO subscription (biblionumber, aqbooksellerid, branchcode, startdate, enddate,
                        closed, notes, internalnotes, periodicity, numberpattern, callnumber, location, itemtype, createddate)
                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, dto.getBiblioId());
            ps.setObject(2, dto.getVendorId());
            ps.setString(3, dto.getLibraryId());
            ps.setObject(4, dto.getStartDate());
            ps.setObject(5, dto.getEndDate());
            ps.setString(6, dto.getStatus());
            ps.setString(7, dto.getNotes());
            ps.setString(8, dto.getInternalnotes());
            ps.setObject(9, dto.getFrequencyId());
            ps.setObject(10, dto.getNumberpatternId());
            ps.setString(11, dto.getCallnumber());
            ps.setString(12, dto.getLocation());
            ps.setString(13, dto.getItemType());
            return ps;
        }, kh);
        dto.setSubscriptionId(((Number) kh.getKeys().get("subscriptionid")).longValue());
        return dto;
    }

    public SubscriptionDto updateSubscription(Long id, SubscriptionDto dto) {
        log.debug("Entering updateSubscription - {}, {}", id, dto);
        jdbc.update("""
                UPDATE subscription SET notes=?, internalnotes=?, enddate=?, closed=? WHERE subscriptionid=?
                """, dto.getNotes(), dto.getInternalnotes(), dto.getEndDate(), dto.getStatus(), id);
        dto.setSubscriptionId(id);
        return dto;
    }

    public void deleteSubscription(Long id) {
        log.debug("Entering deleteSubscription - {}", id);
        jdbc.update("DELETE FROM subscription WHERE subscriptionid = ?", id);
    }

    // ── Serials ───────────────────────────────────────────────────────────────

    private static final RowMapper<SerialDto> SERIAL_MAPPER = (rs, rn) -> {
        SerialDto dto = new SerialDto();
        dto.setSerialId(rs.getLong("serialid"));
        dto.setSubscriptionId(rs.getLong("subscriptionid"));
        dto.setBiblioId(rs.getLong("biblionumber"));
        dto.setStatus(rs.getString("status"));
        dto.setPublishedDate(rs.getObject("publisheddate", java.time.LocalDate.class));
        dto.setPlannedDate(rs.getObject("planneddate", java.time.LocalDate.class));
        dto.setSerialseq(rs.getString("serialseq"));
        dto.setNotes(rs.getString("notes"));
        dto.setRoutingnotes(rs.getString("routingnotes"));
        return dto;
    };

    public List<SerialDto> findSerialsBySubscriptionId(Long subscriptionId) {
        log.debug("Entering findSerialsBySubscriptionId - {}", subscriptionId);
        return jdbc.query(
                "SELECT * FROM serial WHERE subscriptionid = ? ORDER BY serialid DESC",
                SERIAL_MAPPER, subscriptionId);
    }

    // ── Frequencies ───────────────────────────────────────────────────────────

    private static final RowMapper<SubscriptionFrequencyDto> FREQUENCY_MAPPER = (rs, rn) -> {
        SubscriptionFrequencyDto dto = new SubscriptionFrequencyDto();
        dto.setId(rs.getLong("id"));
        dto.setDescription(rs.getString("description"));
        dto.setDisplayorder(rs.getInt("displayorder"));
        dto.setUnit(rs.getString("unit"));
        dto.setUnitsperissue(rs.getInt("unitsperissue"));
        dto.setIssuesperunit(rs.getInt("issuesperunit"));
        return dto;
    };

    public List<SubscriptionFrequencyDto> findAllFrequencies() {
        log.debug("Entering findAllFrequencies");
        return jdbc.query("SELECT * FROM subscription_frequencies ORDER BY displayorder", FREQUENCY_MAPPER);
    }

    public Optional<SubscriptionFrequencyDto> findFrequencyById(Long id) {
        log.debug("Entering findFrequencyById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM subscription_frequencies WHERE id = ?", FREQUENCY_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public SubscriptionFrequencyDto insertFrequency(SubscriptionFrequencyDto dto) {
        log.debug("Entering insertFrequency - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO subscription_frequencies (description, displayorder, unit, unitsperissue, issuesperunit)
                    VALUES (?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getDescription());
            ps.setInt(2, dto.getDisplayorder());
            ps.setString(3, dto.getUnit());
            ps.setInt(4, dto.getUnitsperissue());
            ps.setInt(5, dto.getIssuesperunit());
            return ps;
        }, kh);
        dto.setId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public SubscriptionFrequencyDto updateFrequency(Long id, SubscriptionFrequencyDto dto) {
        log.debug("Entering updateFrequency - {}, {}", id, dto);
        jdbc.update("""
                UPDATE subscription_frequencies SET description=?, displayorder=?, unit=?, unitsperissue=?, issuesperunit=? WHERE id=?
                """, dto.getDescription(), dto.getDisplayorder(), dto.getUnit(), dto.getUnitsperissue(), dto.getIssuesperunit(), id);
        dto.setId(id);
        return dto;
    }

    public void deleteFrequency(Long id) {
        log.debug("Entering deleteFrequency - {}", id);
        jdbc.update("DELETE FROM subscription_frequencies WHERE id = ?", id);
    }

    // ── Numbering Patterns ────────────────────────────────────────────────────

    private static final RowMapper<NumberingPatternDto> PATTERN_MAPPER = (rs, rn) -> {
        NumberingPatternDto dto = new NumberingPatternDto();
        dto.setId(rs.getLong("id"));
        dto.setLabel(rs.getString("label"));
        dto.setDescription(rs.getString("description"));
        dto.setNumberingmethod(rs.getString("numberingmethod"));
        dto.setLabel1(rs.getString("label1"));
        dto.setAdd1(rs.getInt("add1"));
        dto.setEvery1(rs.getInt("every1"));
        dto.setWhenmorethan1(rs.getInt("whenmorethan1"));
        dto.setSetto1(rs.getInt("setto1"));
        dto.setNumbering1(rs.getString("numbering1"));
        dto.setLabel2(rs.getString("label2"));
        dto.setAdd2(rs.getInt("add2"));
        dto.setEvery2(rs.getInt("every2"));
        dto.setWhenmorethan2(rs.getInt("whenmorethan2"));
        dto.setSetto2(rs.getInt("setto2"));
        dto.setNumbering2(rs.getString("numbering2"));
        dto.setLabel3(rs.getString("label3"));
        dto.setAdd3(rs.getInt("add3"));
        dto.setEvery3(rs.getInt("every3"));
        dto.setWhenmorethan3(rs.getInt("whenmorethan3"));
        dto.setSetto3(rs.getInt("setto3"));
        dto.setNumbering3(rs.getString("numbering3"));
        return dto;
    };

    public List<NumberingPatternDto> findAllNumberingPatterns() {
        log.debug("Entering findAllNumberingPatterns");
        return jdbc.query("SELECT * FROM subscription_numberpatterns ORDER BY id", PATTERN_MAPPER);
    }

    public Optional<NumberingPatternDto> findNumberingPatternById(Long id) {
        log.debug("Entering findNumberingPatternById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM subscription_numberpatterns WHERE id = ?", PATTERN_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public NumberingPatternDto insertNumberingPattern(NumberingPatternDto dto) {
        log.debug("Entering insertNumberingPattern - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO subscription_numberpatterns (label, description, numberingmethod,
                        label1, add1, every1, whenmorethan1, setto1, numbering1,
                        label2, add2, every2, whenmorethan2, setto2, numbering2,
                        label3, add3, every3, whenmorethan3, setto3, numbering3)
                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getLabel());
            ps.setString(2, dto.getDescription());
            ps.setString(3, dto.getNumberingmethod());
            ps.setInt(4, dto.getLabel1() != null ? dto.getLabel1().length() : 0);
            ps.setInt(5, dto.getAdd1() != null ? dto.getAdd1() : 0);
            ps.setInt(6, dto.getEvery1() != null ? dto.getEvery1() : 0);
            ps.setInt(7, dto.getWhenmorethan1() != null ? dto.getWhenmorethan1() : 0);
            ps.setInt(8, dto.getSetto1() != null ? dto.getSetto1() : 0);
            ps.setString(9, dto.getNumbering1());
            ps.setInt(10, dto.getLabel2() != null ? dto.getLabel2().length() : 0);
            ps.setInt(11, dto.getAdd2() != null ? dto.getAdd2() : 0);
            ps.setInt(12, dto.getEvery2() != null ? dto.getEvery2() : 0);
            ps.setInt(13, dto.getWhenmorethan2() != null ? dto.getWhenmorethan2() : 0);
            ps.setInt(14, dto.getSetto2() != null ? dto.getSetto2() : 0);
            ps.setString(15, dto.getNumbering2());
            ps.setInt(16, dto.getLabel3() != null ? dto.getLabel3().length() : 0);
            ps.setInt(17, dto.getAdd3() != null ? dto.getAdd3() : 0);
            ps.setInt(18, dto.getEvery3() != null ? dto.getEvery3() : 0);
            ps.setInt(19, dto.getWhenmorethan3() != null ? dto.getWhenmorethan3() : 0);
            ps.setInt(20, dto.getSetto3() != null ? dto.getSetto3() : 0);
            ps.setString(21, dto.getNumbering3());
            return ps;
        }, kh);
        dto.setId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public NumberingPatternDto updateNumberingPattern(Long id, NumberingPatternDto dto) {
        log.debug("Entering updateNumberingPattern - {}, {}", id, dto);
        jdbc.update("""
                UPDATE subscription_numberpatterns SET label=?, description=?, numberingmethod=?,
                    label1=?, add1=?, every1=?, whenmorethan1=?, setto1=?, numbering1=?,
                    label2=?, add2=?, every2=?, whenmorethan2=?, setto2=?, numbering2=?,
                    label3=?, add3=?, every3=?, whenmorethan3=?, setto3=?, numbering3=?
                WHERE id=?
                """, dto.getLabel(), dto.getDescription(), dto.getNumberingmethod(),
                dto.getLabel1(), dto.getAdd1(), dto.getEvery1(), dto.getWhenmorethan1(), dto.getSetto1(), dto.getNumbering1(),
                dto.getLabel2(), dto.getAdd2(), dto.getEvery2(), dto.getWhenmorethan2(), dto.getSetto2(), dto.getNumbering2(),
                dto.getLabel3(), dto.getAdd3(), dto.getEvery3(), dto.getWhenmorethan3(), dto.getSetto3(), dto.getNumbering3(), id);
        dto.setId(id);
        return dto;
    }

    public void deleteNumberingPattern(Long id) {
        log.debug("Entering deleteNumberingPattern - {}", id);
        jdbc.update("DELETE FROM subscription_numberpatterns WHERE id = ?", id);
    }

    public SubscriptionDto renewSubscription(Long id, SubscriptionDto dto) {
        log.debug("Entering renewSubscription - {}, {}", id, dto);
        jdbc.update("""
                UPDATE subscription SET enddate=?, notes=?, internalnotes=? WHERE subscriptionid=?
                """, dto.getEndDate(), dto.getNotes(), dto.getInternalnotes(), id);
        return findSubscriptionById(id).orElse(dto);
    }

    public List<SerialDto> findClaimedSerials(Pageable pageable) {
        log.debug("Entering findClaimedSerials - {}", pageable);
        return jdbc.query(
                "SELECT * FROM serial WHERE status ILIKE 'CLAIMED%' ORDER BY serialid DESC LIMIT ? OFFSET ?",
                SERIAL_MAPPER, pageable.getPageSize(), pageable.getOffset());
    }

    // ── Home / Collection / Routing / Search helpers ─────────────────────────

    public Integer countSubscriptions() {
        log.debug("Entering countSubscriptions");
        return jdbc.queryForObject("SELECT COUNT(*) FROM subscription", Integer.class);
    }

    public Integer countSerials() {
        log.debug("Entering countSerials");
        return jdbc.queryForObject("SELECT COUNT(*) FROM serial", Integer.class);
    }

    public List<Map<String, Object>> findSerialCollection(Pageable pageable) {
        log.debug("Entering findSerialCollection - {}", pageable);
        return jdbc.queryForList(
                "SELECT serialid, subscriptionid, status, serialseq, publisheddate FROM serial ORDER BY serialid DESC LIMIT ? OFFSET ?",
                pageable.getPageSize(), pageable.getOffset());
    }

    public List<Map<String, Object>> findRoutingListBySubscription(Long subscriptionId) {
        log.debug("Entering findRoutingListBySubscription - {}", subscriptionId);
        return jdbc.queryForList(
                "SELECT * FROM subscriptionroutinglist WHERE subscriptionid = ? ORDER BY ranking",
                subscriptionId);
    }

    public void updateRoutingRanking(Object routingId, Object ranking) {
        log.debug("Entering updateRoutingRanking - {}, {}", routingId, ranking);
        jdbc.update("UPDATE subscriptionroutinglist SET ranking = ? WHERE routingid = ?",
                ranking, routingId);
    }

    public List<Map<String, Object>> searchBiblio(String query) {
        log.debug("Entering searchBiblio - {}", query);
        return jdbc.queryForList(
                "SELECT biblionumber, title FROM biblio WHERE title ILIKE ? ORDER BY biblionumber DESC LIMIT 50",
                "%" + query + "%");
    }

    public List<Map<String, Object>> findLateIssues() {
        log.debug("Entering findLateIssues");
        return jdbc.queryForList(
                "SELECT serialid, subscriptionid, serialseq, publisheddate FROM serial WHERE status ILIKE 'LATE%' ORDER BY serialid DESC");
    }

    public List<Map<String, Object>> findExpiredSubscriptions() {
        log.debug("Entering findExpiredSubscriptions");
        return jdbc.queryForList(
                "SELECT subscriptionid, enddate, notes FROM subscription WHERE enddate < CURRENT_DATE ORDER BY enddate DESC");
    }

    public List<Map<String, Object>> searchAcquisitions(String query) {
        log.debug("Entering searchAcquisitions - {}", query);
        return jdbc.queryForList(
                "SELECT aqbooksellerid, name FROM aqbooksellers WHERE name ILIKE ? ORDER BY aqbooksellerid DESC LIMIT 50",
                "%" + query + "%");
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

