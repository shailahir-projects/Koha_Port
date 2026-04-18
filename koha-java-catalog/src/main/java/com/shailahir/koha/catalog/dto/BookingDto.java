package com.shailahir.koha.catalog.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class BookingDto {
    private Long bookingId;
    private Long biblioId;
    private Long itemId;
    private Long patronId;
    private String pickupLibraryId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private String status;
}

