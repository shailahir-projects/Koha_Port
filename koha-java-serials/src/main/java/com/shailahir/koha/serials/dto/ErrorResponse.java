package com.shailahir.koha.serials.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class ErrorResponse {
    private String error;
    private String errorCode;
}

