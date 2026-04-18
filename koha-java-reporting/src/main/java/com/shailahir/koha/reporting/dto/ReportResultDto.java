package com.shailahir.koha.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportResultDto {
    private Long reportId;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private Integer totalRows;
    private String error;
}

