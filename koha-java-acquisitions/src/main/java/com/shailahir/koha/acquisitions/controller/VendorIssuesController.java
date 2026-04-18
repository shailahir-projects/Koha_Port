package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.VendorIssueDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vendor_issues.pl
 *
 * <p>Lists recorded issues/claims against a vendor.
 *
 * <pre>
 * GET /api/v1/acquisitions/vendors/{booksellerid}/issues
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class VendorIssuesController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns all recorded issues for a vendor.
     * Mirrors vendor_issues.pl: reads aqbookseller_issues joined with aqbooksellers.
     *
     * @param booksellerid the vendor identifier
     */
    @GetMapping(
            value = "/api/v1/acquisitions/vendors/{booksellerid}/issues",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<List<VendorIssueDto>> getVendorIssues(@PathVariable Long booksellerid) {
        log.debug("GET vendor-issues booksellerid={}", booksellerid);
        return ResponseEntity.ok(extRepo.findVendorIssues(booksellerid));
    }
}

