package com.shailahir.koha.circulation.service.impl;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class CirculationServiceImpl implements CirculationService {

    private final CirculationRepository circulationRepository;

    @Override
    public Page<CheckoutDto> listCheckouts(String query, Pageable pageable) {
        log.debug("Entering listCheckouts - {}, {}", query, pageable);
        return circulationRepository.findAllCheckouts(query, pageable);
    }

    @Override
    @Transactional
    public CheckoutDto addCheckout(CheckoutDto dto) {
        log.debug("Entering addCheckout - {}", dto);
        return circulationRepository.insertCheckout(dto);
    }

    @Override
    public CheckoutDto getCheckout(Long checkoutId) {
        log.debug("Entering getCheckout - {}", checkoutId);
        return circulationRepository.findCheckoutById(checkoutId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Checkout not found: " + checkoutId));
    }

    @Override
    @Transactional
    public CheckoutDto renewCheckout(Long checkoutId) {
        log.debug("Entering renewCheckout - {}", checkoutId);
        return circulationRepository.renewCheckout(checkoutId);
    }

    @Override
    public List<CheckoutDto> renewsCheckout(Long checkoutId) {
        log.debug("Entering renewsCheckout - {}", checkoutId);
        return getRenewals(checkoutId);
    }

    @Override
    public List<CheckoutDto> getRenewals(Long checkoutId) {
        log.debug("Entering getRenewals - {}", checkoutId);
        return circulationRepository.getRenewals(checkoutId);
    }

    @Override
    public Map<String, Object> allowsRenewal(Long checkoutId) {
        log.debug("Entering allowsRenewal - {}", checkoutId);
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
        log.debug("Entering checkoutAvailability - {}, {}", patronId, itemId);
        return circulationRepository.checkoutAvailability(patronId, itemId);
    }

    @Override
    public Map<String, Object> checkoutAvailabilityPublic(Long patronId, Long itemId) {
        log.debug("Entering checkoutAvailabilityPublic - {}, {}", patronId, itemId);
        return circulationRepository.checkoutAvailability(patronId, itemId);
    }

    @Override
    public Page<BookingDto> listBookings(String query, Pageable pageable) {
        log.debug("Entering listBookings - {}, {}", query, pageable);
        return circulationRepository.findAllBookings(query, pageable);
    }

    @Override
    @Transactional
    public BookingDto addBooking(BookingDto dto) {
        log.debug("Entering addBooking - {}", dto);
        return circulationRepository.insertBooking(dto);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        log.debug("Entering getBooking - {}", bookingId);
        return circulationRepository.findBookingById(bookingId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found: " + bookingId));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, BookingDto dto) {
        log.debug("Entering updateBooking - {}, {}", bookingId, dto);
        return circulationRepository.updateBooking(bookingId, dto);
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        log.debug("Entering deleteBooking - {}", bookingId);
        circulationRepository.deleteBooking(bookingId);
    }

    @Override
    public List<CirculationRuleDto> listCirculationRules() {
        log.debug("Entering listCirculationRules");
        return circulationRepository.findAllCirculationRules();
    }

    @Override
    @Transactional
    public void setCirculationRules(List<CirculationRuleDto> rules) {
        log.debug("Entering setCirculationRules - {}", rules);
        for (CirculationRuleDto rule : rules) {
            circulationRepository.upsertCirculationRule(rule);
        }
    }

    @Override
    public List<String> getCirculationRuleKinds() {
        log.debug("Entering getCirculationRuleKinds");
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
        log.debug("Entering claimReturned - {}", dto);
        return circulationRepository.insertClaim(dto);
    }

    @Override
    @Transactional
    public ReturnClaimDto updateClaimNotes(Long claimId, String notes) {
        log.debug("Entering updateClaimNotes - {}, {}", claimId, notes);
        return circulationRepository.updateClaimNotes(claimId, notes);
    }

    @Override
    @Transactional
    public void deleteClaim(Long claimId) {
        log.debug("Entering deleteClaim - {}", claimId);
        circulationRepository.deleteClaim(claimId);
    }

    @Override
    @Transactional
    public ReturnClaimDto resolveReturnClaim(Long claimId, String resolution) {
        log.debug("Entering resolveReturnClaim - {}, {}", claimId, resolution);
        return circulationRepository.resolveClaim(claimId, resolution);
    }

    @Override
    public List<RotaDto> listRotas() {
        log.debug("Entering listRotas");
        return circulationRepository.findAllRotas();
    }

    @Override
    @Transactional
    public RotaDto addRota(RotaDto dto) {
        log.debug("Entering addRota - {}", dto);
        return circulationRepository.insertRota(dto);
    }

    @Override
    public RotaDto getRota(Long rotaId) {
        log.debug("Entering getRota - {}", rotaId);
        return circulationRepository.findRotaById(rotaId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Rota not found: " + rotaId));
    }

    @Override
    @Transactional
    public RotaDto updateRota(Long rotaId, RotaDto dto) {
        log.debug("Entering updateRota - {}, {}", rotaId, dto);
        return circulationRepository.updateRota(rotaId, dto);
    }

    @Override
    @Transactional
    public void deleteRota(Long rotaId) {
        log.debug("Entering deleteRota - {}", rotaId);
        circulationRepository.deleteRota(rotaId);
    }

    @Override
    @Transactional
    public void moveStage(Long rotaId, Long stageId, Integer position) {
        log.debug("Entering moveStage - {}, {}, {}", rotaId, stageId, position);
        circulationRepository.moveStage(rotaId, stageId, position);
    }
}

