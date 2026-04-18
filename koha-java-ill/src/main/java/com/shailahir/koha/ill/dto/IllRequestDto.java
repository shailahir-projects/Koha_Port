package com.shailahir.koha.ill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IllRequestDto {
    private Long illRequestId;
    private Long patronId;
    private String branchCode;
    private String status;
    private String type;
    private String borrowerNote;
    private String notesOpac;
    private String notesStaff;
    private LocalDateTime placed;
    private LocalDateTime updated;
    private LocalDate completedDate;
    private String authorisedValueCategory;
    private Long biblio;
    private String externalId;
    private String price;
    private String currency;
    private Boolean notesOpacPublic;
}

