package com.shailahir.koha.holds.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.holds.dto.*;
import com.shailahir.koha.holds.service.HoldsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HoldsController {
    private final HoldsService holdsService;

    @GetMapping("/holds", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<HoldDto>> listHolds(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        log.debug("Entering listHolds - {}, {}", query, pageable);
        return ResponseEntity.ok(holdsService.listHolds(query, pageable));
    }
    @PostMapping("/holds", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<HoldDto> addHold(@RequestBody HoldDto dto) {
        log.debug("Entering addHold - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(holdsService.addHold(dto));
    }
    @PostMapping("/holds/suspension_bulk", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> suspendHoldBulk(@RequestBody List<Long> holdIds, @RequestParam(value = "suspend_until", required = false) LocalDateTime suspendUntil) {
        log.debug("Entering suspendHoldBulk - {}, {}", holdIds, suspendUntil);
        holdsService.suspendHoldBulk(holdIds, suspendUntil);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/holds/cancellation_bulk", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> cancelHoldsBulk(@RequestBody List<Long> holdIds) {
        log.debug("Entering cancelHoldsBulk - {}", holdIds);
        holdsService.cancelHoldsBulk(holdIds);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/holds/{hold_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<HoldDto> editHold(@PathVariable("hold_id") Long holdId, @RequestBody HoldDto dto) {
        log.debug("Entering editHold - {}, {}", holdId, dto);
        return ResponseEntity.ok(holdsService.editHold(holdId, dto));
    }
    @PutMapping("/holds/{hold_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<HoldDto> overwriteHold(@PathVariable("hold_id") Long holdId, @RequestBody HoldDto dto) {
        log.debug("Entering overwriteHold - {}, {}", holdId, dto);
        return ResponseEntity.ok(holdsService.overwriteHold(holdId, dto));
    }
    @DeleteMapping("/holds/{hold_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteHold(@PathVariable("hold_id") Long holdId) {
        log.debug("Entering deleteHold - {}", holdId);
        holdsService.deleteHold(holdId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/holds/{hold_id}/priority", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateHoldPriority(@PathVariable("hold_id") Long holdId, @RequestBody Integer priority) {
        log.debug("Entering updateHoldPriority - {}, {}", holdId, priority);
        holdsService.updateHoldPriority(holdId, priority);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/holds/{hold_id}/suspension", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> suspendHold(@PathVariable("hold_id") Long holdId, @RequestParam(value = "suspend_until", required = false) LocalDateTime suspendUntil) {
        log.debug("Entering suspendHold - {}, {}", holdId, suspendUntil);
        holdsService.suspendHold(holdId, suspendUntil);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/holds/{hold_id}/suspension", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> resumeHold(@PathVariable("hold_id") Long holdId) {
        log.debug("Entering resumeHold - {}", holdId);
        holdsService.resumeHold(holdId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/holds/{hold_id}/pickup_locations", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<LibraryDto>> getHoldPickupLocations(@PathVariable("hold_id") Long holdId) {
        log.debug("Entering getHoldPickupLocations - {}", holdId);
        return ResponseEntity.ok(holdsService.getHoldPickupLocations(holdId));
    }
    @PutMapping("/holds/{hold_id}/pickup_location", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<HoldDto> updateHoldPickupLocation(@PathVariable("hold_id") Long holdId, @RequestBody String pickupLibraryId) {
        log.debug("Entering updateHoldPickupLocation - {}, {}", holdId, pickupLibraryId);
        return ResponseEntity.ok(holdsService.updateHoldPickupLocation(holdId, pickupLibraryId));
    }
    @PutMapping("/holds/{hold_id}/lowest_priority", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> toggleLowestPriority(@PathVariable("hold_id") Long holdId) {
        log.debug("Entering toggleLowestPriority - {}", holdId);
        holdsService.toggleLowestPriority(holdId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/article_requests/{article_request_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> cancelArticleRequest(@PathVariable("article_request_id") Long articleRequestId) {
        log.debug("Entering cancelArticleRequest - {}", articleRequestId);
        holdsService.cancelArticleRequest(articleRequestId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/public/patrons/{patron_id}/article_requests/{article_request_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> publicCancelPatronArticleRequest(@PathVariable("patron_id") Long patronId, @PathVariable("article_request_id") Long articleRequestId) {
        log.debug("Entering publicCancelPatronArticleRequest - {}, {}", patronId, articleRequestId);
        holdsService.publicCancelPatronArticleRequest(patronId, articleRequestId);
        return ResponseEntity.noContent().build();
    }
}

