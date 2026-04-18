package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.Z3950SearchResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for z3950_search.pl
 *
 * <p>Provides a bibliographic lookup endpoint that proxies Z39.50 queries.
 * The Perl script z3950_search.pl triggers Koha's Z39.50 search daemon and
 * returns MARC records. In the Java microservice the Z39.50 protocol is
 * abstracted – a real implementation would call a Z39.50 client library
 * (e.g. YAZ4J) or a search facade service. This controller defines the
 * REST contract and returns a structured result; the actual protocol
 * integration is left as an infrastructure concern (configured via
 * application.properties).
 *
 * <pre>
 * GET /api/v1/acquisitions/z3950/search
 *     ?title=&amp;author=&amp;isbn=&amp;issn=&amp;server=
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/z3950")
@RequiredArgsConstructor
@Slf4j
public class Z3950SearchController {

    /**
     * Searches configured Z39.50 targets for bibliographic records.
     * Mirrors z3950_search.pl: accepts title, author, ISBN, ISSN and an
     * optional server name; returns matching records with MARC XML.
     *
     * <p>NOTE: actual Z39.50 network calls require a client library such as
     * YAZ4J. Inject a {@code Z3950ClientService} here when integrating.
     * This stub returns an empty result set so the REST contract is in place.
     *
     * @param title  optional title search term
     * @param author optional author search term
     * @param isbn   optional ISBN search term
     * @param issn   optional ISSN search term
     * @param server optional Z39.50 server name/alias configured in Koha's
     *               z3950servers table
     */
    @GetMapping(
            value = "/search",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Z3950SearchResultDto> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String issn,
            @RequestParam(required = false) String server) {

        log.debug("GET z3950/search title={} author={} isbn={} issn={} server={}",
                title, author, isbn, issn, server);

        // Build a query string representation (mirrors Koha's CCL query building)
        StringBuilder query = new StringBuilder();
        if (title  != null && !title.isBlank())  query.append("title=\"").append(title).append("\" ");
        if (author != null && !author.isBlank()) query.append("author=\"").append(author).append("\" ");
        if (isbn   != null && !isbn.isBlank())   query.append("isbn=\"").append(isbn).append("\" ");
        if (issn   != null && !issn.isBlank())   query.append("issn=\"").append(issn).append("\" ");

        // Stub: return empty results – plug in Z39.50 client here
        Z3950SearchResultDto result = Z3950SearchResultDto.builder()
                .query(query.toString().trim())
                .server(server)
                .totalHits(0)
                .results(List.of())
                .build();

        return ResponseEntity.ok(result);
    }
}

