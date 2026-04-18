package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Sip2InstitutionDto {
    private Long sipInstitutionId;
    private Long sip2InstitutionId;
    private String institutionId;
    private String name;
    private String branchcode;
    private String policy;
}

