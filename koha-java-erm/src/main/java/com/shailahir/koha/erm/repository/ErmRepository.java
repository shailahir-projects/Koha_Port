package com.shailahir.koha.erm.repository;

import com.shailahir.koha.erm.dto.*;
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
 * Repository for ERM data access.
 * Mirrors: erm/*, Koha/ERM/Agreement.pm, Koha/ERM/License.pm, Koha/ERM/EHoldings/Package.pm
 */
@Repository
@RequiredArgsConstructor
public class ErmRepository {

    private final JdbcTemplate jdbc;

    // ── Agreements ────────────────────────────────────────────────────────────

    private static final RowMapper<AgreementDto> AGREEMENT_MAPPER = (rs, rn) -> {
        AgreementDto dto = new AgreementDto();
        dto.setAgreementId(rs.getLong("agreement_id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setStatus(rs.getString("status"));
        dto.setRenewalPriority(rs.getString("renewal_priority"));
        dto.setStartDate(rs.getObject("start_date", java.time.LocalDate.class));
        dto.setEndDate(rs.getObject("end_date", java.time.LocalDate.class));
        dto.setVendorId(rs.getString("vendor_id"));
        dto.setLicenseId(rs.getString("license_id"));
        return dto;
    };

    public Page<AgreementDto> findAllAgreements(String query, Pageable pageable) {
        String where = (query != null && !query.isBlank()) ? " WHERE name ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM erm_agreements" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<AgreementDto> list = jdbc.query(
                "SELECT * FROM erm_agreements" + where + " ORDER BY agreement_id DESC LIMIT ? OFFSET ?",
                AGREEMENT_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<AgreementDto> findAgreementById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM erm_agreements WHERE agreement_id = ?", AGREEMENT_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AgreementDto insertAgreement(AgreementDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO erm_agreements (name, description, status, renewal_priority, start_date, end_date, vendor_id, license_id)
                    VALUES (?,?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getDescription());
            ps.setString(3, dto.getStatus());
            ps.setString(4, dto.getRenewalPriority());
            ps.setObject(5, dto.getStartDate());
            ps.setObject(6, dto.getEndDate());
            ps.setString(7, dto.getVendorId());
            ps.setString(8, dto.getLicenseId());
            return ps;
        }, kh);
        dto.setAgreementId(((Number) kh.getKeys().get("agreement_id")).longValue());
        return dto;
    }

    public AgreementDto updateAgreement(Long id, AgreementDto dto) {
        jdbc.update("""
                UPDATE erm_agreements SET name=?, description=?, status=?, renewal_priority=?,
                    start_date=?, end_date=?, vendor_id=?, license_id=?
                WHERE agreement_id=?
                """, dto.getName(), dto.getDescription(), dto.getStatus(), dto.getRenewalPriority(),
                dto.getStartDate(), dto.getEndDate(), dto.getVendorId(), dto.getLicenseId(), id);
        dto.setAgreementId(id);
        return dto;
    }

    public void deleteAgreement(Long id) {
        jdbc.update("DELETE FROM erm_agreements WHERE agreement_id = ?", id);
    }

    // ── Licenses ──────────────────────────────────────────────────────────────

    private static final RowMapper<LicenseDto> LICENSE_MAPPER = (rs, rn) -> {
        LicenseDto dto = new LicenseDto();
        dto.setLicenseId(rs.getLong("license_id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setStatus(rs.getString("status"));
        dto.setType(rs.getString("type"));
        dto.setStartDate(rs.getObject("start_date", java.time.LocalDate.class));
        dto.setEndDate(rs.getObject("end_date", java.time.LocalDate.class));
        dto.setVendorId(rs.getString("vendor_id"));
        dto.setUrl(rs.getString("url"));
        dto.setNotes(rs.getString("notes"));
        return dto;
    };

    public Page<LicenseDto> findAllLicenses(String query, Pageable pageable) {
        String where = (query != null && !query.isBlank()) ? " WHERE name ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM erm_licenses" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<LicenseDto> list = jdbc.query(
                "SELECT * FROM erm_licenses" + where + " ORDER BY license_id DESC LIMIT ? OFFSET ?",
                LICENSE_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<LicenseDto> findLicenseById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM erm_licenses WHERE license_id = ?", LICENSE_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public LicenseDto insertLicense(LicenseDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO erm_licenses (name, description, status, type, start_date, end_date, vendor_id, url, notes)
                    VALUES (?,?,?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getDescription());
            ps.setString(3, dto.getStatus());
            ps.setString(4, dto.getType());
            ps.setObject(5, dto.getStartDate());
            ps.setObject(6, dto.getEndDate());
            ps.setString(7, dto.getVendorId());
            ps.setString(8, dto.getUrl());
            ps.setString(9, dto.getNotes());
            return ps;
        }, kh);
        dto.setLicenseId(((Number) kh.getKeys().get("license_id")).longValue());
        return dto;
    }

    public LicenseDto updateLicense(Long id, LicenseDto dto) {
        jdbc.update("""
                UPDATE erm_licenses SET name=?, description=?, status=?, type=?,
                    start_date=?, end_date=?, vendor_id=?, url=?, notes=?
                WHERE license_id=?
                """, dto.getName(), dto.getDescription(), dto.getStatus(), dto.getType(),
                dto.getStartDate(), dto.getEndDate(), dto.getVendorId(), dto.getUrl(), dto.getNotes(), id);
        dto.setLicenseId(id);
        return dto;
    }

    public void deleteLicense(Long id) {
        jdbc.update("DELETE FROM erm_licenses WHERE license_id = ?", id);
    }

    // ── eHoldings Packages ────────────────────────────────────────────────────

    private static final RowMapper<ErmPackageDto> PACKAGE_MAPPER = (rs, rn) -> {
        ErmPackageDto dto = new ErmPackageDto();
        dto.setPackageId(rs.getLong("package_id"));
        dto.setName(rs.getString("name"));
        dto.setContentType(rs.getString("content_type"));
        dto.setStatus(rs.getString("status"));
        dto.setVendorId(rs.getString("vendor_id"));
        dto.setPackageType(rs.getString("package_type"));
        dto.setIsSelected(rs.getBoolean("is_selected"));
        dto.setNotes(rs.getString("notes"));
        return dto;
    };

    public Page<ErmPackageDto> findAllPackages(String query, Pageable pageable) {
        String where = (query != null && !query.isBlank()) ? " WHERE name ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM erm_eholdings_packages" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<ErmPackageDto> list = jdbc.query(
                "SELECT * FROM erm_eholdings_packages" + where + " ORDER BY package_id DESC LIMIT ? OFFSET ?",
                PACKAGE_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<ErmPackageDto> findPackageById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM erm_eholdings_packages WHERE package_id = ?", PACKAGE_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public ErmPackageDto insertPackage(ErmPackageDto dto) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO erm_eholdings_packages (name, content_type, status, vendor_id, package_type, is_selected, notes)
                    VALUES (?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getContentType());
            ps.setString(3, dto.getStatus());
            ps.setString(4, dto.getVendorId());
            ps.setString(5, dto.getPackageType());
            ps.setObject(6, dto.getIsSelected());
            ps.setString(7, dto.getNotes());
            return ps;
        }, kh);
        dto.setPackageId(((Number) kh.getKeys().get("package_id")).longValue());
        return dto;
    }

    public ErmPackageDto updatePackage(Long id, ErmPackageDto dto) {
        jdbc.update("""
                UPDATE erm_eholdings_packages SET name=?, content_type=?, status=?, vendor_id=?,
                    package_type=?, is_selected=?, notes=?
                WHERE package_id=?
                """, dto.getName(), dto.getContentType(), dto.getStatus(), dto.getVendorId(),
                dto.getPackageType(), dto.getIsSelected(), dto.getNotes(), id);
        dto.setPackageId(id);
        return dto;
    }

    public void deletePackage(Long id) {
        jdbc.update("DELETE FROM erm_eholdings_packages WHERE package_id = ?", id);
    }

    private Object[] appendPaging(Object[] params, Pageable pageable) {
        Object[] pageParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pageParams, 0, params.length);
        pageParams[params.length] = pageable.getPageSize();
        pageParams[params.length + 1] = pageable.getOffset();
        return pageParams;
    }
}

