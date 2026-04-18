package com.shailahir.koha.notification.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class NoticeTemplateDto {
    private Long id;
    private String code;
    private String name;
    private String title;
    private String content;
    private String contentHtml;
    private String moduleType;
    private String messageTransport;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Boolean isDefault;
}

