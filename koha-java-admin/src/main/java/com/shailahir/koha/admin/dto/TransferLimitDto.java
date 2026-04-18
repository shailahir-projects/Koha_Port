package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferLimitDto {
    private Long limitId;
    private Long transferLimitId;
    private String fromBranch;
    private String fromLibraryId;
    private String toBranch;
    private String toLibraryId;
    private String itemtype;
}

