package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.VendorSummaryDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vendors.pl
 *
 * <p>Lists and searches vendors (booksellers) with summary statistics
 * (basket count, subscription count, contract count).
 *
 * <pre>
 * GET /api/v1/acquisitions/vendors[?name=&amp;active=]
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/vendors")
@RequiredArgsConstructor
@Slf4j
public class VendorsController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns a list of vendors matching the optional search criteria.
     * Mirrors vendors.pl: supports filtering by name (partial match) and active flag.
     *
     * @param name   optional vendor name substring filter
     * @param active optional active flag filter (true/false)
     */
    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<List<VendorSummaryDto>> listVendors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active) {

        log.debug("GET vendors name={} active={}", name, active);
        return ResponseEntity.ok(extRepo.findVendors(name, active));
    }
}

