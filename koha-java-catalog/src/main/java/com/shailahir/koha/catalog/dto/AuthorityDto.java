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
public class AuthorityDto {
    private Long authorityId;
    private String authorityType;
    private String headingText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
