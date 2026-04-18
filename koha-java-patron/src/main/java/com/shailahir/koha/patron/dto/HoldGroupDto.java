package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class HoldGroupDto {
    private Long holdGroupId;
    private Long patronId;
    private String branchcode;
    private java.util.List<Long> holdIds;
}

