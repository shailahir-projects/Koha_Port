package com.shailahir.koha.search.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class SearchFilterDto {
    private Long searchFilterId;
    private Long patronId;
    private String name;
    private String query;
    private Boolean shared;
    private String libraryId;
    private Boolean active;
}
