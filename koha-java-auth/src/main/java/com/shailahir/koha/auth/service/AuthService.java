package com.shailahir.koha.auth.service;
import com.shailahir.koha.auth.dto.LoginRequest;
import com.shailahir.koha.auth.dto.TokenResponse;
public interface AuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse oauthToken(String grantType, String clientId, String clientSecret, String username, String password);
}
