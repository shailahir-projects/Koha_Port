package com.shailahir.koha.circulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RotaDto {
    private Long rotaId;
    private String title;
    private String description;
    private Boolean active;
    private Boolean cyclical;
    private List<RotaStageDto> stages;
}

