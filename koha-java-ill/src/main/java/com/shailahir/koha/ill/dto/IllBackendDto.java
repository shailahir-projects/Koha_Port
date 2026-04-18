package com.shailahir.koha.ill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IllBackendDto {
    private String backendId;
    private String name;
    private Long requestCount;
}

