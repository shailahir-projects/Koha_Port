package com.shailahir.koha.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OtpTokenDeliveryDto {
    private String login;
    private String password;
}

