package com.shailahir.koha.reporting.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class ReportResultDto {
    private Long reportId;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private Integer totalRows;
    private String error;
}

