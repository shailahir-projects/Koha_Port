package com.shailahir.koha.circulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BookingDto {
    private Long bookingId;
    private Long biblioId;
    private Long itemId;
    private Long patronId;
    private String pickupLibraryId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}

