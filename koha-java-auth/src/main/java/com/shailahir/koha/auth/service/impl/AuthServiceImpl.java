package com.shailahir.koha.auth.service.impl;
import lombok.extern.slf4j.Slf4j;
import com.shailahir.koha.auth.dto.LoginRequest;
import com.shailahir.koha.auth.dto.TokenResponse;
import com.shailahir.koha.auth.service.AuthService;
import com.shailahir.koha.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    @Override
    public TokenResponse login(LoginRequest request) {
        log.debug("Entering login - {}", request);
        // In a real implementation, verify credentials against database here.
        UserDetails userDetails = new User(request.getUsername(), "", new ArrayList<>());
        String token = jwtUtil.generateToken(userDetails);
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600000L)
                .build();
    }
    @Override
    public TokenResponse oauthToken(String grantType, String clientId, String clientSecret, String username, String password) {
        log.debug("Entering oauthToken - {}, {}, [REDACTED], {}, [REDACTED]", grantType, clientId, username);
        // Simplified implementation
        LoginRequest req = new LoginRequest();
        req.setUsername(username != null ? username : clientId);
        req.setPassword(password != null ? password : clientSecret);
        return login(req);
    }
}
