package com.shailahir.koha.auth.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.auth.dto.*;
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
 * Repository for auth/identity provider data access.
 * Mirrors: Koha/Auth/Identity/Provider.pm and related Perl modules.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final JdbcTemplate jdbc;

    // ── Auth / Identity Providers ─────────────────────────────────────────────

    private static final RowMapper<AuthProviderDto> PROVIDER_MAPPER = (rs, rn) -> {
        AuthProviderDto dto = new AuthProviderDto();
        dto.setAuthProviderId(rs.getLong("identity_provider_id"));
        dto.setCode(rs.getString("code"));
        dto.setDescription(rs.getString("description"));
        dto.setProtocol(rs.getString("protocol"));
        dto.setConfig(rs.getString("config"));
        dto.setMappingConfig(rs.getString("mapping"));
        dto.setEnabled(rs.getBoolean("enabled"));
        dto.setAutoRegister(rs.getBoolean("auto_register"));
        dto.setUpdateOnAuth(rs.getBoolean("update_on_auth"));
        dto.setDefaultLibraryId(rs.getString("default_library_id"));
        dto.setDefaultCategory(rs.getString("default_categorycode"));
        return dto;
    };

    public Page<AuthProviderDto> findAllProviders(Pageable pageable) {
        log.debug("Entering findAllProviders - {}", pageable);
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM identity_providers", Integer.class);
        List<AuthProviderDto> list = jdbc.query(
                "SELECT * FROM identity_providers ORDER BY identity_provider_id LIMIT ? OFFSET ?",
                PROVIDER_MAPPER, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    public Optional<AuthProviderDto> findProviderById(Long id) {
        log.debug("Entering findProviderById - {}", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM identity_providers WHERE identity_provider_id = ?", PROVIDER_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AuthProviderDto insertProvider(AuthProviderDto dto) {
        log.debug("Entering insertProvider - {}", dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO identity_providers (code, description, protocol, config, mapping,
                        enabled, auto_register, update_on_auth, default_library_id, default_categorycode)
                    VALUES (?,?,?,?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getCode());
            ps.setString(2, dto.getDescription());
            ps.setString(3, dto.getProtocol());
            ps.setString(4, dto.getConfig());
            ps.setString(5, dto.getMappingConfig());
            ps.setObject(6, dto.getEnabled());
            ps.setObject(7, dto.getAutoRegister());
            ps.setObject(8, dto.getUpdateOnAuth());
            ps.setString(9, dto.getDefaultLibraryId());
            ps.setString(10, dto.getDefaultCategory());
            return ps;
        }, kh);
        dto.setAuthProviderId(((Number) kh.getKeys().get("identity_provider_id")).longValue());
        return dto;
    }

    public AuthProviderDto updateProvider(Long id, AuthProviderDto dto) {
        log.debug("Entering updateProvider - {}, {}", id, dto);
        jdbc.update("""
                UPDATE identity_providers SET code=?, description=?, protocol=?, config=?, mapping=?,
                    enabled=?, auto_register=?, update_on_auth=?, default_library_id=?, default_categorycode=?
                WHERE identity_provider_id=?
                """, dto.getCode(), dto.getDescription(), dto.getProtocol(), dto.getConfig(),
                dto.getMappingConfig(), dto.getEnabled(), dto.getAutoRegister(), dto.getUpdateOnAuth(),
                dto.getDefaultLibraryId(), dto.getDefaultCategory(), id);
        dto.setAuthProviderId(id);
        return dto;
    }

    public void deleteProvider(Long id) {
        log.debug("Entering deleteProvider - {}", id);
        jdbc.update("DELETE FROM identity_providers WHERE identity_provider_id = ?", id);
    }

    // ── Provider Domains ──────────────────────────────────────────────────────

    private static final RowMapper<AuthProviderDomainDto> DOMAIN_MAPPER = (rs, rn) -> {
        AuthProviderDomainDto dto = new AuthProviderDomainDto();
        dto.setDomainId(rs.getLong("identity_provider_domain_id"));
        dto.setAuthProviderId(rs.getLong("identity_provider_id"));
        dto.setDomain(rs.getString("domain"));
        dto.setAutoRegister(rs.getBoolean("auto_register"));
        dto.setUpdateOnAuth(rs.getBoolean("update_on_auth"));
        dto.setDefaultLibraryId(rs.getString("default_library_id"));
        dto.setDefaultCategory(rs.getString("default_categorycode"));
        dto.setEnabled(rs.getBoolean("enabled"));
        return dto;
    };

    public List<AuthProviderDomainDto> findDomainsByProviderId(Long providerId) {
        log.debug("Entering findDomainsByProviderId - {}", providerId);
        return jdbc.query(
                "SELECT * FROM identity_provider_domains WHERE identity_provider_id = ? ORDER BY identity_provider_domain_id",
                DOMAIN_MAPPER, providerId);
    }

    public Optional<AuthProviderDomainDto> findDomainById(Long providerId, Long domainId) {
        log.debug("Entering findDomainById - {}, {}", providerId, domainId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM identity_provider_domains WHERE identity_provider_id = ? AND identity_provider_domain_id = ?",
                    DOMAIN_MAPPER, providerId, domainId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public AuthProviderDomainDto insertDomain(Long providerId, AuthProviderDomainDto dto) {
        log.debug("Entering insertDomain - {}, {}", providerId, dto);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO identity_provider_domains (identity_provider_id, domain, auto_register,
                        update_on_auth, default_library_id, default_categorycode, enabled)
                    VALUES (?,?,?,?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, providerId);
            ps.setString(2, dto.getDomain());
            ps.setObject(3, dto.getAutoRegister());
            ps.setObject(4, dto.getUpdateOnAuth());
            ps.setString(5, dto.getDefaultLibraryId());
            ps.setString(6, dto.getDefaultCategory());
            ps.setObject(7, dto.getEnabled());
            return ps;
        }, kh);
        dto.setDomainId(((Number) kh.getKeys().get("identity_provider_domain_id")).longValue());
        dto.setAuthProviderId(providerId);
        return dto;
    }

    public AuthProviderDomainDto updateDomain(Long providerId, Long domainId, AuthProviderDomainDto dto) {
        log.debug("Entering updateDomain - {}, {}, {}", providerId, domainId, dto);
        jdbc.update("""
                UPDATE identity_provider_domains SET domain=?, auto_register=?, update_on_auth=?,
                    default_library_id=?, default_categorycode=?, enabled=?
                WHERE identity_provider_id=? AND identity_provider_domain_id=?
                """, dto.getDomain(), dto.getAutoRegister(), dto.getUpdateOnAuth(),
                dto.getDefaultLibraryId(), dto.getDefaultCategory(), dto.getEnabled(),
                providerId, domainId);
        dto.setDomainId(domainId);
        dto.setAuthProviderId(providerId);
        return dto;
    }

    public void deleteDomain(Long providerId, Long domainId) {
        log.debug("Entering deleteDomain - {}, {}", providerId, domainId);
        jdbc.update(
                "DELETE FROM identity_provider_domains WHERE identity_provider_id = ? AND identity_provider_domain_id = ?",
                providerId, domainId);
    }

    // ── Two-Factor ────────────────────────────────────────────────────────────

    public Optional<String> findTwoFactorSecret(Long borrowerNumber) {
        log.debug("Entering findTwoFactorSecret - {}", borrowerNumber);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT secret FROM borrower_attributes WHERE borrowernumber = ? AND code = 'TOTP_SECRET'",
                    String.class, borrowerNumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Password validation ───────────────────────────────────────────────────

    public boolean validatePassword(String userid, String password) {
        log.debug("Entering validatePassword - {}, {}", userid, password);
        try {
            Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM borrowers WHERE userid = ? AND password = crypt(?, password)",
                    Integer.class, userid, password);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
