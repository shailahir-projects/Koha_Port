package com.shailahir.koha.catalog.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.catalog.dto.*;
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
import java.util.List;
import java.util.Optional;

/**
 * Repository for catalog data access.
 * Mirrors: catalogue/detail.pl, cataloguing/addbiblio.pl, cataloguing/additem.pl,
 *          catalogue/merge.pl, authorities/authorities.pl, etc.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CatalogRepository {

    private final JdbcTemplate jdbc;

    // ── Bibliographic records ──────────────────────────────────────────────────

    private static final RowMapper<BiblioDto> BIBLIO_MAPPER = (rs, rn) -> {
        BiblioDto dto = new BiblioDto();
        dto.setBiblioId(rs.getLong("biblionumber"));
        dto.setBiblionumber(rs.getString("biblionumber"));
        dto.setTitle(rs.getString("title"));
        dto.setAuthor(rs.getString("author"));
        dto.setFrameworkCode(rs.getString("frameworkcode"));
        dto.setDateCreated(rs.getObject("datecreated", java.time.LocalDateTime.class));
        dto.setTimestamp(rs.getObject("timestamp", java.time.LocalDateTime.class));
        dto.setNotes(rs.getString("notes"));
        dto.setMedium(rs.getString("medium"));
        dto.setSubtitle(rs.getString("subtitle"));
        dto.setPartNumber(rs.getString("part_number"));
        dto.setPartName(rs.getString("part_name"));
        dto.setAbstractNote(rs.getString("abstract"));
        return dto;
    };

    public Page<BiblioDto> findAllBiblios(String query, Pageable pageable) {
        log.debug("Entering findAllBiblios - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE title ILIKE ? OR author ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank())
                ? new Object[]{"%" + query + "%", "%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM biblio" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<BiblioDto> list = jdbc.query(
                "SELECT * FROM biblio" + where + " ORDER BY biblionumber DESC LIMIT ? OFFSET ?",
                BIBLIO_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<BiblioDto> findBiblioById(Long biblioId) {
        log.debug("Entering findBiblioById - {}", biblioId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM biblio WHERE biblionumber = ?", BIBLIO_MAPPER, biblioId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public BiblioDto insertBiblio(BiblioDto dto) {
        log.debug("Entering insertBiblio - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO biblio (title, author, frameworkcode, notes, medium, subtitle, part_number, part_name, abstract, datecreated)
                    VALUES (?,?,?,?,?,?,?,?,?,NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getAuthor());
            ps.setString(3, dto.getFrameworkCode());
            ps.setString(4, dto.getNotes());
            ps.setString(5, dto.getMedium());
            ps.setString(6, dto.getSubtitle());
            ps.setString(7, dto.getPartNumber());
            ps.setString(8, dto.getPartName());
            ps.setString(9, dto.getAbstractNote());
            return ps;
        }, kh);
        dto.setBiblioId(((Number) kh.getKeys().get("biblionumber")).longValue());
        return dto;
    }

    public BiblioDto updateBiblio(Long biblioId, BiblioDto dto) {
        log.debug("Entering updateBiblio - {}, {}", biblioId, dto);
        jdbc.update("""
                UPDATE biblio SET title=?, author=?, frameworkcode=?, notes=?, medium=?, subtitle=?, part_number=?, part_name=?, abstract=?
                WHERE biblionumber=?
                """,
                dto.getTitle(), dto.getAuthor(), dto.getFrameworkCode(), dto.getNotes(), dto.getMedium(),
                dto.getSubtitle(), dto.getPartNumber(), dto.getPartName(), dto.getAbstractNote(), biblioId);
        dto.setBiblioId(biblioId);
        return dto;
    }

    public void deleteBiblio(Long biblioId) {
        log.debug("Entering deleteBiblio - {}", biblioId);
        jdbc.update("DELETE FROM biblio WHERE biblionumber = ?", biblioId);
    }

    public BiblioDto mergeBiblios(Long targetBiblioId, Long fromBiblioId) {
        log.debug("Entering mergeBiblios - {}, {}", targetBiblioId, fromBiblioId);
        jdbc.update("UPDATE items SET biblionumber = ? WHERE biblionumber = ?", targetBiblioId, fromBiblioId);
        jdbc.update("UPDATE reserves SET biblionumber = ? WHERE biblionumber = ?", targetBiblioId, fromBiblioId);
        jdbc.update("DELETE FROM biblio WHERE biblionumber = ?", fromBiblioId);
        return findBiblioById(targetBiblioId).orElseThrow();
    }

    public Page<BiblioDto> findDeletedBiblios(String query, Pageable pageable) {
        log.debug("Entering findDeletedBiblios - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE title ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM deletedbiblio" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<BiblioDto> list = jdbc.query(
                "SELECT *, '' as medium, '' as subtitle, '' as part_number, '' as part_name, '' as abstract FROM deletedbiblio"
                        + where + " ORDER BY biblionumber DESC LIMIT ? OFFSET ?",
                BIBLIO_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<BiblioDto> findDeletedBiblioById(Long biblioId) {
        log.debug("Entering findDeletedBiblioById - {}", biblioId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT *, '' as medium, '' as subtitle, '' as part_number, '' as part_name, '' as abstract FROM deletedbiblio WHERE biblionumber = ?",
                    BIBLIO_MAPPER, biblioId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Items ──────────────────────────────────────────────────────────────────

    private static final RowMapper<ItemDto> ITEM_MAPPER = (rs, rn) -> {
        ItemDto dto = new ItemDto();
        dto.setItemId(rs.getLong("itemnumber"));
        dto.setBiblioId(rs.getLong("biblionumber"));
        dto.setBarcode(rs.getString("barcode"));
        dto.setDateAcquisitioned(rs.getObject("dateaccessioned", java.time.LocalDate.class));
        dto.setHomeLibraryId(rs.getString("homebranch"));
        dto.setHoldingLibraryId(rs.getString("holdingbranch"));
        dto.setCallNumber(rs.getString("itemcallnumber"));
        dto.setItemTypeId(rs.getString("itype"));
        dto.setNotForLoan(rs.getObject("notforloan", Integer.class));
        dto.setDamaged(rs.getObject("damaged", Integer.class));
        dto.setWithdrawn(rs.getObject("withdrawn", Integer.class));
        dto.setLost(rs.getObject("itemlost", Integer.class));
        dto.setNotes(rs.getString("itemnotes"));
        dto.setInternalNotes(rs.getString("itemnotes_nonpublic"));
        dto.setLocation(rs.getString("location"));
        dto.setUri(rs.getString("uri"));
        dto.setTimestamp(rs.getObject("timestamp", java.time.LocalDateTime.class));
        return dto;
    };

    public Page<ItemDto> findAllItems(String query, Pageable pageable) {
        log.debug("Entering findAllItems - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE barcode ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM items" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<ItemDto> list = jdbc.query(
                "SELECT * FROM items" + where + " ORDER BY itemnumber DESC LIMIT ? OFFSET ?",
                ITEM_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public List<ItemDto> findItemsByBiblioId(Long biblioId, Boolean bookable) {
        log.debug("Entering findItemsByBiblioId - {}, {}", biblioId, bookable);
        String sql = "SELECT * FROM items WHERE biblionumber = ?";
        if (Boolean.TRUE.equals(bookable)) {
            sql += " AND bookable = true";
        }
        return jdbc.query(sql + " ORDER BY itemnumber", ITEM_MAPPER, biblioId);
    }

    public Optional<ItemDto> findItemById(Long itemId) {
        log.debug("Entering findItemById - {}", itemId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM items WHERE itemnumber = ?", ITEM_MAPPER, itemId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public ItemDto insertItem(Long biblioId, ItemDto dto) {
        log.debug("Entering insertItem - {}, {}", biblioId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO items (biblionumber, barcode, dateaccessioned, homebranch, holdingbranch,
                        itemcallnumber, itype, notforloan, damaged, withdrawn, itemlost,
                        itemnotes, itemnotes_nonpublic, location, uri)
                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, biblioId);
            ps.setString(2, dto.getBarcode());
            ps.setObject(3, dto.getDateAcquisitioned());
            ps.setString(4, dto.getHomeLibraryId());
            ps.setString(5, dto.getHoldingLibraryId());
            ps.setString(6, dto.getCallNumber());
            ps.setString(7, dto.getItemTypeId());
            ps.setObject(8, dto.getNotForLoan());
            ps.setObject(9, dto.getDamaged());
            ps.setObject(10, dto.getWithdrawn());
            ps.setObject(11, dto.getLost());
            ps.setString(12, dto.getNotes());
            ps.setString(13, dto.getInternalNotes());
            ps.setString(14, dto.getLocation());
            ps.setString(15, dto.getUri());
            return ps;
        }, kh);
        dto.setItemId(((Number) kh.getKeys().get("itemnumber")).longValue());
        dto.setBiblioId(biblioId);
        return dto;
    }

    public ItemDto updateItem(Long biblioId, Long itemId, ItemDto dto) {
        log.debug("Entering updateItem - {}, {}, {}", biblioId, itemId, dto);
        jdbc.update("""
                UPDATE items SET barcode=?, homebranch=?, holdingbranch=?, itemcallnumber=?, itype=?,
                    notforloan=?, damaged=?, withdrawn=?, itemlost=?, itemnotes=?, itemnotes_nonpublic=?,
                    location=?, uri=?
                WHERE itemnumber=? AND biblionumber=?
                """,
                dto.getBarcode(), dto.getHomeLibraryId(), dto.getHoldingLibraryId(), dto.getCallNumber(),
                dto.getItemTypeId(), dto.getNotForLoan(), dto.getDamaged(), dto.getWithdrawn(),
                dto.getLost(), dto.getNotes(), dto.getInternalNotes(), dto.getLocation(), dto.getUri(),
                itemId, biblioId);
        dto.setItemId(itemId);
        dto.setBiblioId(biblioId);
        return dto;
    }

    public void deleteItem(Long itemId) {
        log.debug("Entering deleteItem - {}", itemId);
        jdbc.update("DELETE FROM items WHERE itemnumber = ?", itemId);
    }

    // ── Authorities ────────────────────────────────────────────────────────────

    private static final RowMapper<AuthorityDto> AUTHORITY_MAPPER = (rs, rn) -> {
        AuthorityDto dto = new AuthorityDto();
        dto.setAuthorityId(rs.getLong("authid"));
        dto.setAuthorityType(rs.getString("authtypecode"));
        dto.setHeadingText(rs.getString("authtrees"));
        dto.setCreatedAt(rs.getObject("create_date", java.time.LocalDateTime.class));
        dto.setUpdatedAt(rs.getObject("modification_date", java.time.LocalDateTime.class));
        return dto;
    };

    public Page<AuthorityDto> findAllAuthorities(String query, String type, Pageable pageable) {
        log.debug("Entering findAllAuthorities - {}, {}, {}", query, type, pageable);
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            where.append(" AND authtrees ILIKE ?");
            params.add("%" + query + "%");
        }
        if (type != null && !type.isBlank()) {
            where.append(" AND authtypecode = ?");
            params.add(type);
        }
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM auth_header" + where, Integer.class, params.toArray());
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());
        List<AuthorityDto> list = jdbc.query(
                "SELECT * FROM auth_header" + where + " ORDER BY authid DESC LIMIT ? OFFSET ?",
                AUTHORITY_MAPPER, params.toArray());
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<AuthorityDto> findAuthorityById(Long authorityId) {
        log.debug("Entering findAuthorityById - {}", authorityId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM auth_header WHERE authid = ?", AUTHORITY_MAPPER, authorityId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AuthorityDto insertAuthority(AuthorityDto dto) {
        log.debug("Entering insertAuthority - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO auth_header (authtypecode, authtrees, create_date, modification_date)
                    VALUES (?, ?, NOW(), NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getAuthorityType());
            ps.setString(2, dto.getHeadingText());
            return ps;
        }, kh);
        dto.setAuthorityId(((Number) kh.getKeys().get("authid")).longValue());
        return dto;
    }

    public AuthorityDto updateAuthority(Long authorityId, AuthorityDto dto) {
        log.debug("Entering updateAuthority - {}, {}", authorityId, dto);
        jdbc.update("""
                UPDATE auth_header SET authtypecode=?, authtrees=?, modification_date=NOW()
                WHERE authid=?
                """, dto.getAuthorityType(), dto.getHeadingText(), authorityId);
        dto.setAuthorityId(authorityId);
        return dto;
    }

    public void deleteAuthority(Long authorityId) {
        log.debug("Entering deleteAuthority - {}", authorityId);
        jdbc.update("DELETE FROM auth_header WHERE authid = ?", authorityId);
    }

    // ── Item Groups ────────────────────────────────────────────────────────────

    private static final RowMapper<ItemGroupDto> IG_MAPPER = (rs, rn) -> {
        ItemGroupDto dto = new ItemGroupDto();
        dto.setItemGroupId(rs.getLong("item_group_id"));
        dto.setBiblioId(rs.getLong("biblio_id"));
        dto.setDisplayTitle(rs.getString("display_title"));
        dto.setDescription(rs.getString("description"));
        dto.setDisplayOrder(rs.getObject("display_order", Integer.class));
        return dto;
    };

    public List<ItemGroupDto> findItemGroupsByBiblioId(Long biblioId) {
        log.debug("Entering findItemGroupsByBiblioId - {}", biblioId);
        return jdbc.query("SELECT * FROM item_groups WHERE biblio_id = ? ORDER BY display_order", IG_MAPPER, biblioId);
    }

    public Optional<ItemGroupDto> findItemGroupById(Long biblioId, Long groupId) {
        log.debug("Entering findItemGroupById - {}, {}", biblioId, groupId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM item_groups WHERE item_group_id = ? AND biblio_id = ?", IG_MAPPER, groupId, biblioId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public ItemGroupDto insertItemGroup(Long biblioId, ItemGroupDto dto) {
        log.debug("Entering insertItemGroup - {}, {}", biblioId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO item_groups (biblio_id, display_title, description, display_order)
                    VALUES (?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, biblioId);
            ps.setString(2, dto.getDisplayTitle());
            ps.setString(3, dto.getDescription());
            ps.setObject(4, dto.getDisplayOrder());
            return ps;
        }, kh);
        dto.setItemGroupId(((Number) kh.getKeys().get("item_group_id")).longValue());
        dto.setBiblioId(biblioId);
        return dto;
    }

    public ItemGroupDto updateItemGroup(Long biblioId, Long groupId, ItemGroupDto dto) {
        log.debug("Entering updateItemGroup - {}, {}, {}", biblioId, groupId, dto);
        jdbc.update("""
                UPDATE item_groups SET display_title=?, description=?, display_order=?
                WHERE item_group_id=? AND biblio_id=?
                """, dto.getDisplayTitle(), dto.getDescription(), dto.getDisplayOrder(), groupId, biblioId);
        dto.setItemGroupId(groupId);
        dto.setBiblioId(biblioId);
        return dto;
    }

    public void deleteItemGroup(Long biblioId, Long groupId) {
        log.debug("Entering deleteItemGroup - {}, {}", biblioId, groupId);
        jdbc.update("DELETE FROM item_groups WHERE item_group_id = ? AND biblio_id = ?", groupId, biblioId);
    }

    public void addItemToGroup(Long groupId, Long itemId) {
        log.debug("Entering addItemToGroup - {}, {}", groupId, itemId);
        jdbc.update("""
                INSERT INTO item_group_items (item_group_id, item_id) VALUES (?, ?) ON CONFLICT DO NOTHING
                """, groupId, itemId);
    }

    public void removeItemFromGroup(Long groupId, Long itemId) {
        log.debug("Entering removeItemFromGroup - {}, {}", groupId, itemId);
        jdbc.update("DELETE FROM item_group_items WHERE item_group_id = ? AND item_id = ?", groupId, itemId);
    }

    // ── Record Sources ────────────────────────────────────────────────────────

    private static final RowMapper<RecordSourceDto> RS_MAPPER = (rs, rn) -> {
        RecordSourceDto dto = new RecordSourceDto();
        dto.setRecordSourceId(rs.getLong("record_source_id"));
        dto.setName(rs.getString("name"));
        dto.setCanBeEdited(rs.getBoolean("can_be_edited"));
        return dto;
    };

    public Page<RecordSourceDto> findAllRecordSources(String query, Pageable pageable) {
        log.debug("Entering findAllRecordSources - {}, {}", query, pageable);
        String where = (query != null && !query.isBlank()) ? " WHERE name ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM record_sources" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<RecordSourceDto> list = jdbc.query(
                "SELECT * FROM record_sources" + where + " ORDER BY record_source_id LIMIT ? OFFSET ?",
                RS_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<RecordSourceDto> findRecordSourceById(Long id) {
        log.debug("Entering findRecordSourceById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM record_sources WHERE record_source_id = ?", RS_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public RecordSourceDto insertRecordSource(RecordSourceDto dto) {
        log.debug("Entering insertRecordSource - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO record_sources (name, can_be_edited) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setBoolean(2, Boolean.TRUE.equals(dto.getCanBeEdited()));
            return ps;
        }, kh);
        dto.setRecordSourceId(((Number) kh.getKeys().get("record_source_id")).longValue());
        return dto;
    }

    public RecordSourceDto updateRecordSource(Long id, RecordSourceDto dto) {
        log.debug("Entering updateRecordSource - {}, {}", id, dto);
        jdbc.update("UPDATE record_sources SET name=?, can_be_edited=? WHERE record_source_id=?",
                dto.getName(), dto.getCanBeEdited(), id);
        dto.setRecordSourceId(id);
        return dto;
    }

    public void deleteRecordSource(Long id) {
        log.debug("Entering deleteRecordSource - {}", id);
        jdbc.update("DELETE FROM record_sources WHERE record_source_id = ?", id);
    }

    // ── Bookings ──────────────────────────────────────────────────────────────

    private static final RowMapper<BookingDto> BOOKING_MAPPER = (rs, rn) -> {
        BookingDto dto = new BookingDto();
        dto.setBookingId(rs.getLong("booking_id"));
        dto.setBiblioId(rs.getLong("biblio_id"));
        dto.setItemId(rs.getObject("item_id", Long.class));
        dto.setPatronId(rs.getLong("patron_id"));
        dto.setPickupLibraryId(rs.getString("pickup_library_id"));
        dto.setStartDate(rs.getObject("start_date", java.time.LocalDateTime.class));
        dto.setEndDate(rs.getObject("end_date", java.time.LocalDateTime.class));
        dto.setStatus(rs.getString("status"));
        return dto;
    };

    public List<BookingDto> findBookingsByBiblioId(Long biblioId) {
        log.debug("Entering findBookingsByBiblioId - {}", biblioId);
        return jdbc.query("SELECT * FROM bookings WHERE biblio_id = ? ORDER BY start_date", BOOKING_MAPPER, biblioId);
    }

    public List<BookingDto> findBookingsByItemId(Long itemId) {
        log.debug("Entering findBookingsByItemId - {}", itemId);
        return jdbc.query("SELECT * FROM bookings WHERE item_id = ? ORDER BY start_date", BOOKING_MAPPER, itemId);
    }

    // ── Checkouts ─────────────────────────────────────────────────────────────

    private static final RowMapper<CheckoutDto> CHECKOUT_MAPPER = (rs, rn) -> {
        CheckoutDto dto = new CheckoutDto();
        dto.setCheckoutId(rs.getLong("issue_id"));
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setItemId(rs.getLong("itemnumber"));
        try { dto.setBiblioId(rs.getLong("biblionumber")); } catch (Exception ignored) {}
        dto.setIssueDate(rs.getObject("issuedate", java.time.LocalDateTime.class));
        dto.setDueDate(rs.getObject("date_due", java.time.LocalDateTime.class));
        dto.setBranchCode(rs.getString("branchcode"));
        return dto;
    };

    public List<CheckoutDto> findCheckoutsByBiblioId(Long biblioId, Boolean checkedIn) {
        log.debug("Entering findCheckoutsByBiblioId - {}, {}", biblioId, checkedIn);
        String table = Boolean.TRUE.equals(checkedIn) ? "old_issues i JOIN items it ON it.itemnumber=i.itemnumber" : "issues i JOIN items it ON it.itemnumber=i.itemnumber";
        return jdbc.query("SELECT i.*, it.biblionumber FROM " + table + " WHERE it.biblionumber = ? ORDER BY i.issuedate DESC",
                CHECKOUT_MAPPER, biblioId);
    }

    public List<CheckoutDto> findCheckoutsByItemId(Long itemId) {
        log.debug("Entering findCheckoutsByItemId - {}", itemId);
        return jdbc.query("SELECT *, NULL as biblionumber FROM issues WHERE itemnumber = ? ORDER BY issuedate DESC",
                CHECKOUT_MAPPER, itemId);
    }

    // ── Pickup Locations ──────────────────────────────────────────────────────

    private static final RowMapper<LibraryDto> LIBRARY_MAPPER = (rs, rn) -> {
        LibraryDto dto = new LibraryDto();
        dto.setLibraryId(rs.getString("branchcode"));
        dto.setName(rs.getString("branchname"));
        dto.setAddress1(rs.getString("branchaddress1"));
        dto.setCity(rs.getString("branchcity"));
        dto.setPhone(rs.getString("branchphone"));
        dto.setEmail(rs.getString("branchemail"));
        return dto;
    };

    public List<LibraryDto> findPickupLocations() {
        log.debug("Entering findPickupLocations");
        return jdbc.query("SELECT * FROM branches WHERE pickup_location = true ORDER BY branchname", LIBRARY_MAPPER);
    }

    // ── Ratings ───────────────────────────────────────────────────────────────

    public RatingResultDto setRating(Long biblioId, Long borrowerNumber, Integer rating) {
        log.debug("Entering setRating - {}, {}, {}", biblioId, borrowerNumber, rating);
        jdbc.update("""
                INSERT INTO ratings (borrowernumber, biblionumber, rating_value, timestamp)
                VALUES (?, ?, ?, NOW())
                ON CONFLICT (borrowernumber, biblionumber) DO UPDATE SET rating_value = EXCLUDED.rating_value, timestamp = NOW()
                """, borrowerNumber, biblioId, rating);
        Double avg = jdbc.queryForObject("SELECT AVG(rating_value) FROM ratings WHERE biblionumber = ?", Double.class, biblioId);
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM ratings WHERE biblionumber = ?", Integer.class, biblioId);
        RatingResultDto result = new RatingResultDto();
        result.setBiblioId(biblioId);
        result.setRating(rating);
        result.setAverage(avg);
        result.setCount(count);
        return result;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Object[] appendPaging(Object[] params, Pageable pageable) {
        log.debug("Entering appendPaging - {}, {}", params, pageable);
        Object[] pageParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pageParams, 0, params.length);
        pageParams[params.length] = pageable.getPageSize();
        pageParams[params.length + 1] = pageable.getOffset();
        return pageParams;
    }
}

