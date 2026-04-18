package com.shailahir.koha.serials.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class SubscriptionFrequencyDto {
    private Long id;
    private String description;
    private Integer displayorder;
    private String unit;
    private Integer unitsperissue;
    private Integer issuesperunit;
}

