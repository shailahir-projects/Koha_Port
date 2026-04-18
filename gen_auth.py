import os
base_dir = r"C:\PHASE2\Koha_Port\koha-java-auth\src\main\java\com\shailahir\koha\auth"
# Generate AuthController
ctrl_dir = os.path.join(base_dir, "controller")
os.makedirs(ctrl_dir, exist_ok=True)
with open(os.path.join(ctrl_dir, "AuthController.java"), 'w') as f:
    f.write("""package com.shailahir.koha.auth.controller;
import com.shailahir.koha.auth.dto.LoginRequest;
import com.shailahir.koha.auth.dto.TokenResponse;
import com.shailahir.koha.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping(value = "/auth/login", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping(value = "/oauth/token", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TokenResponse> oauthToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password) {
        return ResponseEntity.ok(authService.oauthToken(grantType, clientId, clientSecret, username, password));
    }
}
""")
# Generate DTOs
dto_dir = os.path.join(base_dir, "dto")
os.makedirs(dto_dir, exist_ok=True)
with open(os.path.join(dto_dir, "LoginRequest.java"), 'w') as f:
    f.write("""package com.shailahir.koha.auth.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
@Data
@JacksonXmlRootElement
public class LoginRequest {
    private String username;
    private String password;
}
""")
with open(os.path.join(dto_dir, "TokenResponse.java"), 'w') as f:
    f.write("""package com.shailahir.koha.auth.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
}
""")
# Generate Service
svc_dir = os.path.join(base_dir, "service")
os.makedirs(svc_dir, exist_ok=True)
with open(os.path.join(svc_dir, "AuthService.java"), 'w') as f:
    f.write("""package com.shailahir.koha.auth.service;
import com.shailahir.koha.auth.dto.LoginRequest;
import com.shailahir.koha.auth.dto.TokenResponse;
public interface AuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse oauthToken(String grantType, String clientId, String clientSecret, String username, String password);
}
""")
# Generate Service Impl
impl_dir = os.path.join(svc_dir, "impl")
os.makedirs(impl_dir, exist_ok=True)
with open(os.path.join(impl_dir, "AuthServiceImpl.java"), 'w') as f:
    f.write("""package com.shailahir.koha.auth.service.impl;
import com.shailahir.koha.auth.dto.LoginRequest;
import com.shailahir.koha.auth.dto.TokenResponse;
import com.shailahir.koha.auth.service.AuthService;
import com.shailahir.koha.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    @Override
    public TokenResponse login(LoginRequest request) {
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
        // Simplified implementation
        LoginRequest req = new LoginRequest();
        req.setUsername(username != null ? username : clientId);
        req.setPassword(password != null ? password : clientSecret);
        return login(req);
    }
}
""")
