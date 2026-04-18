package com.shailahir.koha.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SavedReportDto {
    private Long reportId;
    private String reportName;
    private String type;
    private String notes;
    private String sqlText;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String group;
    private String subGroup;
}

