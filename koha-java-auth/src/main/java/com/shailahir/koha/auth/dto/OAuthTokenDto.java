package com.shailahir.koha.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OAuthTokenDto {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String scope;
}

