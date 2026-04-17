package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CityDto {
    private Long cityId;
    private String name;
    private String state;
    private String zipcode;
    private String country;
}

