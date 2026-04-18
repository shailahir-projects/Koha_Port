package com.shailahir.koha.ill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IllRequestCommentDto {
    private Long commentId;
    private Long illRequestId;
    private Long patronId;
    private String comment;
    private LocalDateTime timestamp;
}

