package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExtendedAttributeTypeDto {
    private Long extendedAttributeTypeId;
    private String code;
    private String description;
    private String repeatable;
    private String unique_id;
    private String is_date;
    private String authorised_value_category;
    private String opac_display;
    private String opac_editable;
    private String staff_searchable;
}

