package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HoldGroupDto {
    private Long holdGroupId;
    private Long patronId;
    private String branchcode;
    private java.util.List<Long> holdIds;
}

