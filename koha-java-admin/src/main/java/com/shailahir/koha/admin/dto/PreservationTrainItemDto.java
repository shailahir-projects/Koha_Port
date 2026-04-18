package com.shailahir.koha.admin.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class PreservationTrainItemDto {
    private Long trainItemId;
    private Long trainId;
    private Long itemId;
    private Long processingId;
    private LocalDateTime addedOn;
    private LocalDateTime removedOn;
    private List<Map<String, Object>> attributes;
    private Long userNotes;
}
