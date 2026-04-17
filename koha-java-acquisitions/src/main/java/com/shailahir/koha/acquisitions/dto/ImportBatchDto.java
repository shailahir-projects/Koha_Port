package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a row from the marc_import_batches table.
 * Used by GET /acquisitions/marc-import/batches.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportBatchDto {

    @JsonProperty("import_batch_id")
    private Long importBatchId;

    @JsonProperty("matcher_id")
    private Long matcherId;

    @JsonProperty("template_id")
    private Long templateId;

    @JsonProperty("branchcode")
    private String branchcode;

    @JsonProperty("num_records")
    private Integer numRecords;

    @JsonProperty("num_items")
    private Integer numItems;

    @JsonProperty("upload_timestamp")
    private LocalDateTime uploadTimestamp;

    @JsonProperty("overlay_action")
    private String overlayAction;

    @JsonProperty("nomatch_action")
    private String nomatchAction;

    @JsonProperty("item_action")
    private String itemAction;

    @JsonProperty("import_status")
    private String importStatus;

    @JsonProperty("batch_type")
    private String batchType;

    @JsonProperty("record_type")
    private String recordType;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("comments")
    private String comments;
}

