package com.shailahir.koha.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SearchFilterDto {
    private Long searchFilterId;
    private Long patronId;
    private String name;
    private String query;
    private Boolean shared;
    private String libraryId;
    private Boolean active;
}
