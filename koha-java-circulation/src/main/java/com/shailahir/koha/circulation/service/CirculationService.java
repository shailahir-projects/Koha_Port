package com.shailahir.koha.circulation.service;

import com.shailahir.koha.circulation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CirculationService {
    // Checkouts
    Page<CheckoutDto> listCheckouts(String query, Pageable pageable);
    CheckoutDto addCheckout(CheckoutDto dto);
    CheckoutDto getCheckout(Long checkoutId);
    CheckoutDto renewCheckout(Long checkoutId);
    List<CheckoutDto> renewsCheckout(Long checkoutId);
    List<CheckoutDto> getRenewals(Long checkoutId);
    Map<String, Object> allowsRenewal(Long checkoutId);
    Map<String, Object> checkoutAvailability(Long patronId, Long itemId);
    Map<String, Object> checkoutAvailabilityPublic(Long patronId, Long itemId);
    // Bookings
    Page<BookingDto> listBookings(String query, Pageable pageable);
    BookingDto addBooking(BookingDto dto);
    BookingDto getBooking(Long bookingId);
    BookingDto updateBooking(Long bookingId, BookingDto dto);
    void deleteBooking(Long bookingId);
    // Circulation rules
    List<CirculationRuleDto> listCirculationRules();
    void setCirculationRules(List<CirculationRuleDto> rules);
    List<String> getCirculationRuleKinds();
    // Return claims
    ReturnClaimDto claimReturned(ReturnClaimDto dto);
    ReturnClaimDto updateClaimNotes(Long claimId, String notes);
    void deleteClaim(Long claimId);
    ReturnClaimDto resolveReturnClaim(Long claimId, String resolution);
    // Rotas
    List<RotaDto> listRotas();
    RotaDto addRota(RotaDto dto);
    RotaDto getRota(Long rotaId);
    RotaDto updateRota(Long rotaId, RotaDto dto);
    void deleteRota(Long rotaId);
    void moveStage(Long rotaId, Long stageId, Integer position);
}

