package com.shailahir.koha.erm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AgreementDto {
    private Long agreementId;
    private String name;
    private String description;
    private String status;
    private String renewalPriority;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String libraryId;
    private String vendorId;
    private String licenseId;
}

