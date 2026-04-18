package com.shailahir.koha.holds.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.holds.dto.HoldDto;
import com.shailahir.koha.holds.dto.LibraryDto;
import com.shailahir.koha.holds.repository.HoldsRepository;
import com.shailahir.koha.holds.service.HoldsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoldsServiceImpl implements HoldsService {

    private final HoldsRepository holdsRepository;

    @Override
    public Page<HoldDto> listHolds(String query, Pageable pageable) {
        log.debug("Entering listHolds - {}, {}", query, pageable);
        return holdsRepository.findAll(query, pageable);
    }

    @Override
    @Transactional
    public HoldDto addHold(HoldDto dto) {
        log.debug("Entering addHold - {}", dto);
        return holdsRepository.insert(dto);
    }

    @Override
    @Transactional
    public void suspendHoldBulk(List<Long> holdIds, LocalDateTime suspendUntil) {
        log.debug("Entering suspendHoldBulk - {}, {}", holdIds, suspendUntil);
        holdsRepository.suspendBulk(holdIds, suspendUntil);
    }

    @Override
    @Transactional
    public void cancelHoldsBulk(List<Long> holdIds) {
        log.debug("Entering cancelHoldsBulk - {}", holdIds);
        holdsRepository.cancelBulk(holdIds);
    }

    @Override
    @Transactional
    public HoldDto editHold(Long holdId, HoldDto dto) {
        log.debug("Entering editHold - {}, {}", holdId, dto);
        return holdsRepository.update(holdId, dto);
    }

    @Override
    @Transactional
    public HoldDto overwriteHold(Long holdId, HoldDto dto) {
        log.debug("Entering overwriteHold - {}, {}", holdId, dto);
        return holdsRepository.update(holdId, dto);
    }

    @Override
    @Transactional
    public void deleteHold(Long holdId) {
        log.debug("Entering deleteHold - {}", holdId);
        holdsRepository.delete(holdId);
    }

    @Override
    @Transactional
    public void updateHoldPriority(Long holdId, Integer priority) {
        log.debug("Entering updateHoldPriority - {}, {}", holdId, priority);
        holdsRepository.updatePriority(holdId, priority);
    }

    @Override
    @Transactional
    public void suspendHold(Long holdId, LocalDateTime suspendUntil) {
        log.debug("Entering suspendHold - {}, {}", holdId, suspendUntil);
        holdsRepository.suspend(holdId, suspendUntil);
    }

    @Override
    @Transactional
    public void resumeHold(Long holdId) {
        log.debug("Entering resumeHold - {}", holdId);
        holdsRepository.resume(holdId);
    }

    @Override
    public List<LibraryDto> getHoldPickupLocations(Long holdId) {
        log.debug("Entering getHoldPickupLocations - {}", holdId);
        return holdsRepository.findPickupLocations();
    }

    @Override
    @Transactional
    public HoldDto updateHoldPickupLocation(Long holdId, String pickupLibraryId) {
        log.debug("Entering updateHoldPickupLocation - {}, {}", holdId, pickupLibraryId);
        holdsRepository.updatePickupLocation(holdId, pickupLibraryId);
        return holdsRepository.findById(holdId).orElseThrow(
            () -> new java.util.NoSuchElementException("Hold not found: " + holdId));
    }

    @Override
    @Transactional
    public void toggleLowestPriority(Long holdId) {
        log.debug("Entering toggleLowestPriority - {}", holdId);
        holdsRepository.toggleLowestPriority(holdId);
    }

    @Override
    @Transactional
    public void cancelArticleRequest(Long articleRequestId) {
        log.debug("Entering cancelArticleRequest - {}", articleRequestId);
        holdsRepository.cancelArticleRequest(articleRequestId);
    }

    @Override
    @Transactional
    public void publicCancelPatronArticleRequest(Long patronId, Long articleRequestId) {
        log.debug("Entering publicCancelPatronArticleRequest - {}, {}", patronId, articleRequestId);
        holdsRepository.cancelArticleRequest(articleRequestId);
    }
}

