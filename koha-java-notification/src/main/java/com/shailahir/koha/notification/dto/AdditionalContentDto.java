package com.shailahir.koha.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdditionalContentDto {
    private Long id;
    private String idnew;
    private String code;
    private String title;
    private String content;
    private String contentHtml;
    private String category;
    private String location;
    private String lang;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

