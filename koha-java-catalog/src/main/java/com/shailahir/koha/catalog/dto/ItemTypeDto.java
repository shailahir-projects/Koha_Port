package com.shailahir.koha.catalog.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
@Data
@JacksonXmlRootElement
public class ItemTypeDto {
    private String itemType;
    private String description;
}
