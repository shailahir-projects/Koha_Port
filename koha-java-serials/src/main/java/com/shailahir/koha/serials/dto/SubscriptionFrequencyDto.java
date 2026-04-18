package com.shailahir.koha.serials.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubscriptionFrequencyDto {
    private Long id;
    private String description;
    private Integer displayorder;
    private String unit;
    private Integer unitsperissue;
    private Integer issuesperunit;
}

