package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PreservationConfigDto {
    private Map<String, Object> config;
}

