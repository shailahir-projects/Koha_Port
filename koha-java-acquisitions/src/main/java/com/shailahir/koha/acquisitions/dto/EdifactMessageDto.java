package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for an EDIFACT message row (edifact_messages table).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class EdifactMessageDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("message_type")
    private String messageType;

    @JsonProperty("transfer_date")
    private LocalDateTime transferDate;

    @JsonProperty("vendor_id")
    private Long vendorId;

    @JsonProperty("vendor_name")
    private String vendorName;

    @JsonProperty("basketno")
    private Long basketno;

    @JsonProperty("status")
    private String status;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("deleted")
    private Boolean deleted;

    /** Raw EDI message content */
    @JsonProperty("raw_msg")
    private String rawMsg;
}

