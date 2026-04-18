package com.shailahir.koha.serials.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NumberingPatternDto {
    private Long id;
    private String label;
    private String description;
    private String numberingmethod;
    private String label1;
    private Integer add1;
    private Integer every1;
    private Integer whenmorethan1;
    private Integer setto1;
    private String numbering1;
    private String label2;
    private Integer add2;
    private Integer every2;
    private Integer whenmorethan2;
    private Integer setto2;
    private String numbering2;
    private String label3;
    private Integer add3;
    private Integer every3;
    private Integer whenmorethan3;
    private Integer setto3;
    private String numbering3;
}

