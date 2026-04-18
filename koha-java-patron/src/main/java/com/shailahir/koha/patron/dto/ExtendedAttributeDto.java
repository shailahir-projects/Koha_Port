package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExtendedAttributeDto {
    private Long extendedAttributeId;
    private Long patronId;
    private String code;
    private String value;
    private String attribute; // alias for value (DB column name)
}

