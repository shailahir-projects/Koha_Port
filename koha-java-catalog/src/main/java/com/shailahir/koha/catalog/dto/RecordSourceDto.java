package com.shailahir.koha.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordSourceDto {
    private Long recordSourceId;
    private String name;
    private Boolean canBeEdited;
    private Integer usageCount;
}

