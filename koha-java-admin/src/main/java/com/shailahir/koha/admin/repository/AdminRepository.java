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
 * Repository for library/branch administration.
 * Mirrors: admin/branches.pl, admin/cities.pl, admin/authorised_values.pl,
 *          admin/adveditorshortcuts.pl
 */
@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final JdbcTemplate jdbc;

    // ── Libraries ──────────────────────────────────────────────────────────────

    private static final RowMapper<LibraryDto> LIBRARY_MAPPER = (rs, rn) -> {
        LibraryDto dto = new LibraryDto();
        dto.setLibraryId(rs.getString("branchcode"));
        dto.setName(rs.getString("branchname"));
        dto.setAddress1(rs.getString("branchaddress1"));
        dto.setAddress2(rs.getString("branchaddress2"));
        dto.setAddress3(rs.getString("branchaddress3"));
        dto.setCity(rs.getString("branchcity"));
        dto.setState(rs.getString("branchstate"));
        dto.setZipPostal(rs.getString("branchzip"));
        dto.setCountry(rs.getString("branchcountry"));
        dto.setPhone(rs.getString("branchphone"));
        dto.setFax(rs.getString("branchfax"));
        dto.setEmail(rs.getString("branchemail"));
        dto.setUrl(rs.getString("branchurl"));
        dto.setIp(rs.getString("branchip"));
        dto.setNotes(rs.getString("branchnotes"));
        dto.setOpacInfo(rs.getString("opac_info"));
        dto.setTimezone(rs.getString("timezone"));
        dto.setPickupLocation(rs.getObject("pickup_location", Boolean.class));
        return dto;
    };

    public Page<LibraryDto> findAllLibraries(String query, Pageable pageable) {
        String where = query != null && !query.isBlank()
            ? " WHERE branchname ILIKE ? OR branchcode ILIKE ?" : "";
        Object[] params = query != null && !query.isBlank()
            ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};

        int total = jdbc.queryForObject("SELECT COUNT(*) FROM branches" + where, Integer.class, params);
        Object[] pageParams = appendPage(params, pageable);
        List<LibraryDto> list = jdbc.query(
            "SELECT * FROM branches" + where + " ORDER BY branchcode LIMIT ? OFFSET ?",
            LIBRARY_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<LibraryDto> findLibraryById(String branchcode) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM branches WHERE branchcode = ?", LIBRARY_MAPPER, branchcode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public LibraryDto insertLibrary(LibraryDto dto) {
        jdbc.update("""
            INSERT INTO branches (branchcode, branchname, branchaddress1, branchaddress2, branchaddress3,
                branchcity, branchstate, branchzip, branchcountry, branchphone, branchfax,
                branchemail, branchreplyto, branchreturnpath, branchurl, branchip, branchnotes,
                opac_info, timezone, pickup_location)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """,
            dto.getLibraryId(), dto.getName(), dto.getAddress1(), dto.getAddress2(), dto.getAddress3(),
            dto.getCity(), dto.getState(), dto.getZipPostal(), dto.getCountry(),
            dto.getPhone(), dto.getFax(), dto.getEmail(), dto.getReplyToEmail(), dto.getReturnPath(),
            dto.getUrl(), dto.getIp(), dto.getNotes(), dto.getOpacInfo(),
            dto.getTimezone(), dto.getPickupLocation());
        return dto;    }

    public LibraryDto updateLibrary(String branchcode, LibraryDto dto) {
        jdbc.update("""
            UPDATE branches SET branchname=?, branchaddress1=?, branchaddress2=?, branchaddress3=?,
                branchcity=?, branchstate=?, branchzip=?, branchcountry=?,
                branchphone=?, branchfax=?, branchemail=?, branchurl=?,
                branchnotes=?, opac_info=?, timezone=?, pickup_location=?
            WHERE branchcode=?
            """,
            dto.getName(), dto.getAddress1(), dto.getAddress2(), dto.getAddress3(),
            dto.getCity(), dto.getState(), dto.getZipPostal(), dto.getCountry(),
            dto.getPhone(), dto.getFax(), dto.getEmail(), dto.getUrl(),
            dto.getNotes(), dto.getOpacInfo(), dto.getTimezone(), dto.getPickupLocation(),
            branchcode);
        dto.setLibraryId(branchcode);
        return dto;
    }

    public void deleteLibrary(String branchcode) {
        jdbc.update("DELETE FROM branches WHERE branchcode = ?", branchcode);
    }

    // ── Cities ─────────────────────────────────────────────────────────────────

    private static final RowMapper<CityDto> CITY_MAPPER = (rs, rn) -> {
        CityDto dto = new CityDto();
        dto.setCityId(rs.getLong("cityid"));
        dto.setName(rs.getString("city_name"));
        dto.setState(rs.getString("city_state"));
        dto.setCountry(rs.getString("city_country"));
        dto.setZipcode(rs.getString("city_zipcode"));
        return dto;
    };

    public Page<CityDto> findAllCities(String query, Pageable pageable) {
        String where = query != null && !query.isBlank() ? " WHERE city_name ILIKE ?" : "";
        Object[] params = query != null && !query.isBlank()
            ? new Object[]{"%" + query + "%"} : new Object[]{};
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM cities" + where, Integer.class, params);
        List<CityDto> list = jdbc.query(
            "SELECT * FROM cities" + where + " ORDER BY city_name LIMIT ? OFFSET ?",
            CITY_MAPPER, appendPage(params, pageable));
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<CityDto> findCityById(Long cityId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM cities WHERE cityid = ?", CITY_MAPPER, cityId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public CityDto insertCity(CityDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cities (city_name, city_state, city_country, city_zipcode) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getState());
            ps.setString(3, dto.getCountry());
            ps.setString(4, dto.getZipcode());
            return ps;
        }, kh);
        dto.setCityId(((Number) kh.getKeys().get("cityid")).longValue());
        return dto;
    }

    public CityDto updateCity(Long cityId, CityDto dto) {
        jdbc.update("UPDATE cities SET city_name=?, city_state=?, city_country=?, city_zipcode=? WHERE cityid=?",
            dto.getName(), dto.getState(), dto.getCountry(), dto.getZipcode(), cityId);
        dto.setCityId(cityId);
        return dto;
    }

    public void deleteCity(Long cityId) {
        jdbc.update("DELETE FROM cities WHERE cityid = ?", cityId);
    }

    // ── Authorised Values ──────────────────────────────────────────────────────

    private static final RowMapper<AuthorisedValueDto> AV_MAPPER = (rs, rn) -> {
        AuthorisedValueDto dto = new AuthorisedValueDto();
        dto.setAuthorisedValueId(rs.getLong("id"));
        dto.setCategoryName(rs.getString("category"));
        dto.setValue(rs.getString("authorised_value"));
        dto.setDescription(rs.getString("lib"));
        dto.setDescriptionOpac(rs.getString("lib_opac"));
        dto.setImageUrl(rs.getString("imageurl"));
        dto.setBranchLimitation(rs.getString("branchcode"));
        return dto;
    };

    private static final RowMapper<AuthorisedValueCategoryDto> AVC_MAPPER = (rs, rn) -> {
        AuthorisedValueCategoryDto dto = new AuthorisedValueCategoryDto();
        dto.setCategoryName(rs.getString("category"));
        dto.setIsSystem(rs.getBoolean("is_system"));
        return dto;
    };

    public List<AuthorisedValueDto> findAuthorisedValuesByCategory(String category, Pageable pageable) {
        if (pageable.isUnpaged()) {
            return jdbc.query("SELECT * FROM authorised_values WHERE category = ? ORDER BY lib",
                AV_MAPPER, category);
        }
        return jdbc.query("SELECT * FROM authorised_values WHERE category = ? ORDER BY lib LIMIT ? OFFSET ?",
            AV_MAPPER, category, pageable.getPageSize(), pageable.getOffset());
    }

    public List<AuthorisedValueCategoryDto> findAllAuthorisedValueCategories() {
        return jdbc.query("SELECT DISTINCT category, is_system FROM authorised_values ORDER BY category",
            AVC_MAPPER);
    }

    // ── Transfer Limits ────────────────────────────────────────────────────────

    private static final RowMapper<TransferLimitDto> TL_MAPPER = (rs, rn) -> {
        TransferLimitDto dto = new TransferLimitDto();
        dto.setTransferLimitId(rs.getLong("id"));
        dto.setFromLibraryId(rs.getString("fromBranch"));
        dto.setToLibraryId(rs.getString("toBranch"));
        dto.setItemtype(rs.getString("itemtype"));
        return dto;
    };

    public List<TransferLimitDto> findAllTransferLimits() {
        return jdbc.query("SELECT * FROM branch_transfer_limits ORDER BY fromBranch, toBranch", TL_MAPPER);
    }

    public TransferLimitDto insertTransferLimit(TransferLimitDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO branch_transfer_limits (fromBranch, toBranch, itemtype) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getFromLibraryId());
            ps.setString(2, dto.getToLibraryId());
            ps.setString(3, dto.getItemtype());
            return ps;
        }, kh);
        dto.setTransferLimitId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public void deleteTransferLimit(Long limitId) {
        jdbc.update("DELETE FROM branch_transfer_limits WHERE id = ?", limitId);
    }

    // ── Extended Attribute Types ───────────────────────────────────────────────

    private static final RowMapper<ExtendedAttributeTypeDto> EAT_MAPPER = (rs, rn) -> {
        ExtendedAttributeTypeDto dto = new ExtendedAttributeTypeDto();
        dto.setCode(rs.getString("code"));
        dto.setDescription(rs.getString("description"));
        dto.setRepeatableFlag(rs.getBoolean("repeatable"));
        dto.setUniqueIdFlag(rs.getBoolean("unique_id"));
        dto.setOpacDisplay(rs.getBoolean("opac_display"));
        dto.setOpacEditable(rs.getBoolean("opac_editable"));
        dto.setSearchable(rs.getBoolean("searchable"));
        dto.setCategoryCodes(rs.getString("category_code"));
        dto.setPasswordBlacklist(rs.getString("password_blacklisted"));
        dto.setMandatoryIfNotPermission(rs.getString("mandatory"));
        dto.setAuthorisedValueCategory(rs.getString("authorised_value_category"));
        dto.setLibraryLimits(rs.getString("lib_opac"));
        dto.setMaxLength(rs.getObject("max_length", Integer.class));
        dto.setNormalizer(rs.getString("normalizer"));
        return dto;
    };

    public List<ExtendedAttributeTypeDto> findAllExtendedAttributeTypes() {
        return jdbc.query("SELECT * FROM borrower_attribute_types ORDER BY code", EAT_MAPPER);
    }

    // ── Desks ──────────────────────────────────────────────────────────────────

    private static final RowMapper<DeskDto> DESK_MAPPER = (rs, rn) -> {
        DeskDto dto = new DeskDto();
        dto.setDeskId(rs.getLong("desk_id"));
        dto.setDeskName(rs.getString("desk_name"));
        dto.setBranchcode(rs.getString("branchcode"));
        return dto;
    };

    public List<DeskDto> findDesksByLibrary(String branchcode) {
        return jdbc.query("SELECT * FROM desks WHERE branchcode = ? ORDER BY desk_name", DESK_MAPPER, branchcode);
    }

    // ── Cash Registers ─────────────────────────────────────────────────────────

    private static final RowMapper<CashRegisterDto> CR_MAPPER = (rs, rn) -> {
        CashRegisterDto dto = new CashRegisterDto();
        dto.setCashRegisterId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setLibraryId(rs.getString("branch"));
        dto.setInitialFloat(rs.getBigDecimal("initial_float"));
        dto.setAccountType(rs.getString("account_type"));
        return dto;
    };

    public List<CashRegisterDto> findCashRegistersByLibrary(String branchcode) {
        return jdbc.query("SELECT * FROM cash_registers WHERE branch = ? ORDER BY name", CR_MAPPER, branchcode);
    }

    // ── Advanced Editor Macros ─────────────────────────────────────────────────

    private static final RowMapper<AdvancedEditorMacroDto> MACRO_MAPPER = (rs, rn) -> {
        AdvancedEditorMacroDto dto = new AdvancedEditorMacroDto();
        dto.setMacroId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setContent(rs.getString("content"));
        dto.setPatronId(rs.getObject("borrowernumber", Long.class));
        dto.setShared(rs.getBoolean("shared"));
        return dto;
    };

    public Page<AdvancedEditorMacroDto> findAllMacros(Pageable pageable) {
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM advanced_editor_macros WHERE shared = false", Integer.class);
        List<AdvancedEditorMacroDto> list = jdbc.query(
            "SELECT * FROM advanced_editor_macros WHERE shared = false ORDER BY name LIMIT ? OFFSET ?",
            MACRO_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<AdvancedEditorMacroDto> findMacroById(Long macroId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM advanced_editor_macros WHERE id = ?", MACRO_MAPPER, macroId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AdvancedEditorMacroDto insertMacro(AdvancedEditorMacroDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO advanced_editor_macros (name, content, borrowernumber, shared) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getContent());
            ps.setObject(3, dto.getPatronId());
            ps.setBoolean(4, dto.getShared() != null && dto.getShared());
            return ps;
        }, kh);
        dto.setMacroId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public AdvancedEditorMacroDto updateMacro(Long macroId, AdvancedEditorMacroDto dto) {
        jdbc.update("UPDATE advanced_editor_macros SET name=?, content=? WHERE id=?",
            dto.getName(), dto.getContent(), macroId);
        dto.setMacroId(macroId);
        return dto;
    }

    public void deleteMacro(Long macroId) {
        jdbc.update("DELETE FROM advanced_editor_macros WHERE id = ?", macroId);
    }

    // ── Tickets ────────────────────────────────────────────────────────────────

    private static final RowMapper<TicketDto> TICKET_MAPPER = (rs, rn) -> {
        TicketDto dto = new TicketDto();
        dto.setTicketId(rs.getLong("id"));
        dto.setTitle(rs.getString("title"));
        dto.setBody(rs.getString("body"));
        dto.setStatus(rs.getString("status"));
        dto.setReporterId(rs.getObject("reporter_id", Long.class));
        dto.setBiblionumber(rs.getObject("biblio_id", Long.class));
        dto.setAssigneeId(rs.getObject("assignee_id", Long.class));
        dto.setCreatedDate(rs.getObject("created_date", java.time.LocalDateTime.class));
        dto.setUpdatedDate(rs.getObject("updated_date", java.time.LocalDateTime.class));
        return dto;
    };

    public Page<TicketDto> findAllTickets(Pageable pageable) {
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM tickets", Integer.class);
        List<TicketDto> list = jdbc.query(
            "SELECT * FROM tickets ORDER BY created_date DESC LIMIT ? OFFSET ?",
            TICKET_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<TicketDto> findTicketById(Long ticketId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "SELECT * FROM tickets WHERE id = ?", TICKET_MAPPER, ticketId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public TicketDto insertTicket(TicketDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tickets (title, body, status, reporter_id, biblio_id, assignee_id) VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getBody());
            ps.setString(3, dto.getStatus() != null ? dto.getStatus() : "new");
            ps.setObject(4, dto.getReporterId());
            ps.setObject(5, dto.getBiblionumber());
            ps.setObject(6, dto.getAssigneeId());
            return ps;
        }, kh);
        dto.setTicketId(((Number) kh.getKeys().get("id")).longValue());
        return dto;
    }

    public TicketDto updateTicket(Long ticketId, TicketDto dto) {
        jdbc.update("UPDATE tickets SET title=?, body=?, status=?, assignee_id=? WHERE id=?",
            dto.getTitle(), dto.getBody(), dto.getStatus(), dto.getAssigneeId(), ticketId);
        dto.setTicketId(ticketId);
        return dto;
    }

    public void deleteTicket(Long ticketId) {
        jdbc.update("DELETE FROM tickets WHERE id = ?", ticketId);
    }

    private static final RowMapper<TicketUpdateDto> TICKET_UPDATE_MAPPER = (rs, rn) -> {
        TicketUpdateDto dto = new TicketUpdateDto();
        dto.setUpdateId(rs.getLong("id"));
        dto.setTicketId(rs.getLong("ticket_id"));
        dto.setMessage(rs.getString("message"));
        dto.setCreatorId(rs.getObject("creator_id", Long.class));
        dto.setCreatedDate(rs.getObject("created_date", java.time.LocalDateTime.class));
        return dto;
    };

    public List<TicketUpdateDto> findTicketUpdates(Long ticketId) {
        return jdbc.query("SELECT * FROM ticket_updates WHERE ticket_id = ? ORDER BY created_date", TICKET_UPDATE_MAPPER, ticketId);
    }

    public TicketUpdateDto insertTicketUpdate(Long ticketId, TicketUpdateDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO ticket_updates (ticket_id, message, creator_id) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, ticketId);
            ps.setString(2, dto.getMessage());
            ps.setObject(3, dto.getCreatorId());
            return ps;
        }, kh);
        dto.setUpdateId(((Number) kh.getKeys().get("id")).longValue());
        dto.setTicketId(ticketId);
        return dto;
    }

    private Object[] appendPage(Object[] base, Pageable pageable) {
        Object[] result = new Object[base.length + 2];
        System.arraycopy(base, 0, result, 0, base.length);
        result[base.length] = pageable.getPageSize();
        result[base.length + 1] = pageable.getOffset();
        return result;
    }
}

