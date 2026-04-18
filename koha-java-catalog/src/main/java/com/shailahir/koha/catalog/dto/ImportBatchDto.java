package com.shailahir.koha.catalog.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
@Data
@JacksonXmlRootElement
public class ImportBatchDto {
    private Long importBatchId;
    private String status;
    private String fileName;
}
