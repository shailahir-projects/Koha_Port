package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HoldDto {
    private Long holdId;
    private Long patronId;
    private Long biblioId;
    private Long biblionumber;
    private Long itemId;
    private Long itemnumber;
    private String branchCode;
    private String branchcode;
    private String status;
    private java.time.LocalDate reservdate;
    private java.time.LocalDateTime reserveDate;
    private java.time.LocalDateTime waitingDate;
    private java.time.LocalDate waitingdate;
    private java.time.LocalDateTime expirationDate;
    private java.time.LocalDate expirationdate;
    private Integer priority;
    private String itemtype;
    private String pickupLibraryId;
    private String notes;
    private Boolean lowestPriority;
    private Boolean suspend;
    private java.time.LocalDate suspendUntil;
}

