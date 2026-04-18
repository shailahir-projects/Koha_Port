package com.shailahir.koha.patron.controller;

import com.shailahir.koha.patron.dto.*;
import com.shailahir.koha.patron.service.PatronService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PatronController {

    private final PatronService patronService;

    // ── /patrons ──
    @GetMapping("/patrons", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<PatronDto>> listPatrons(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(patronService.listPatrons(query, pageable));
    }

    @PostMapping("/patrons", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PatronDto> addPatron(@RequestBody PatronDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronService.addPatron(dto));
    }

    @GetMapping("/patrons/{patron_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PatronDto> getPatron(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronService.getPatron(patronId));
    }

    @PutMapping("/patrons/{patron_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PatronDto> updatePatron(@PathVariable("patron_id") Long patronId, @RequestBody PatronDto dto) {
        return ResponseEntity.ok(patronService.updatePatron(patronId, dto));
    }

    @DeleteMapping("/patrons/{patron_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePatron(@PathVariable("patron_id") Long patronId) {
        patronService.deletePatron(patronId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patrons/{patron_id}/ill/requests", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<IllRequestDto>> getPatronIllRequests(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        return ResponseEntity.ok(patronService.getPatronIllRequests(patronId, pageable));
    }

    // ── /patron_categories ──
    @GetMapping("/patron_categories", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PatronCategoryDto>> listPatronCategories(Pageable pageable) {
        return ResponseEntity.ok(patronService.listPatronCategories(pageable));
    }

    // ── /patrons/{patron_id}/checkouts ──
    @GetMapping("/patrons/{patron_id}/checkouts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CheckoutDto>> getPatronCheckouts(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        return ResponseEntity.ok(patronService.getPatronCheckouts(patronId, pageable));
    }

    // ── /patrons/{patron_id}/extended_attributes ──
    @GetMapping("/patrons/{patron_id}/extended_attributes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ExtendedAttributeDto>> getPatronAttributes(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronService.getPatronAttributes(patronId));
    }

    @PostMapping("/patrons/{patron_id}/extended_attributes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ExtendedAttributeDto> addPatronAttribute(@PathVariable("patron_id") Long patronId, @RequestBody ExtendedAttributeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronService.addPatronAttribute(patronId, dto));
    }

    @PutMapping("/patrons/{patron_id}/extended_attributes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ExtendedAttributeDto>> overwritePatronAttributes(@PathVariable("patron_id") Long patronId, @RequestBody List<ExtendedAttributeDto> dtos) {
        return ResponseEntity.ok(patronService.overwritePatronAttributes(patronId, dtos));
    }

    @PutMapping("/patrons/{patron_id}/extended_attributes/{extended_attribute_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ExtendedAttributeDto> updatePatronAttribute(@PathVariable("patron_id") Long patronId, @PathVariable("extended_attribute_id") Long attrId, @RequestBody ExtendedAttributeDto dto) {
        return ResponseEntity.ok(patronService.updatePatronAttribute(patronId, attrId, dto));
    }

    @DeleteMapping("/patrons/{patron_id}/extended_attributes/{extended_attribute_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePatronAttribute(@PathVariable("patron_id") Long patronId, @PathVariable("extended_attribute_id") Long attrId) {
        patronService.deletePatronAttribute(patronId, attrId);
        return ResponseEntity.noContent().build();
    }

    // ── /patrons/{patron_id}/holds ──
    @GetMapping("/patrons/{patron_id}/holds", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<HoldDto>> getPatronHolds(@PathVariable("patron_id") Long patronId, Pageable pageable) {
        return ResponseEntity.ok(patronService.getPatronHolds(patronId, pageable));
    }

    // ── /patrons/{patron_id}/hold_groups ──
    @GetMapping("/patrons/{patron_id}/hold_groups", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<HoldGroupDto>> getPatronHoldGroups(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronService.getPatronHoldGroups(patronId));
    }

    @PostMapping("/patrons/{patron_id}/hold_groups", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<HoldGroupDto> addPatronHoldGroup(@PathVariable("patron_id") Long patronId, @RequestBody HoldGroupDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronService.addPatronHoldGroup(patronId, dto));
    }

    @DeleteMapping("/patrons/{patron_id}/hold_groups/{hold_group_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePatronHoldGroup(@PathVariable("patron_id") Long patronId, @PathVariable("hold_group_id") Long holdGroupId) {
        patronService.deletePatronHoldGroup(patronId, holdGroupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/patrons/{patron_id}/hold_groups/{hold_group_id}/cancel", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> cancelPatronHoldGroup(@PathVariable("patron_id") Long patronId, @PathVariable("hold_group_id") Long holdGroupId) {
        patronService.cancelPatronHoldGroup(patronId, holdGroupId);
        return ResponseEntity.ok().build();
    }

    // ── /patrons/{patron_id}/password ──
    @PutMapping("/patrons/{patron_id}/password", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setPatronPassword(@PathVariable("patron_id") Long patronId, @RequestBody PasswordDto dto) {
        patronService.setPatronPassword(patronId, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/patrons/{patron_id}/password/expiration_date", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setPatronPasswordExpiration(@PathVariable("patron_id") Long patronId, @RequestBody(required = false) LocalDate expirationDate) {
        patronService.setPatronPasswordExpiration(patronId, expirationDate);
        return ResponseEntity.ok().build();
    }

    // ── /patrons/{patron_id}/recalls ──
    @GetMapping("/patrons/{patron_id}/recalls", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<RecallDto>> getPatronRecalls(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronService.getPatronRecalls(patronId));
    }

    // ── /clubs/{club_id}/holds ──
    @PostMapping("/clubs/{club_id}/holds", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ClubHoldDto> addClubHold(@PathVariable("club_id") Long clubId, @RequestBody ClubHoldDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronService.addClubHold(clubId, dto));
    }

    // ── /public/lists ──
    @GetMapping("/public/lists", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<VirtualShelfDto>> listPublicLists(Pageable pageable) {
        return ResponseEntity.ok(patronService.listPublicLists(pageable));
    }

    // ── Public patron endpoints ──
    @PutMapping("/public/patrons/{patron_id}/password", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setPatronPasswordPublic(@PathVariable("patron_id") Long patronId, @RequestBody PasswordDto dto) {
        patronService.setPatronPasswordPublic(patronId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/patrons/{patron_id}/checkouts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> addCheckoutPublic(@PathVariable("patron_id") Long patronId) {
        patronService.addCheckoutPublic(patronId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/public/patrons/{patron_id}/guarantors/can_see_charges", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setGuarantorCanSeeCharges(@PathVariable("patron_id") Long patronId, @RequestBody Boolean canSeeCharges) {
        patronService.setGuarantorCanSeeCharges(patronId, canSeeCharges);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/public/patrons/{patron_id}/guarantors/can_see_checkouts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setGuarantorCanSeeCheckouts(@PathVariable("patron_id") Long patronId, @RequestBody Boolean canSeeCheckouts) {
        patronService.setGuarantorCanSeeCheckouts(patronId, canSeeCheckouts);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/public/patrons/{patron_id}/holds/{hold_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> cancelPatronHoldPublic(@PathVariable("patron_id") Long patronId, @PathVariable("hold_id") Long holdId) {
        patronService.cancelPatronHoldPublic(patronId, holdId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/patrons/{patron_id}/ill/requests", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<IllRequestDto>> getPublicPatronIllRequests(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronService.getPublicPatronIllRequests(patronId));
    }
}

