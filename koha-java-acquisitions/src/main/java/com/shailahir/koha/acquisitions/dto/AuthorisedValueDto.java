package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a single authorised value row.
 * Maps to a row from the authorised_values table (optionally filtered by
 * branch via authorised_values_branches).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorisedValueDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("category")
    private String category;

    @JsonProperty("authorised_value")
    private String authorisedValue;

    /** Staff-facing label (lib) */
    @JsonProperty("lib")
    private String lib;

    /** OPAC-facing label (lib_opac) */
    @JsonProperty("lib_opac")
    private String libOpac;

    /** Image file name, if any */
    @JsonProperty("image_url")
    private String imageUrl;
}

