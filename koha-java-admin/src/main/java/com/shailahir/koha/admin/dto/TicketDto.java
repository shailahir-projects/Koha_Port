package com.shailahir.koha.admin.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class TicketDto {
    private Long ticketId;
    private Long reporterId;
    private Long biblioId;
    private Long biblionumber;
    private Long assigneeId;
    private String title;
    private String body;
    private String status;
    private LocalDateTime creationDate;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private LocalDateTime updatedDate;
}

