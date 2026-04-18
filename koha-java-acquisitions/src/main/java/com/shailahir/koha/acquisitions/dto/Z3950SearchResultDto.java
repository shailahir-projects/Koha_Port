package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/** Z39.50 search result – mirrors z3950_search.pl. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class Z3950SearchResultDto {

    @JsonProperty("query")
    private String query;

    @JsonProperty("server")
    private String server;

    @JsonProperty("total_hits")
    private Integer totalHits;

    @JsonProperty("results")
    private List<Z3950RecordDto> results;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Z3950RecordDto {

        @JsonProperty("title")
        private String title;

        @JsonProperty("author")
        private String author;

        @JsonProperty("isbn")
        private String isbn;

        @JsonProperty("issn")
        private String issn;

        @JsonProperty("publisher")
        private String publisher;

        @JsonProperty("year")
        private String year;

        @JsonProperty("edition")
        private String edition;

        @JsonProperty("marc_xml")
        private String marcXml;

        @JsonProperty("server")
        private String server;
    }
}

