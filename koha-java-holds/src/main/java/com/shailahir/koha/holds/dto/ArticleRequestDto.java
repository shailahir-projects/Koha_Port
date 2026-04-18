package com.shailahir.koha.holds.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class ArticleRequestDto {
    private Long articleRequestId;
    private Long patronId;
    private Long biblioId;
    private Long itemId;
    private String branchCode;
    private String status;
    private String title;
    private String author;
    private String volume;
    private String issue;
    private String date;
    private String pages;
    private String chapters;
    private String patron_notes;
    private String format;
    private String notes;
}

