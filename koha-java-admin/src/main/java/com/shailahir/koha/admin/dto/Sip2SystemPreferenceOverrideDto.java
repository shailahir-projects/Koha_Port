package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Sip2SystemPreferenceOverrideDto {
    private Long overrideId;
    private Long sipAccountId;
    private Long sip2AccountId;
    private String variable;
    private String preference;
    private String value;
}

