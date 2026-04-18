package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of a MARC-import order creation run.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class MarcImportResult {

    /** Number of records successfully imported and turned into orders */
    @JsonProperty("imported")
    private int imported;

    /** Number of records skipped (duplicate / not selected) */
    @JsonProperty("skipped")
    private int skipped;

    /** Whether any duplicates were detected inside the batch */
    @JsonProperty("duplicates_in_batch")
    private boolean duplicatesInBatch;

    /** Whether the import batch was marked 'imported' */
    @JsonProperty("batch_fully_imported")
    private boolean batchFullyImported;

    /** Order numbers that were created */
    @JsonProperty("created_ordernumbers")
    private List<Long> createdOrdernumbers;
}

