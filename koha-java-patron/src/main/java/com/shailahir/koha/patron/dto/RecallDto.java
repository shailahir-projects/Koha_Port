package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RecallDto {
    private Long recallId;
    private Long patronId;
    private Long biblioId;
    private Long itemId;
    private String branchCode;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;
}

