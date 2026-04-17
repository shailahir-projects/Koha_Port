package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthorisedValueDto {
    private Long authorisedValueId;
    private String category;
    private String authorisedValue;
    private String lib;
    private String libOpac;
    private String imageUrl;
}

