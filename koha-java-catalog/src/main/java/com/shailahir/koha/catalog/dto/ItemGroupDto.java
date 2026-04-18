package com.shailahir.koha.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemGroupDto {
    private Long itemGroupId;
    private Long biblioId;
    private String displayTitle;
    private String description;
    private Integer displayOrder;
}
