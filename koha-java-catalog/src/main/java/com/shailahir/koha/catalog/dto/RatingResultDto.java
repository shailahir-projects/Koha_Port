package com.shailahir.koha.catalog.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class RatingResultDto {
    private Long biblioId;
    private Number rating;
    private Double average;
    private Integer count;
}
