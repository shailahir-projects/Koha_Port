package com.shailahir.koha.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthProviderDto {
    private Long authProviderId;
    private String code;
    private String description;
    private String protocol;
    private String config;
}

