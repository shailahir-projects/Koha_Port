package com.shailahir.koha.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JobDto {
    private Long jobId;
    private String type;
    private String status;
    private String data;
    private LocalDateTime createdDate;
    private LocalDateTime startedDate;
    private LocalDateTime completedDate;
    private String progress;
    private String error;
    private Long userId;
}

