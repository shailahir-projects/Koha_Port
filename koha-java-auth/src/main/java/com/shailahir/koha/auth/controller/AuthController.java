package com.shailahir.koha.auth.controller;
import lombok.extern.slf4j.Slf4j;
import com.shailahir.koha.auth.dto.LoginRequest;
import com.shailahir.koha.auth.dto.TokenResponse;
import com.shailahir.koha.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping(value = "/auth/login", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        log.debug("Entering login - {}", request);
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping(value = "/oauth/token", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TokenResponse> oauthToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password) {
        log.debug("Entering oauthToken - {}, {}, {}, {}, {}", grantType, clientId, clientSecret, username, password);
        return ResponseEntity.ok(authService.oauthToken(grantType, clientId, clientSecret, username, password));
    }
}
