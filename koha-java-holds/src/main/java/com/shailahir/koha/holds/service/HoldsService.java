package com.shailahir.koha.holds.service;

import com.shailahir.koha.holds.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface HoldsService {
    Page<HoldDto> listHolds(String query, Pageable pageable);
    HoldDto addHold(HoldDto dto);
    void suspendHoldBulk(List<Long> holdIds, LocalDateTime suspendUntil);
    void cancelHoldsBulk(List<Long> holdIds);
    HoldDto editHold(Long holdId, HoldDto dto);
    HoldDto overwriteHold(Long holdId, HoldDto dto);
    void deleteHold(Long holdId);
    void updateHoldPriority(Long holdId, Integer priority);
    void suspendHold(Long holdId, LocalDateTime suspendUntil);
    void resumeHold(Long holdId);
    List<LibraryDto> getHoldPickupLocations(Long holdId);
    HoldDto updateHoldPickupLocation(Long holdId, String pickupLibraryId);
    void toggleLowestPriority(Long holdId);
    void cancelArticleRequest(Long articleRequestId);
    void publicCancelPatronArticleRequest(Long patronId, Long articleRequestId);
}

