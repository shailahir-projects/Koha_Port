package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketDto {
    private Long ticketId;
    private Long reporterId;
    private Long biblioId;
    private String title;
    private String body;
    private String status;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}

