package com.shailahir.koha.auth.service.impl;

import com.shailahir.koha.auth.dto.*;
import com.shailahir.koha.auth.repository.AuthRepository;
import com.shailahir.koha.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Auth service implementation.
 * Handles OAuth2 providers, identity providers, 2FA, and password validation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository repo;

    // ── OTP / 2FA ──────────────────────────────────────────────────────────────

    @Override
    public void sendOtpToken(OtpTokenDeliveryDto body) {
        // In production: generate TOTP and send via email/SMS
        // For now, log/no-op - actual delivery uses notification service
    }

    @Override
    public TwoFactorRegistrationDto getTwoFactorRegistration() {
        // Generate a new TOTP secret for registration
        String secret = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        String qrCodeUri = "otpauth://totp/Koha?secret=" + secret + "&issuer=Koha";
        return TwoFactorRegistrationDto.builder()
                .secret(secret)
                .qrCodeUri(qrCodeUri)
                .build();
    }

    @Override
    public void verifyTwoFactorRegistration(String token) {
        // In production: verify TOTP token against stored secret
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }
    }

    // ── Auth Providers ─────────────────────────────────────────────────────────

    @Override
    public Page<AuthProviderDto> listAuthProviders(Pageable pageable) {
        return repo.findAllProviders(pageable);
    }

    @Override
    public AuthProviderDto addAuthProvider(AuthProviderDto dto) {
        return repo.insertProvider(dto);
    }

    @Override
    public AuthProviderDto getAuthProvider(Long id) {
        return repo.findProviderById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auth provider not found"));
    }

    @Override
    public AuthProviderDto updateAuthProvider(Long id, AuthProviderDto dto) {
        repo.findProviderById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auth provider not found"));
        return repo.updateProvider(id, dto);
    }

    @Override
    public void deleteAuthProvider(Long id) {
        repo.deleteProvider(id);
    }

    @Override
    public List<AuthProviderDomainDto> listAuthProviderDomains(Long providerId) {
        return repo.findDomainsByProviderId(providerId);
    }

    @Override
    public AuthProviderDomainDto addAuthProviderDomain(Long providerId, AuthProviderDomainDto dto) {
        return repo.insertDomain(providerId, dto);
    }

    @Override
    public AuthProviderDomainDto getAuthProviderDomain(Long providerId, Long domainId) {
        return repo.findDomainById(providerId, domainId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain not found"));
    }

    @Override
    public void deleteAuthProviderDomain(Long providerId, Long domainId) {
        repo.deleteDomain(providerId, domainId);
    }

    // ── Identity Providers ─────────────────────────────────────────────────────

    @Override
    public Page<AuthProviderDto> listIdentityProviders(Pageable pageable) {
        return repo.findAllProviders(pageable);
    }

    @Override
    public AuthProviderDto addIdentityProvider(AuthProviderDto dto) {
        return repo.insertProvider(dto);
    }

    @Override
    public AuthProviderDto getIdentityProvider(Long id) {
        return repo.findProviderById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity provider not found"));
    }

    @Override
    public AuthProviderDto updateIdentityProvider(Long id, AuthProviderDto dto) {
        repo.findProviderById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity provider not found"));
        return repo.updateProvider(id, dto);
    }

    @Override
    public void deleteIdentityProvider(Long id) {
        repo.deleteProvider(id);
    }

    @Override
    public List<AuthProviderDomainDto> listIdentityProviderDomains(Long providerId) {
        return repo.findDomainsByProviderId(providerId);
    }

    @Override
    public AuthProviderDomainDto addIdentityProviderDomain(Long providerId, AuthProviderDomainDto dto) {
        return repo.insertDomain(providerId, dto);
    }

    @Override
    public AuthProviderDomainDto getIdentityProviderDomain(Long providerId, Long domainId) {
        return repo.findDomainById(providerId, domainId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain not found"));
    }

    @Override
    public AuthProviderDomainDto updateIdentityProviderDomain(Long providerId, Long domainId, AuthProviderDomainDto dto) {
        repo.findDomainById(providerId, domainId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain not found"));
        return repo.updateDomain(providerId, domainId, dto);
    }

    @Override
    public void deleteIdentityProviderDomain(Long providerId, Long domainId) {
        repo.deleteDomain(providerId, domainId);
    }

    // ── Password & OAuth ───────────────────────────────────────────────────────

    @Override
    public boolean validatePassword(PasswordValidationDto dto) {
        return repo.validatePassword(dto.getLogin(), dto.getPassword());
    }

    @Override
    public OAuthTokenDto getOAuthToken(String grantType, String clientId, String clientSecret, String scope) {
        // Simplified: in production this would validate client credentials and issue JWT
        if ("client_credentials".equals(grantType)) {
            return OAuthTokenDto.builder()
                    .accessToken(UUID.randomUUID().toString())
                    .tokenType("Bearer")
                    .expiresIn(3600L)
                    .scope(scope)
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported grant_type: " + grantType);
    }

    @Override
    public void loginOAuthClient(String providerCode, String interfaceName) {
        // Redirect to OAuth provider - handled at gateway level in production
    }

    @Override
    public void loginOAuthClientPublic(String providerCode, String interfaceName) {
        // Redirect to OAuth provider for OPAC
    }
}

