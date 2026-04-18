package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class IllRequestDto {
    private Long illRequestId;
    private Long patronId;
    private Long biblioId;
    private String status;
    private String backend;
    private String branchcode;
    private String medium;
    private String placed;
    private String replied;
    private String completed;
    private String accessurl;
    private String cost;
    private String price_paid;
    private Long notesopac;
    private Long notesstaff;
    private String orderid;
    private String updated;
}

