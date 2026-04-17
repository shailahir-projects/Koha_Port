package com.shailahir.koha.auth.controller;

import com.shailahir.koha.auth.dto.*;
import com.shailahir.koha.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── /auth/otp/token_delivery ──
    @PostMapping("/auth/otp/token_delivery")
    public ResponseEntity<Void> sendOtpToken(@RequestBody OtpTokenDeliveryDto body) {
        authService.sendOtpToken(body);
        return ResponseEntity.ok().build();
    }

    // ── /auth/two-factor/registration ──
    @GetMapping("/auth/two-factor/registration")
    public ResponseEntity<TwoFactorRegistrationDto> getTwoFactorRegistration() {
        return ResponseEntity.ok(authService.getTwoFactorRegistration());
    }

    @PostMapping("/auth/two-factor/registration/verification")
    public ResponseEntity<Void> verifyTwoFactorRegistration(@RequestBody(required = false) String token) {
        authService.verifyTwoFactorRegistration(token);
        return ResponseEntity.ok().build();
    }

    // ── /auth/providers ──
    @GetMapping("/auth/providers")
    public ResponseEntity<Page<AuthProviderDto>> listAuthProviders(Pageable pageable) {
        return ResponseEntity.ok(authService.listAuthProviders(pageable));
    }

    @PostMapping("/auth/providers")
    public ResponseEntity<AuthProviderDto> addAuthProvider(@RequestBody AuthProviderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.addAuthProvider(dto));
    }

    @GetMapping("/auth/providers/{auth_provider_id}")
    public ResponseEntity<AuthProviderDto> getAuthProvider(@PathVariable("auth_provider_id") Long id) {
        return ResponseEntity.ok(authService.getAuthProvider(id));
    }

    @PutMapping("/auth/providers/{auth_provider_id}")
    public ResponseEntity<AuthProviderDto> updateAuthProvider(@PathVariable("auth_provider_id") Long id, @RequestBody AuthProviderDto dto) {
        return ResponseEntity.ok(authService.updateAuthProvider(id, dto));
    }

    @DeleteMapping("/auth/providers/{auth_provider_id}")
    public ResponseEntity<Void> deleteAuthProvider(@PathVariable("auth_provider_id") Long id) {
        authService.deleteAuthProvider(id);
        return ResponseEntity.noContent().build();
    }

    // ── /auth/providers/{auth_provider_id}/domains ──
    @GetMapping("/auth/providers/{auth_provider_id}/domains")
    public ResponseEntity<List<AuthProviderDomainDto>> listAuthProviderDomains(@PathVariable("auth_provider_id") Long providerId) {
        return ResponseEntity.ok(authService.listAuthProviderDomains(providerId));
    }

    @PostMapping("/auth/providers/{auth_provider_id}/domains")
    public ResponseEntity<AuthProviderDomainDto> addAuthProviderDomain(@PathVariable("auth_provider_id") Long providerId, @RequestBody AuthProviderDomainDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.addAuthProviderDomain(providerId, dto));
    }

    @GetMapping("/auth/providers/{auth_provider_id}/domains/{auth_provider_domain_id}")
    public ResponseEntity<AuthProviderDomainDto> getAuthProviderDomain(@PathVariable("auth_provider_id") Long providerId, @PathVariable("auth_provider_domain_id") Long domainId) {
        return ResponseEntity.ok(authService.getAuthProviderDomain(providerId, domainId));
    }

    @DeleteMapping("/auth/providers/{auth_provider_id}/domains/{auth_provider_domain_id}")
    public ResponseEntity<Void> deleteAuthProviderDomain(@PathVariable("auth_provider_id") Long providerId, @PathVariable("auth_provider_domain_id") Long domainId) {
        authService.deleteAuthProviderDomain(providerId, domainId);
        return ResponseEntity.noContent().build();
    }

    // ── /auth/identity_providers ──
    @GetMapping("/auth/identity_providers")
    public ResponseEntity<Page<AuthProviderDto>> listIdentityProviders(Pageable pageable) {
        return ResponseEntity.ok(authService.listIdentityProviders(pageable));
    }

    @PostMapping("/auth/identity_providers")
    public ResponseEntity<AuthProviderDto> addIdentityProvider(@RequestBody AuthProviderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.addIdentityProvider(dto));
    }

    @GetMapping("/auth/identity_providers/{identity_provider_id}")
    public ResponseEntity<AuthProviderDto> getIdentityProvider(@PathVariable("identity_provider_id") Long id) {
        return ResponseEntity.ok(authService.getIdentityProvider(id));
    }

    @PutMapping("/auth/identity_providers/{identity_provider_id}")
    public ResponseEntity<AuthProviderDto> updateIdentityProvider(@PathVariable("identity_provider_id") Long id, @RequestBody AuthProviderDto dto) {
        return ResponseEntity.ok(authService.updateIdentityProvider(id, dto));
    }

    @DeleteMapping("/auth/identity_providers/{identity_provider_id}")
    public ResponseEntity<Void> deleteIdentityProvider(@PathVariable("identity_provider_id") Long id) {
        authService.deleteIdentityProvider(id);
        return ResponseEntity.noContent().build();
    }

    // ── /auth/identity_providers/{identity_provider_id}/domains ──
    @GetMapping("/auth/identity_providers/{identity_provider_id}/domains")
    public ResponseEntity<List<AuthProviderDomainDto>> listIdentityProviderDomains(@PathVariable("identity_provider_id") Long providerId) {
        return ResponseEntity.ok(authService.listIdentityProviderDomains(providerId));
    }

    @PostMapping("/auth/identity_providers/{identity_provider_id}/domains")
    public ResponseEntity<AuthProviderDomainDto> addIdentityProviderDomain(@PathVariable("identity_provider_id") Long providerId, @RequestBody AuthProviderDomainDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.addIdentityProviderDomain(providerId, dto));
    }

    @GetMapping("/auth/identity_providers/{identity_provider_id}/domains/{identity_provider_domain_id}")
    public ResponseEntity<AuthProviderDomainDto> getIdentityProviderDomain(@PathVariable("identity_provider_id") Long providerId, @PathVariable("identity_provider_domain_id") Long domainId) {
        return ResponseEntity.ok(authService.getIdentityProviderDomain(providerId, domainId));
    }

    @PutMapping("/auth/identity_providers/{identity_provider_id}/domains/{identity_provider_domain_id}")
    public ResponseEntity<AuthProviderDomainDto> updateIdentityProviderDomain(@PathVariable("identity_provider_id") Long providerId, @PathVariable("identity_provider_domain_id") Long domainId, @RequestBody AuthProviderDomainDto dto) {
        return ResponseEntity.ok(authService.updateIdentityProviderDomain(providerId, domainId, dto));
    }

    @DeleteMapping("/auth/identity_providers/{identity_provider_id}/domains/{identity_provider_domain_id}")
    public ResponseEntity<Void> deleteIdentityProviderDomain(@PathVariable("identity_provider_id") Long providerId, @PathVariable("identity_provider_domain_id") Long domainId) {
        authService.deleteIdentityProviderDomain(providerId, domainId);
        return ResponseEntity.noContent().build();
    }

    // ── /auth/password/validation ──
    @PostMapping("/auth/password/validation")
    public ResponseEntity<Boolean> validatePassword(@RequestBody PasswordValidationDto dto) {
        return ResponseEntity.ok(authService.validatePassword(dto));
    }

    // ── /oauth/token ──
    @PostMapping("/oauth/token")
    public ResponseEntity<OAuthTokenDto> getOAuthToken(
            @RequestParam(value = "grant_type", required = false) String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "scope", required = false) String scope) {
        return ResponseEntity.ok(authService.getOAuthToken(grantType, clientId, clientSecret, scope));
    }

    // ── /oauth/login/{provider_code}/{interface} ──
    @GetMapping("/oauth/login/{provider_code}/{interface}")
    public ResponseEntity<Void> loginOAuthClient(@PathVariable("provider_code") String providerCode, @PathVariable("interface") String interfaceName) {
        authService.loginOAuthClient(providerCode, interfaceName);
        return ResponseEntity.ok().build();
    }

    // ── /public/oauth/login/{provider_code}/{interface} ──
    @GetMapping("/public/oauth/login/{provider_code}/{interface}")
    public ResponseEntity<Void> loginOAuthClientPublic(@PathVariable("provider_code") String providerCode, @PathVariable("interface") String interfaceName) {
        authService.loginOAuthClientPublic(providerCode, interfaceName);
        return ResponseEntity.ok().build();
    }
}

