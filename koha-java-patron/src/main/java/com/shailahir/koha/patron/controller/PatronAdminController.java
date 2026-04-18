package com.shailahir.koha.patron.controller;

import com.shailahir.koha.patron.dto.PatronDto;
import com.shailahir.koha.patron.service.PatronAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller for patron administrative operations.
 * Mirrors: members/discharge.pl, members/discharges.pl, members/merge-patrons.pl,
 *          members/setstatus.pl, members/members-update.pl, members/members-update-do.pl,
 *          members/update-child.pl, members/two_factor_auth.pl,
 *          members/patronimage.pl, members/member-flags.pl,
 *          members/mod_debarment.pl, members/deletemem.pl,
 *          members/members-home.pl, members/member.pl, members/moremember.pl
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class PatronAdminController {

    private final PatronAdminService patronAdminService;

    /** members/members-home.pl - patron search home */
    @GetMapping("/patrons/home")
    public ResponseEntity<Map<String, Object>> patronsHome() {
        return ResponseEntity.ok(patronAdminService.getHome());
    }

    /** members/discharge.pl - issue patron discharge letter */
    @PostMapping("/patrons/{patron_id}/discharge")
    public ResponseEntity<Map<String, Object>> issueDischarge(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronAdminService.issueDischarge(patronId));
    }

    /** members/discharges.pl - list pending discharge requests */
    @GetMapping("/patrons/discharges")
    public ResponseEntity<java.util.List<Map<String, Object>>> listDischarges(
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(patronAdminService.listDischarges(status));
    }

    /** members/merge-patrons.pl - merge two patron records */
    @PostMapping("/patrons/{patron_id}/merge")
    public ResponseEntity<PatronDto> mergePatrons(
            @PathVariable("patron_id") Long keepPatronId,
            @RequestParam("merge_with") Long deletePatronId) {
        return ResponseEntity.ok(patronAdminService.mergePatrons(keepPatronId, deletePatronId));
    }

    /** members/setstatus.pl - set patron debarment/lost status */
    @PutMapping("/patrons/{patron_id}/status")
    public ResponseEntity<Void> setStatus(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, Object> statusRequest) {
        patronAdminService.setStatus(patronId, statusRequest);
        return ResponseEntity.ok().build();
    }

    /** members/mod_debarment.pl - modify debarment entry */
    @PostMapping("/patrons/{patron_id}/debarment")
    public ResponseEntity<Void> addDebarment(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, String> debarmentRequest) {
        patronAdminService.addDebarment(patronId, debarmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/patrons/{patron_id}/debarment")
    public ResponseEntity<Void> removeDebarment(@PathVariable("patron_id") Long patronId) {
        patronAdminService.removeDebarment(patronId);
        return ResponseEntity.noContent().build();
    }

    /** members/member-flags.pl - manage patron permission flags */
    @PutMapping("/patrons/{patron_id}/flags")
    public ResponseEntity<Void> updateFlags(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, Long> flagRequest) {
        patronAdminService.updateFlags(patronId, flagRequest.get("flags"));
        return ResponseEntity.ok().build();
    }

    /** members/two_factor_auth.pl - two-factor authentication setup */
    @GetMapping("/patrons/{patron_id}/two_factor_auth")
    public ResponseEntity<Map<String, Object>> getTwoFactorAuth(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronAdminService.getTwoFactorAuthStatus(patronId));
    }

    @PostMapping("/patrons/{patron_id}/two_factor_auth")
    public ResponseEntity<Map<String, Object>> enrollTwoFactorAuth(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(patronAdminService.enrollTwoFactorAuth(patronId, request.get("secret"), request.get("pin")));
    }

    @DeleteMapping("/patrons/{patron_id}/two_factor_auth")
    public ResponseEntity<Void> disableTwoFactorAuth(@PathVariable("patron_id") Long patronId) {
        patronAdminService.disableTwoFactorAuth(patronId);
        return ResponseEntity.noContent().build();
    }

    /** members/patronimage.pl - patron photo */
    @GetMapping("/patrons/{patron_id}/image")
    public ResponseEntity<byte[]> getPatronImage(@PathVariable("patron_id") Long patronId) {
        byte[] image = patronAdminService.getPatronImage(patronId);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(image);
    }

    @PutMapping(value = "/patrons/{patron_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPatronImage(
            @PathVariable("patron_id") Long patronId,
            @RequestParam("image") MultipartFile image) {
        patronAdminService.uploadPatronImage(patronId, image);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/patrons/{patron_id}/image")
    public ResponseEntity<Void> deletePatronImage(@PathVariable("patron_id") Long patronId) {
        patronAdminService.deletePatronImage(patronId);
        return ResponseEntity.noContent().build();
    }

    /** members/update-child.pl - update child patron to adult */
    @PostMapping("/patrons/{patron_id}/update_category")
    public ResponseEntity<PatronDto> updatePatronCategory(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(patronAdminService.updateCategory(patronId, request.get("categorycode")));
    }

    /** members/members-update.pl / members-update-do.pl - handle pending updates from OPAC */
    @GetMapping("/patrons/pending_updates")
    public ResponseEntity<java.util.List<Map<String, Object>>> listPendingUpdates() {
        return ResponseEntity.ok(patronAdminService.listPendingUpdates());
    }

    @PostMapping("/patrons/{patron_id}/approve_update")
    public ResponseEntity<Void> approveUpdate(@PathVariable("patron_id") Long patronId) {
        patronAdminService.approveUpdate(patronId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/patrons/{patron_id}/pending_update")
    public ResponseEntity<Void> rejectUpdate(@PathVariable("patron_id") Long patronId) {
        patronAdminService.rejectUpdate(patronId);
        return ResponseEntity.noContent().build();
    }
}

