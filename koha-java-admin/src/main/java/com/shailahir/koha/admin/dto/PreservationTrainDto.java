package com.shailahir.koha.admin.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class PreservationTrainDto {
    private Long trainId;
    private String name;
    private String description;
    private String branchcode;
    private Long defaultProcessingId;
    private LocalDate closedDate;
    private java.time.LocalDateTime createdDate;
    private java.time.LocalDateTime sentDate;
    private java.time.LocalDateTime receivedDate;
    private List<PreservationTrainItemDto> items;
}
