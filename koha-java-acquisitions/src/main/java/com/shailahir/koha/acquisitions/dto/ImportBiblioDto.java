package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing a single biblio record within an import batch.
 * Mirrors one row from marc_import_records joined to marc_import_biblios.
 * Used by GET /acquisitions/marc-import/batches/{batch_id}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportBiblioDto {

    @JsonProperty("import_record_id")
    private Long importRecordId;

    @JsonProperty("import_batch_id")
    private Long importBatchId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("overlay_status")
    private String overlayStatus;

    @JsonProperty("matched_biblionumber")
    private Long matchedBiblionumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("issn")
    private String issn;

    @JsonProperty("publishercode")
    private String publishercode;

    @JsonProperty("publicationyear")
    private String publicationyear;

    /** Price extracted from the MARC record (e.g. field 020$c or 345) */
    @JsonProperty("price")
    private BigDecimal price;

    /** Quantity extracted from MARC record */
    @JsonProperty("quantity")
    private Integer quantity;

    /** Whether this record already exists in the catalogue */
    @JsonProperty("already_exists")
    private Boolean alreadyExists;
}

