package com.shailahir.koha.admin.repository;

import com.shailahir.koha.admin.dto.*;
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
 * Repository for SIP2 and Preservation data.
 * Mirrors: admin/sip2_*.pl and preserv_*.pl (via preservation config/processings/trains)
 */
@Repository
@RequiredArgsConstructor
public class Sip2PreservationRepository {

    private final JdbcTemplate jdbc;

    // ── SIP2 Accounts ──────────────────────────────────────────────────────────

    private static final RowMapper<Sip2AccountDto> SIP2_ACCOUNT_MAPPER = (rs, rn) -> {
        Sip2AccountDto dto = new Sip2AccountDto();
        dto.setSipAccountId(rs.getLong("id"));
        dto.setSip2AccountId(rs.getLong("id"));
        dto.setDescription(rs.getString("description"));
        dto.setIpRestriction(rs.getString("ip_restriction"));
        dto.setLogin(rs.getString("login"));
        dto.setPassword(rs.getString("password"));
        dto.setPatronId(rs.getObject("patron_id", Long.class));
        return dto;
    };

    public Page<Sip2AccountDto> findAllSip2Accounts(Pageable pageable) {
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM sip_accounts", Integer.class);
        List<Sip2AccountDto> list = jdbc.query(
            "SELECT * FROM sip_accounts ORDER BY id LIMIT ? OFFSET ?",
            SIP2_ACCOUNT_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<Sip2AccountDto> findSip2AccountById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM sip_accounts WHERE id = ?", SIP2_ACCOUNT_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Sip2AccountDto insertSip2Account(Sip2AccountDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO sip_accounts (description, ip_restriction, login, password, patron_id) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getDescription());
            ps.setString(2, dto.getIpRestriction());
            ps.setString(3, dto.getLogin());
            ps.setString(4, dto.getPassword());
            ps.setObject(5, dto.getPatronId());
            return ps;
        }, kh);
        dto.setSip2AccountId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public Sip2AccountDto updateSip2Account(Long id, Sip2AccountDto dto) {
        jdbc.update("UPDATE sip_accounts SET description=?, ip_restriction=?, login=?, password=? WHERE id=?",
            dto.getDescription(), dto.getIpRestriction(), dto.getLogin(), dto.getPassword(), id);
        dto.setSip2AccountId(id);
        return dto;
    }

    public void deleteSip2Account(Long id) {
        jdbc.update("DELETE FROM sip_accounts WHERE id = ?", id);
    }

    // ── SIP2 Institutions ──────────────────────────────────────────────────────

    private static final RowMapper<Sip2InstitutionDto> SIP2_INST_MAPPER = (rs, rn) -> {
        Sip2InstitutionDto dto = new Sip2InstitutionDto();
        dto.setSip2InstitutionId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setBranchcode(rs.getString("branchcode"));
        return dto;
    };

    public Page<Sip2InstitutionDto> findAllSip2Institutions(Pageable pageable) {
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM sip_institutions", Integer.class);
        List<Sip2InstitutionDto> list = jdbc.query(
            "SELECT * FROM sip_institutions ORDER BY name LIMIT ? OFFSET ?",
            SIP2_INST_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<Sip2InstitutionDto> findSip2InstitutionById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM sip_institutions WHERE id = ?", SIP2_INST_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Sip2InstitutionDto insertSip2Institution(Sip2InstitutionDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO sip_institutions (name, branchcode) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getBranchcode());
            return ps;
        }, kh);
        dto.setSip2InstitutionId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public Sip2InstitutionDto updateSip2Institution(Long id, Sip2InstitutionDto dto) {
        jdbc.update("UPDATE sip_institutions SET name=?, branchcode=? WHERE id=?",
            dto.getName(), dto.getBranchcode(), id);
        dto.setSip2InstitutionId(id);
        return dto;
    }

    public void deleteSip2Institution(Long id) {
        jdbc.update("DELETE FROM sip_institutions WHERE id = ?", id);
    }

    // ── SIP2 System Preference Overrides ──────────────────────────────────────

    private static final RowMapper<Sip2SystemPreferenceOverrideDto> SIP2_PREF_MAPPER = (rs, rn) -> {
        Sip2SystemPreferenceOverrideDto dto = new Sip2SystemPreferenceOverrideDto();
        dto.setOverrideId(rs.getLong("id"));
        dto.setSip2AccountId(rs.getObject("sip_account_id", Long.class));
        dto.setPreference(rs.getString("preference"));
        dto.setValue(rs.getString("value"));
        return dto;
    };

    public Page<Sip2SystemPreferenceOverrideDto> findAllSip2Overrides(Pageable pageable) {
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM sip_preferences", Integer.class);
        List<Sip2SystemPreferenceOverrideDto> list = jdbc.query(
            "SELECT * FROM sip_preferences ORDER BY id LIMIT ? OFFSET ?",
            SIP2_PREF_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<Sip2SystemPreferenceOverrideDto> findSip2OverrideById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM sip_preferences WHERE id = ?", SIP2_PREF_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Sip2SystemPreferenceOverrideDto insertSip2Override(Sip2SystemPreferenceOverrideDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO sip_preferences (sip_account_id, preference, value) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, dto.getSip2AccountId());
            ps.setString(2, dto.getPreference());
            ps.setString(3, dto.getValue());
            return ps;
        }, kh);
        dto.setOverrideId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public Sip2SystemPreferenceOverrideDto updateSip2Override(Long id, Sip2SystemPreferenceOverrideDto dto) {
        jdbc.update("UPDATE sip_preferences SET preference=?, value=? WHERE id=?",
            dto.getPreference(), dto.getValue(), id);
        dto.setOverrideId(id);
        return dto;
    }

    public void deleteSip2Override(Long id) {
        jdbc.update("DELETE FROM sip_preferences WHERE id = ?", id);
    }

    // ── Preservation Trains ────────────────────────────────────────────────────

    private static final RowMapper<PreservationTrainDto> TRAIN_MAPPER = (rs, rn) -> {
        PreservationTrainDto dto = new PreservationTrainDto();
        dto.setTrainId(rs.getLong("train_id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setBranchcode(rs.getString("branchcode"));
        dto.setDefaultProcessingId(rs.getObject("default_processing_id", Long.class));
        dto.setCreatedDate(rs.getObject("created_on", java.time.LocalDateTime.class));
        dto.setClosedDate(rs.getObject("closed_on", java.time.LocalDate.class));
        dto.setSentDate(rs.getObject("sent_on", java.time.LocalDateTime.class));
        dto.setReceivedDate(rs.getObject("received_on", java.time.LocalDateTime.class));
        return dto;
    };

    public List<PreservationTrainDto> findAllTrains() {
        return jdbc.query("SELECT * FROM preservation_trains ORDER BY train_id", TRAIN_MAPPER);
    }

    public Optional<PreservationTrainDto> findTrainById(Long trainId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM preservation_trains WHERE train_id = ?", TRAIN_MAPPER, trainId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public PreservationTrainDto insertTrain(PreservationTrainDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO preservation_trains (name, description, branchcode, default_processing_id) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getDescription());
            ps.setString(3, dto.getBranchcode());
            ps.setObject(4, dto.getDefaultProcessingId());
            return ps;
        }, kh);
        dto.setTrainId(((Number) kh.getKeys().get("train_id")).longValue());
        return dto;
    }

    public PreservationTrainDto updateTrain(Long trainId, PreservationTrainDto dto) {
        jdbc.update("UPDATE preservation_trains SET name=?, description=?, branchcode=? WHERE train_id=?",
            dto.getName(), dto.getDescription(), dto.getBranchcode(), trainId);
        dto.setTrainId(trainId);
        return dto;
    }

    public void deleteTrain(Long trainId) {
        jdbc.update("DELETE FROM preservation_trains WHERE train_id = ?", trainId);
    }

    // ── Preservation Train Items ───────────────────────────────────────────────

    private static final RowMapper<PreservationTrainItemDto> TRAIN_ITEM_MAPPER = (rs, rn) -> {
        PreservationTrainItemDto dto = new PreservationTrainItemDto();
        dto.setTrainItemId(rs.getLong("train_item_id"));
        dto.setTrainId(rs.getLong("train_id"));
        dto.setItemId(rs.getLong("item_id"));
        dto.setProcessingId(rs.getObject("processing_id", Long.class));
        dto.setAddedOn(rs.getObject("added_on", java.time.LocalDateTime.class));
        dto.setRemovedOn(rs.getObject("removed_on", java.time.LocalDateTime.class));
        return dto;
    };

    public List<PreservationTrainItemDto> findTrainItems(Long trainId) {
        return jdbc.query("SELECT * FROM preservation_train_items WHERE train_id = ? ORDER BY train_item_id",
            TRAIN_ITEM_MAPPER, trainId);
    }

    public Optional<PreservationTrainItemDto> findTrainItemById(Long trainItemId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM preservation_train_items WHERE train_item_id = ?", TRAIN_ITEM_MAPPER, trainItemId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public PreservationTrainItemDto insertTrainItem(Long trainId, PreservationTrainItemDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO preservation_train_items (train_id, item_id, processing_id) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, trainId);
            ps.setLong(2, dto.getItemId());
            ps.setObject(3, dto.getProcessingId());
            return ps;
        }, kh);
        dto.setTrainItemId(((Number) kh.getKeys().get("train_item_id")).longValue());
        dto.setTrainId(trainId);
        return dto;
    }

    public PreservationTrainItemDto updateTrainItem(Long trainId, Long trainItemId, PreservationTrainItemDto dto) {
        jdbc.update("UPDATE preservation_train_items SET processing_id=? WHERE train_item_id=? AND train_id=?",
            dto.getProcessingId(), trainItemId, trainId);
        dto.setTrainItemId(trainItemId);
        dto.setTrainId(trainId);
        return dto;
    }

    public void deleteTrainItem(Long trainId, Long trainItemId) {
        jdbc.update("DELETE FROM preservation_train_items WHERE train_item_id = ? AND train_id = ?",
            trainItemId, trainId);
    }

    // ── Preservation Processings ───────────────────────────────────────────────

    private static final RowMapper<PreservationProcessingDto> PROC_MAPPER = (rs, rn) -> {
        PreservationProcessingDto dto = new PreservationProcessingDto();
        dto.setProcessingId(rs.getLong("processing_id"));
        dto.setName(rs.getString("name"));
        dto.setLocation(rs.getString("location"));
        return dto;
    };

    public List<PreservationProcessingDto> findAllProcessings() {
        return jdbc.query("SELECT * FROM preservation_processings ORDER BY processing_id", PROC_MAPPER);
    }

    public Optional<PreservationProcessingDto> findProcessingById(Long processingId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM preservation_processings WHERE processing_id = ?", PROC_MAPPER, processingId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public PreservationProcessingDto insertProcessing(PreservationProcessingDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO preservation_processings (name, location) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getLocation());
            return ps;
        }, kh);
        dto.setProcessingId(((Number) kh.getKeys().get("processing_id")).longValue());
        return dto;
    }

    public PreservationProcessingDto updateProcessing(Long processingId, PreservationProcessingDto dto) {
        jdbc.update("UPDATE preservation_processings SET name=?, location=? WHERE processing_id=?",
            dto.getName(), dto.getLocation(), processingId);
        dto.setProcessingId(processingId);
        return dto;
    }

    public void deleteProcessing(Long processingId) {
        jdbc.update("DELETE FROM preservation_processings WHERE processing_id = ?", processingId);
    }

    // ── Preservation Waiting List ──────────────────────────────────────────────

    private static final RowMapper<PreservationWaitingListItemDto> WL_MAPPER = (rs, rn) -> {
        PreservationWaitingListItemDto dto = new PreservationWaitingListItemDto();
        dto.setItemId(rs.getLong("item_id"));
        dto.setAddedOn(rs.getObject("added_on", java.time.LocalDateTime.class));
        return dto;
    };

    public List<PreservationWaitingListItemDto> findWaitingListItems() {
        return jdbc.query("SELECT * FROM preservation_waiting_list ORDER BY added_on", WL_MAPPER);
    }

    public void insertWaitingListItem(Long itemId) {
        jdbc.update("INSERT INTO preservation_waiting_list (item_id, added_on) VALUES (?, NOW()) ON CONFLICT DO NOTHING", itemId);
    }

    public void deleteWaitingListItem(Long itemId) {
        jdbc.update("DELETE FROM preservation_waiting_list WHERE item_id = ?", itemId);
    }
}

