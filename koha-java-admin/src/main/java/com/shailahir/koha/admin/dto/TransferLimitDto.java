package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferLimitDto {
    private Long limitId;
    private String fromBranch;
    private String toBranch;
    private String itemtype;
}

