package com.shailahir.koha.acquisitions.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.AuthorisedValueDto;
import com.shailahir.koha.acquisitions.repository.AuthorisedValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for authorised values lookup.
 * Ports ajax-getauthvaluedropbox.pl.
 *
 * <pre>
 *  GET /acquisitions/authorised-values?category=LOST[&branchcode=CPL][&default=1]
 * </pre>
 *
 * The Perl script returned an HTML {@code <select>} fragment;
 * this endpoint returns JSON so the frontend can render its own control.
 * The {@code default} parameter is echoed back in each item's {@code selected}
 * flag so the consumer knows which value should be pre-selected.
 */
@Slf4j
@RestController
@RequestMapping("/acquisitions/authorised-values")
@RequiredArgsConstructor
public class AuthorisedValueController {

    private final AuthorisedValueRepository authorisedValueRepository;

    /**
     * Returns the list of authorised values for a given category,
     * optionally filtered to those visible from a specific branch.
     *
     * <p>Query parameters:
     * <ul>
     *   <li>{@code category}   – required; e.g. {@code LOST}, {@code LOC}, {@code CCODE}</li>
     *   <li>{@code branchcode} – optional; limits to values visible from this branch
     *       (mirrors {@code $branch_limit} in the Perl script)</li>
     *   <li>{@code default}    – optional; the value that should be pre-selected
     *       (mirrors the {@code default} CGI param)</li>
     * </ul>
     *
     * <p>Each item in the response has a {@code selected} boolean set to {@code true}
     * when its {@code authorised_value} equals the {@code default} parameter —
     * exactly what the Perl script expressed with {@code selected="selected"}.
     *
     * @param category   authorised value category (required)
     * @param branchcode optional branch code
     * @param defaultVal optional default/pre-selected value
     * @return list of authorised values with a {@code selected} flag
     */
    @GetMapping
    public ResponseEntity<List<AuthorisedValueResponse>> getAuthorisedValues(
            @RequestParam("category") String category,
            @RequestParam(value = "branchcode", required = false) String branchcode,
            @RequestParam(value = "default", required = false) String defaultVal) {
        log.debug("Entering getAuthorisedValues - {}, {}, {}", category, branchcode, defaultVal);

        List<AuthorisedValueDto> values = authorisedValueRepository.findByCategory(category, branchcode);

        List<AuthorisedValueResponse> response = values.stream()
                .map(av -> new AuthorisedValueResponse(
                        av.getId(),
                        av.getCategory(),
                        av.getAuthorisedValue(),
                        av.getLib(),
                        av.getLibOpac(),
                        av.getImageUrl(),
                        defaultVal != null && defaultVal.equals(av.getAuthorisedValue())))
                .toList();

        return ResponseEntity.ok(response);
    }

    /** Wrapper that adds the {@code selected} flag to the base DTO. */
    public record AuthorisedValueResponse(
            Long id,
            String category,
            String authorised_value,
            String lib,
            String lib_opac,
            String image_url,
            boolean selected) {
        log.debug("Entering AuthorisedValueResponse - {}, {}, {}, {}, {}, {}, {}", id, category, authorised_value, lib, lib_opac, image_url, selected);
    }
}

