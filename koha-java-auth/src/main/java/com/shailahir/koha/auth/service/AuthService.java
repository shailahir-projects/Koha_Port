package com.shailahir.koha.auth.service;

import com.shailahir.koha.auth.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthService {
    void sendOtpToken(OtpTokenDeliveryDto body);
    TwoFactorRegistrationDto getTwoFactorRegistration();
    void verifyTwoFactorRegistration(String token);
    Page<AuthProviderDto> listAuthProviders(Pageable pageable);
    AuthProviderDto addAuthProvider(AuthProviderDto dto);
    AuthProviderDto getAuthProvider(Long id);
    AuthProviderDto updateAuthProvider(Long id, AuthProviderDto dto);
    void deleteAuthProvider(Long id);
    List<AuthProviderDomainDto> listAuthProviderDomains(Long providerId);
    AuthProviderDomainDto addAuthProviderDomain(Long providerId, AuthProviderDomainDto dto);
    AuthProviderDomainDto getAuthProviderDomain(Long providerId, Long domainId);
    void deleteAuthProviderDomain(Long providerId, Long domainId);
    Page<AuthProviderDto> listIdentityProviders(Pageable pageable);
    AuthProviderDto addIdentityProvider(AuthProviderDto dto);
    AuthProviderDto getIdentityProvider(Long id);
    AuthProviderDto updateIdentityProvider(Long id, AuthProviderDto dto);
    void deleteIdentityProvider(Long id);
    List<AuthProviderDomainDto> listIdentityProviderDomains(Long providerId);
    AuthProviderDomainDto addIdentityProviderDomain(Long providerId, AuthProviderDomainDto dto);
    AuthProviderDomainDto getIdentityProviderDomain(Long providerId, Long domainId);
    AuthProviderDomainDto updateIdentityProviderDomain(Long providerId, Long domainId, AuthProviderDomainDto dto);
    void deleteIdentityProviderDomain(Long providerId, Long domainId);
    boolean validatePassword(PasswordValidationDto dto);
    OAuthTokenDto getOAuthToken(String grantType, String clientId, String clientSecret, String scope);
    void loginOAuthClient(String providerCode, String interfaceName);
    void loginOAuthClientPublic(String providerCode, String interfaceName);
}

