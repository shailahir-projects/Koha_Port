package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HoldDto {
    private Long holdId;
    private Long patronId;
    private Long biblioId;
    private Long itemId;
    private String branchCode;
    private String status;
    private LocalDateTime reserveDate;
    private LocalDateTime waitingDate;
    private LocalDateTime expirationDate;
    private Integer priority;
    private String itemtype;
    private String notes;
    private Boolean lowestPriority;
    private Boolean suspend;
    private LocalDateTime suspendUntil;
}

