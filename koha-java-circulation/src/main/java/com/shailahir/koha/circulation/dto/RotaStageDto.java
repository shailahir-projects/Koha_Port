package com.shailahir.koha.circulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RotaStageDto {
    private Long stageId;
    private Long rotaId;
    private String branchCode;
    private Integer duration;
    private Integer position;
}

