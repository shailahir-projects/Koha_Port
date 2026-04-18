package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PreservationWaitingListItemDto {
    private Long itemId;
    private String barcode;
    private Long biblioId;
    private String branchcode;
    private java.time.LocalDateTime addedOn;
}
