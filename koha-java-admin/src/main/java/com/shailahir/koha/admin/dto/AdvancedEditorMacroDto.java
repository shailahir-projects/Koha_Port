package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdvancedEditorMacroDto {
    private Long macroId;
    private Long borrowernumber;
    private Long patronId;
    private String name;
    private String content;
    private Boolean shared;
}

