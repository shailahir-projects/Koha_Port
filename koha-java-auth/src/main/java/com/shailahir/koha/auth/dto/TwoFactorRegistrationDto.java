package com.shailahir.koha.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TwoFactorRegistrationDto {
    private String secret;
    private String qrCodeUri;
}

