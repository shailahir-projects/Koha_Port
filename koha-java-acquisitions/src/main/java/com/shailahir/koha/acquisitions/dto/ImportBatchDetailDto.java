package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Summary of an import batch with its biblio records.
 * Returned by GET /acquisitions/marc-import/batches/{batch_id}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportBatchDetailDto {

    @JsonProperty("batch")
    private ImportBatchDto batch;

    @JsonProperty("biblios")
    private List<ImportBiblioDto> biblios;

    @JsonProperty("num_results")
    private Integer numResults;

    @JsonProperty("overlay_action")
    private String overlayAction;

    @JsonProperty("nomatch_action")
    private String nomatchAction;

    @JsonProperty("item_action")
    private String itemAction;

    @JsonProperty("current_matcher_id")
    private Long currentMatcherId;

    @JsonProperty("current_matcher_code")
    private String currentMatcherCode;

    @JsonProperty("current_matcher_description")
    private String currentMatcherDescription;
}

