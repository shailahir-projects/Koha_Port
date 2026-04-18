package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class ClubHoldDto {
    private Long clubHoldId;
    private Long clubId;
    private Long biblioId;
    private Long biblionumber;
    private Long itemId;
    private String branchCode;
    private String itemtype;
}

