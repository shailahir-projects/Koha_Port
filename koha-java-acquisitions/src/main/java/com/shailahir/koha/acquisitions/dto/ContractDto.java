package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for a contract (aqcontract row).
 * Used when populating the contract selector on the basket header form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class ContractDto {

    @JsonProperty("contractnumber")
    private Long contractnumber;

    @JsonProperty("contractname")
    private String contractname;

    @JsonProperty("contractdescription")
    private String contractdescription;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("contractstartdate")
    private LocalDate contractstartdate;

    @JsonProperty("contractenddate")
    private LocalDate contractenddate;

    /** True when this is the contract currently attached to the basket */
    @JsonProperty("selected")
    private Boolean selected;
}

