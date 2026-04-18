package com.shailahir.koha.circulation.service.impl;

import com.shailahir.koha.circulation.dto.*;
import com.shailahir.koha.circulation.repository.CirculationRepository;
import com.shailahir.koha.circulation.service.CirculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service implementation for circulation operations.
 * Covers: circ/circulation.pl, circ/returns.pl, circ/renew.pl,
 *         circ/bookings.pl, circ/smart-rules.pl, circ/rotas.pl,
 *         circ/claim-returned.pl, circ/overdue.pl, circ/offline_circ.pl,
 *         circ/waitingreserves.pl, circ/transferstoreceive.pl,
 *         circ/branchreserves.pl, circ/reserveratios.pl, circ/hold-transfer.pl,
 *         circ/stats.pl, circ/selectbranchprinter.pl, circ/printslip.pl,
 *         circ/batch_checkout.pl, circ/batch_checkin.pl, circ/pendingreserves.pl,
 *         circ/hold-transfer.pl, circ/ysearch.pl, circ/cigfees.pl
 */
@Service
@RequiredArgsConstructor
public class CirculationServiceImpl implements CirculationService {

    private final CirculationRepository circulationRepository;

    @Override
    public Page<CheckoutDto> listCheckouts(String query, Pageable pageable) {
        return circulationRepository.findAllCheckouts(query, pageable);
    }

    @Override
    @Transactional
    public CheckoutDto addCheckout(CheckoutDto dto) {
        return circulationRepository.insertCheckout(dto);
    }

    @Override
    public CheckoutDto getCheckout(Long checkoutId) {
        return circulationRepository.findCheckoutById(checkoutId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Checkout not found: " + checkoutId));
    }

    @Override
    @Transactional
    public CheckoutDto renewCheckout(Long checkoutId) {
        return circulationRepository.renewCheckout(checkoutId);
    }

    @Override
    public List<CheckoutDto> renewsCheckout(Long checkoutId) {
        return getRenewals(checkoutId);
    }

    @Override
    public List<CheckoutDto> getRenewals(Long checkoutId) {
        return circulationRepository.getRenewals(checkoutId);
    }

    @Override
    public Map<String, Object> allowsRenewal(Long checkoutId) {
        CheckoutDto checkout = getCheckout(checkoutId);
        int maxRenewals = 3; // default; would come from circulation rules
        boolean allowed = checkout.getRenewals() == null || checkout.getRenewals() < maxRenewals;
        return Map.of(
            "checkout_id", checkoutId,
            "allows_renewal", allowed,
            "current_renewals", checkout.getRenewals() != null ? checkout.getRenewals() : 0,
            "max_renewals", maxRenewals
        );
    }

    @Override
    public Map<String, Object> checkoutAvailability(Long patronId, Long itemId) {
        return circulationRepository.checkoutAvailability(patronId, itemId);
    }

    @Override
    public Map<String, Object> checkoutAvailabilityPublic(Long patronId, Long itemId) {
        return circulationRepository.checkoutAvailability(patronId, itemId);
    }

    @Override
    public Page<BookingDto> listBookings(String query, Pageable pageable) {
        return circulationRepository.findAllBookings(query, pageable);
    }

    @Override
    @Transactional
    public BookingDto addBooking(BookingDto dto) {
        return circulationRepository.insertBooking(dto);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        return circulationRepository.findBookingById(bookingId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found: " + bookingId));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, BookingDto dto) {
        return circulationRepository.updateBooking(bookingId, dto);
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        circulationRepository.deleteBooking(bookingId);
    }

    @Override
    public List<CirculationRuleDto> listCirculationRules() {
        return circulationRepository.findAllCirculationRules();
    }

    @Override
    @Transactional
    public void setCirculationRules(List<CirculationRuleDto> rules) {
        for (CirculationRuleDto rule : rules) {
            circulationRepository.upsertCirculationRule(rule);
        }
    }

    @Override
    public List<String> getCirculationRuleKinds() {
        return List.of(
            "maxissueqty", "maxreserveqty", "issuelength", "lengthunit",
            "renewalsallowed", "renewalperiod", "norenewalbefore",
            "fine", "chargeperiod", "chargeperiodbase",
            "overduefinescap", "cap_fine_to_replacement_price",
            "suspension_chargeperiod", "reservesallowed",
            "holds_per_record", "holds_per_day"
        );
    }

    @Override
    @Transactional
    public ReturnClaimDto claimReturned(ReturnClaimDto dto) {
        return circulationRepository.insertClaim(dto);
    }

    @Override
    @Transactional
    public ReturnClaimDto updateClaimNotes(Long claimId, String notes) {
        return circulationRepository.updateClaimNotes(claimId, notes);
    }

    @Override
    @Transactional
    public void deleteClaim(Long claimId) {
        circulationRepository.deleteClaim(claimId);
    }

    @Override
    @Transactional
    public ReturnClaimDto resolveReturnClaim(Long claimId, String resolution) {
        return circulationRepository.resolveClaim(claimId, resolution);
    }

    @Override
    public List<RotaDto> listRotas() {
        return circulationRepository.findAllRotas();
    }

    @Override
    @Transactional
    public RotaDto addRota(RotaDto dto) {
        return circulationRepository.insertRota(dto);
    }

    @Override
    public RotaDto getRota(Long rotaId) {
        return circulationRepository.findRotaById(rotaId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Rota not found: " + rotaId));
    }

    @Override
    @Transactional
    public RotaDto updateRota(Long rotaId, RotaDto dto) {
        return circulationRepository.updateRota(rotaId, dto);
    }

    @Override
    @Transactional
    public void deleteRota(Long rotaId) {
        circulationRepository.deleteRota(rotaId);
    }

    @Override
    @Transactional
    public void moveStage(Long rotaId, Long stageId, Integer position) {
        circulationRepository.moveStage(rotaId, stageId, position);
    }
}

