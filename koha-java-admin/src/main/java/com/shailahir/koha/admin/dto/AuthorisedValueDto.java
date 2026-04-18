package com.shailahir.koha.admin.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class AuthorisedValueDto {
    private Long authorisedValueId;
    private String category;
    private String categoryName;
    private String authorisedValue;
    private String value;
    private String lib;
    private String description;
    private String libOpac;
    private String descriptionOpac;
    private String imageUrl;
    private String branchLimitation;
}

