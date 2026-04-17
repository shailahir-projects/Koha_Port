package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PreservationTrainDto {
    private Long trainId;
    private String name;
    private String description;
    private LocalDate closedDate;
    private Long defaultProcessingId;
    private List<PreservationTrainItemDto> items;
}

