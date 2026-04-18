package com.shailahir.koha.catalog.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class ItemDto {
    private Long itemId;
    private Long biblioId;
    private String barcode;
    private String booksellerIdentifier;
    private LocalDate dateAcquisitioned;
    private String homeLibraryId;
    private String holdingLibraryId;
    private String callNumber;
    private String itemTypeId;
    private Integer notForLoan;
    private Integer damaged;
    private Integer withdrawn;
    private Integer lost;
    private String notes;
    private String internalNotes;
    private String publicNotes;
    private Integer renewals;
    private String location;
    private String uri;
    private LocalDateTime timestamp;
    private Boolean bookable;
}

