package com.shailahir.koha.circulation.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.circulation.dto.*;
import com.shailahir.koha.circulation.service.CirculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CirculationController {
    private final CirculationService circulationService;

    // ── /checkouts ──
    @GetMapping("/checkouts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<CheckoutDto>> listCheckouts(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        log.debug("Entering listCheckouts - {}, {}", query, pageable);
        return ResponseEntity.ok(circulationService.listCheckouts(query, pageable));
    }
    @PostMapping("/checkouts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CheckoutDto> addCheckout(@RequestBody CheckoutDto dto) {
        log.debug("Entering addCheckout - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.addCheckout(dto));
    }
    @GetMapping("/checkouts/{checkout_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CheckoutDto> getCheckout(@PathVariable("checkout_id") Long checkoutId) {
        log.debug("Entering getCheckout - {}", checkoutId);
        return ResponseEntity.ok(circulationService.getCheckout(checkoutId));
    }
    @PostMapping("/checkouts/{checkout_id}/renewal", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CheckoutDto> renewCheckout(@PathVariable("checkout_id") Long checkoutId) {
        log.debug("Entering renewCheckout - {}", checkoutId);
        return ResponseEntity.ok(circulationService.renewCheckout(checkoutId));
    }
    @PostMapping("/checkouts/{checkout_id}/renewals", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CheckoutDto>> renewsCheckout(@PathVariable("checkout_id") Long checkoutId) {
        log.debug("Entering renewsCheckout - {}", checkoutId);
        return ResponseEntity.ok(circulationService.renewsCheckout(checkoutId));
    }
    @GetMapping("/checkouts/{checkout_id}/renewals", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CheckoutDto>> getRenewals(@PathVariable("checkout_id") Long checkoutId) {
        log.debug("Entering getRenewals - {}", checkoutId);
        return ResponseEntity.ok(circulationService.getRenewals(checkoutId));
    }
    @GetMapping("/checkouts/{checkout_id}/allows_renewal", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> allowsRenewal(@PathVariable("checkout_id") Long checkoutId) {
        log.debug("Entering allowsRenewal - {}", checkoutId);
        return ResponseEntity.ok(circulationService.allowsRenewal(checkoutId));
    }
    @GetMapping("/checkouts/availability", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> checkoutAvailability(@RequestParam("patron_id") Long patronId, @RequestParam("item_id") Long itemId) {
        log.debug("Entering checkoutAvailability - {}, {}", patronId, itemId);
        return ResponseEntity.ok(circulationService.checkoutAvailability(patronId, itemId));
    }
    @GetMapping("/public/checkouts/availability", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> checkoutAvailabilityPublic(@RequestParam("patron_id") Long patronId, @RequestParam("item_id") Long itemId) {
        log.debug("Entering checkoutAvailabilityPublic - {}, {}", patronId, itemId);
        return ResponseEntity.ok(circulationService.checkoutAvailabilityPublic(patronId, itemId));
    }

    // ── /bookings ──
    @GetMapping("/bookings", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<BookingDto>> listBookings(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        log.debug("Entering listBookings - {}, {}", query, pageable);
        return ResponseEntity.ok(circulationService.listBookings(query, pageable));
    }
    @PostMapping("/bookings", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookingDto> addBooking(@RequestBody BookingDto dto) {
        log.debug("Entering addBooking - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.addBooking(dto));
    }
    @GetMapping("/bookings/{booking_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookingDto> getBooking(@PathVariable("booking_id") Long bookingId) {
        log.debug("Entering getBooking - {}", bookingId);
        return ResponseEntity.ok(circulationService.getBooking(bookingId));
    }
    @PutMapping("/bookings/{booking_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookingDto> updateBooking(@PathVariable("booking_id") Long bookingId, @RequestBody BookingDto dto) {
        log.debug("Entering updateBooking - {}, {}", bookingId, dto);
        return ResponseEntity.ok(circulationService.updateBooking(bookingId, dto));
    }
    @PatchMapping("/bookings/{booking_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookingDto> editBooking(@PathVariable("booking_id") Long bookingId, @RequestBody BookingDto dto) {
        log.debug("Entering editBooking - {}, {}", bookingId, dto);
        return ResponseEntity.ok(circulationService.updateBooking(bookingId, dto));
    }
    @DeleteMapping("/bookings/{booking_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteBooking(@PathVariable("booking_id") Long bookingId) {
        log.debug("Entering deleteBooking - {}", bookingId);
        circulationService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    // ── /circulation_rules ──
    @GetMapping("/circulation_rules", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CirculationRuleDto>> listCirculationRules() {
        log.debug("Entering listCirculationRules");
        return ResponseEntity.ok(circulationService.listCirculationRules());
    }
    @PutMapping("/circulation_rules", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setCirculationRules(@RequestBody List<CirculationRuleDto> rules) {
        log.debug("Entering setCirculationRules - {}", rules);
        circulationService.setCirculationRules(rules);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/circulation_rules/kinds", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<String>> getCirculationRuleKinds() {
        log.debug("Entering getCirculationRuleKinds");
        return ResponseEntity.ok(circulationService.getCirculationRuleKinds());
    }

    // ── /return_claims ──
    @PostMapping("/return_claims", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ReturnClaimDto> claimReturned(@RequestBody ReturnClaimDto dto) {
        log.debug("Entering claimReturned - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.claimReturned(dto));
    }
    @PutMapping("/return_claims/{claim_id}/notes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ReturnClaimDto> updateClaimNotes(@PathVariable("claim_id") Long claimId, @RequestBody String notes) {
        log.debug("Entering updateClaimNotes - {}, {}", claimId, notes);
        return ResponseEntity.ok(circulationService.updateClaimNotes(claimId, notes));
    }
    @DeleteMapping("/return_claims/{claim_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteClaim(@PathVariable("claim_id") Long claimId) {
        log.debug("Entering deleteClaim - {}", claimId);
        circulationService.deleteClaim(claimId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/return_claims/{claim_id}/resolve", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ReturnClaimDto> resolveReturnClaim(@PathVariable("claim_id") Long claimId, @RequestBody String resolution) {
        log.debug("Entering resolveReturnClaim - {}, {}", claimId, resolution);
        return ResponseEntity.ok(circulationService.resolveReturnClaim(claimId, resolution));
    }

    // ── /rotas ──
    @GetMapping("/rotas", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<RotaDto>> listRotas() {
        log.debug("Entering listRotas");
        return ResponseEntity.ok(circulationService.listRotas());
    }
    @PostMapping("/rotas", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RotaDto> addRota(@RequestBody RotaDto dto) {
        log.debug("Entering addRota - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.addRota(dto));
    }
    @GetMapping("/rotas/{rota_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RotaDto> getRota(@PathVariable("rota_id") Long rotaId) {
        log.debug("Entering getRota - {}", rotaId);
        return ResponseEntity.ok(circulationService.getRota(rotaId));
    }
    @PutMapping("/rotas/{rota_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RotaDto> updateRota(@PathVariable("rota_id") Long rotaId, @RequestBody RotaDto dto) {
        log.debug("Entering updateRota - {}, {}", rotaId, dto);
        return ResponseEntity.ok(circulationService.updateRota(rotaId, dto));
    }
    @DeleteMapping("/rotas/{rota_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteRota(@PathVariable("rota_id") Long rotaId) {
        log.debug("Entering deleteRota - {}", rotaId);
        circulationService.deleteRota(rotaId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/rotas/{rota_id}/stages/{stage_id}/position", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> moveStage(@PathVariable("rota_id") Long rotaId, @PathVariable("stage_id") Long stageId, @RequestParam("position") Integer position) {
        log.debug("Entering moveStage - {}, {}, {}", rotaId, stageId, position);
        circulationService.moveStage(rotaId, stageId, position);
        return ResponseEntity.ok().build();
    }
}

