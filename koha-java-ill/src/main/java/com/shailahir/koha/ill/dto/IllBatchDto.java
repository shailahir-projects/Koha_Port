package com.shailahir.koha.ill.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class IllBatchDto {
    private Long illBatchId;
    private String name;
    private String backend;
    private Long patronId;
    private String libraryId;
    private String statusCode;
}

