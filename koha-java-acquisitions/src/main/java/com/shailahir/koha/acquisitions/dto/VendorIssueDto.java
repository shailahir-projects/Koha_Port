package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Vendor claim / issue row – mirrors vendor_issues.pl. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorIssueDto {

    @JsonProperty("issue_id")
    private Long issueId;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("booksellername")
    private String booksellername;

    @JsonProperty("type")
    private String type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("issue_date")
    private LocalDate issueDate;

    @JsonProperty("notes")
    private String notes;
}

