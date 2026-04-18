package com.shailahir.koha.catalog.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class BiblioDto {
    private Long biblioId;
    private String title;
    private String author;
    private String isbn;
    private String issn;
    private String publisherCode;
    private String copyrightDate;
    private String biblionumber;
    private String frameworkCode;
    private LocalDateTime dateCreated;
    private LocalDateTime timestamp;
    private String serial;
    private String notes;
    private String medium;
    private String subtitle;
    private String partNumber;
    private String partName;
    private String abstractNote;
}

