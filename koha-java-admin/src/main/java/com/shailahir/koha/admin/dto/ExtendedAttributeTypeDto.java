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
    private Boolean repeatableFlag;
    private String unique_id;
    private Boolean uniqueIdFlag;
    private String is_date;
    private String authorised_value_category;
    private String authorisedValueCategory;
    private String opac_display;
    private Boolean opacDisplay;
    private String opac_editable;
    private Boolean opacEditable;
    private String staff_searchable;
    private Boolean searchable;
    private String categoryCodes;
    private String passwordBlacklist;
    private String mandatoryIfNotPermission;
    private String libraryLimits;
    private Integer maxLength;
    private String normalizer;
}

