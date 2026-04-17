package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DeskDto {
    private Long deskId;
    private String deskName;
    private String branchcode;
}

