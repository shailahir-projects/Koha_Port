package com.shailahir.koha.circulation.controller;

import com.shailahir.koha.circulation.dto.*;
import com.shailahir.koha.circulation.service.CirculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CirculationController {
    private final CirculationService circulationService;

    // ── /checkouts ──
    @GetMapping("/checkouts")
    public ResponseEntity<Page<CheckoutDto>> listCheckouts(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(circulationService.listCheckouts(query, pageable));
    }
    @PostMapping("/checkouts")
    public ResponseEntity<CheckoutDto> addCheckout(@RequestBody CheckoutDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.addCheckout(dto));
    }
    @GetMapping("/checkouts/{checkout_id}")
    public ResponseEntity<CheckoutDto> getCheckout(@PathVariable("checkout_id") Long checkoutId) {
        return ResponseEntity.ok(circulationService.getCheckout(checkoutId));
    }
    @PostMapping("/checkouts/{checkout_id}/renewal")
    public ResponseEntity<CheckoutDto> renewCheckout(@PathVariable("checkout_id") Long checkoutId) {
        return ResponseEntity.ok(circulationService.renewCheckout(checkoutId));
    }
    @PostMapping("/checkouts/{checkout_id}/renewals")
    public ResponseEntity<List<CheckoutDto>> renewsCheckout(@PathVariable("checkout_id") Long checkoutId) {
        return ResponseEntity.ok(circulationService.renewsCheckout(checkoutId));
    }
    @GetMapping("/checkouts/{checkout_id}/renewals")
    public ResponseEntity<List<CheckoutDto>> getRenewals(@PathVariable("checkout_id") Long checkoutId) {
        return ResponseEntity.ok(circulationService.getRenewals(checkoutId));
    }
    @GetMapping("/checkouts/{checkout_id}/allows_renewal")
    public ResponseEntity<Map<String, Object>> allowsRenewal(@PathVariable("checkout_id") Long checkoutId) {
        return ResponseEntity.ok(circulationService.allowsRenewal(checkoutId));
    }
    @GetMapping("/checkouts/availability")
    public ResponseEntity<Map<String, Object>> checkoutAvailability(@RequestParam("patron_id") Long patronId, @RequestParam("item_id") Long itemId) {
        return ResponseEntity.ok(circulationService.checkoutAvailability(patronId, itemId));
    }
    @GetMapping("/public/checkouts/availability")
    public ResponseEntity<Map<String, Object>> checkoutAvailabilityPublic(@RequestParam("patron_id") Long patronId, @RequestParam("item_id") Long itemId) {
        return ResponseEntity.ok(circulationService.checkoutAvailabilityPublic(patronId, itemId));
    }

    // ── /bookings ──
    @GetMapping("/bookings")
    public ResponseEntity<Page<BookingDto>> listBookings(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(circulationService.listBookings(query, pageable));
    }
    @PostMapping("/bookings")
    public ResponseEntity<BookingDto> addBooking(@RequestBody BookingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.addBooking(dto));
    }
    @GetMapping("/bookings/{booking_id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable("booking_id") Long bookingId) {
        return ResponseEntity.ok(circulationService.getBooking(bookingId));
    }
    @PutMapping("/bookings/{booking_id}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable("booking_id") Long bookingId, @RequestBody BookingDto dto) {
        return ResponseEntity.ok(circulationService.updateBooking(bookingId, dto));
    }
    @PatchMapping("/bookings/{booking_id}")
    public ResponseEntity<BookingDto> editBooking(@PathVariable("booking_id") Long bookingId, @RequestBody BookingDto dto) {
        return ResponseEntity.ok(circulationService.updateBooking(bookingId, dto));
    }
    @DeleteMapping("/bookings/{booking_id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("booking_id") Long bookingId) {
        circulationService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    // ── /circulation_rules ──
    @GetMapping("/circulation_rules")
    public ResponseEntity<List<CirculationRuleDto>> listCirculationRules() {
        return ResponseEntity.ok(circulationService.listCirculationRules());
    }
    @PutMapping("/circulation_rules")
    public ResponseEntity<Void> setCirculationRules(@RequestBody List<CirculationRuleDto> rules) {
        circulationService.setCirculationRules(rules);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/circulation_rules/kinds")
    public ResponseEntity<List<String>> getCirculationRuleKinds() {
        return ResponseEntity.ok(circulationService.getCirculationRuleKinds());
    }

    // ── /return_claims ──
    @PostMapping("/return_claims")
    public ResponseEntity<ReturnClaimDto> claimReturned(@RequestBody ReturnClaimDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.claimReturned(dto));
    }
    @PutMapping("/return_claims/{claim_id}/notes")
    public ResponseEntity<ReturnClaimDto> updateClaimNotes(@PathVariable("claim_id") Long claimId, @RequestBody String notes) {
        return ResponseEntity.ok(circulationService.updateClaimNotes(claimId, notes));
    }
    @DeleteMapping("/return_claims/{claim_id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable("claim_id") Long claimId) {
        circulationService.deleteClaim(claimId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/return_claims/{claim_id}/resolve")
    public ResponseEntity<ReturnClaimDto> resolveReturnClaim(@PathVariable("claim_id") Long claimId, @RequestBody String resolution) {
        return ResponseEntity.ok(circulationService.resolveReturnClaim(claimId, resolution));
    }

    // ── /rotas ──
    @GetMapping("/rotas")
    public ResponseEntity<List<RotaDto>> listRotas() {
        return ResponseEntity.ok(circulationService.listRotas());
    }
    @PostMapping("/rotas")
    public ResponseEntity<RotaDto> addRota(@RequestBody RotaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(circulationService.addRota(dto));
    }
    @GetMapping("/rotas/{rota_id}")
    public ResponseEntity<RotaDto> getRota(@PathVariable("rota_id") Long rotaId) {
        return ResponseEntity.ok(circulationService.getRota(rotaId));
    }
    @PutMapping("/rotas/{rota_id}")
    public ResponseEntity<RotaDto> updateRota(@PathVariable("rota_id") Long rotaId, @RequestBody RotaDto dto) {
        return ResponseEntity.ok(circulationService.updateRota(rotaId, dto));
    }
    @DeleteMapping("/rotas/{rota_id}")
    public ResponseEntity<Void> deleteRota(@PathVariable("rota_id") Long rotaId) {
        circulationService.deleteRota(rotaId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/rotas/{rota_id}/stages/{stage_id}/position")
    public ResponseEntity<Void> moveStage(@PathVariable("rota_id") Long rotaId, @PathVariable("stage_id") Long stageId, @RequestParam("position") Integer position) {
        circulationService.moveStage(rotaId, stageId, position);
        return ResponseEntity.ok().build();
    }
}

