package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketUpdateDto {
    private Long ticketUpdateId;
    private Long updateId;
    private Long ticketId;
    private Long updaterId;
    private Long creatorId;
    private String message;
    private String newStatus;
    private LocalDateTime updateDate;
    private LocalDateTime createdDate;
}

