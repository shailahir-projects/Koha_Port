package com.shailahir.koha.erm.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class ErmPackageDto {
    private Long packageId;
    private String name;
    private String contentType;
    private String status;
    private String vendorId;
    private String packageType;
    private Boolean isSelected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}

