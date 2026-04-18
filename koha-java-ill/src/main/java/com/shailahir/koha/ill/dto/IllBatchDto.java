package com.shailahir.koha.ill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IllBatchDto {
    private Long illBatchId;
    private String name;
    private String backend;
    private Long patronId;
    private String libraryId;
    private String statusCode;
}

