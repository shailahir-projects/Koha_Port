package com.shailahir.koha.serials.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SerialDto {
    private Long serialId;
    private Long subscriptionId;
    private Long biblioId;
    private String status;
    private LocalDate publishedDate;
    private LocalDate plannedDate;
    private String serialseq;
    private String notes;
    private String routingnotes;
}

