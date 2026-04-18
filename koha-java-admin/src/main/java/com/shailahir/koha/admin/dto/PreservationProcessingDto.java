package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PreservationProcessingDto {
    private Long processingId;
    private String name;
    private String location;
    private List<Map<String, Object>> attributes;
}
