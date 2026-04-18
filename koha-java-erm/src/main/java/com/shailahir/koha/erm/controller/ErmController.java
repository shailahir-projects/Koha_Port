package com.shailahir.koha.erm.controller;

import com.shailahir.koha.erm.dto.*;
import com.shailahir.koha.erm.service.ErmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ERM controller covering agreements, licenses, and eHoldings packages.
 * Mirrors: erm/*.pl, Koha/ERM/*.pm
 */
@RestController
@RequiredArgsConstructor
public class ErmController {

    private final ErmService ermService;

    // ── Agreements ────────────────────────────────────────────────────────────

    @GetMapping("/erm/agreements")
    public ResponseEntity<Page<AgreementDto>> listAgreements(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listAgreements(query, pageable));
    }

    @PostMapping("/erm/agreements")
    public ResponseEntity<AgreementDto> addAgreement(@RequestBody AgreementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addAgreement(dto));
    }

    @GetMapping("/erm/agreements/{agreement_id}")
    public ResponseEntity<AgreementDto> getAgreement(@PathVariable("agreement_id") Long id) {
        return ResponseEntity.ok(ermService.getAgreement(id));
    }

    @PutMapping("/erm/agreements/{agreement_id}")
    public ResponseEntity<AgreementDto> updateAgreement(@PathVariable("agreement_id") Long id, @RequestBody AgreementDto dto) {
        return ResponseEntity.ok(ermService.updateAgreement(id, dto));
    }

    @DeleteMapping("/erm/agreements/{agreement_id}")
    public ResponseEntity<Void> deleteAgreement(@PathVariable("agreement_id") Long id) {
        ermService.deleteAgreement(id);
        return ResponseEntity.noContent().build();
    }

    // ── Licenses ──────────────────────────────────────────────────────────────

    @GetMapping("/erm/licenses")
    public ResponseEntity<Page<LicenseDto>> listLicenses(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listLicenses(query, pageable));
    }

    @PostMapping("/erm/licenses")
    public ResponseEntity<LicenseDto> addLicense(@RequestBody LicenseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addLicense(dto));
    }

    @GetMapping("/erm/licenses/{license_id}")
    public ResponseEntity<LicenseDto> getLicense(@PathVariable("license_id") Long id) {
        return ResponseEntity.ok(ermService.getLicense(id));
    }

    @PutMapping("/erm/licenses/{license_id}")
    public ResponseEntity<LicenseDto> updateLicense(@PathVariable("license_id") Long id, @RequestBody LicenseDto dto) {
        return ResponseEntity.ok(ermService.updateLicense(id, dto));
    }

    @DeleteMapping("/erm/licenses/{license_id}")
    public ResponseEntity<Void> deleteLicense(@PathVariable("license_id") Long id) {
        ermService.deleteLicense(id);
        return ResponseEntity.noContent().build();
    }

    // ── eHoldings Packages ────────────────────────────────────────────────────

    @GetMapping("/erm/eholdings/packages")
    public ResponseEntity<Page<ErmPackageDto>> listPackages(
            @RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(ermService.listPackages(query, pageable));
    }

    @PostMapping("/erm/eholdings/packages")
    public ResponseEntity<ErmPackageDto> addPackage(@RequestBody ErmPackageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ermService.addPackage(dto));
    }

    @GetMapping("/erm/eholdings/packages/{package_id}")
    public ResponseEntity<ErmPackageDto> getPackage(@PathVariable("package_id") Long id) {
        return ResponseEntity.ok(ermService.getPackage(id));
    }

    @PutMapping("/erm/eholdings/packages/{package_id}")
    public ResponseEntity<ErmPackageDto> updatePackage(@PathVariable("package_id") Long id, @RequestBody ErmPackageDto dto) {
        return ResponseEntity.ok(ermService.updatePackage(id, dto));
    }

    @DeleteMapping("/erm/eholdings/packages/{package_id}")
    public ResponseEntity<Void> deletePackage(@PathVariable("package_id") Long id) {
        ermService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
