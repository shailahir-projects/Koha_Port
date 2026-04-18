package com.shailahir.koha.serials.controller;

import com.shailahir.koha.serials.dto.*;
import com.shailahir.koha.serials.service.SerialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Serials controller - implements subscriptions, frequencies, and numbering patterns.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/serials")
public class SerialsController {

    private final SerialsService service;

    // ── Subscriptions ─────────────────────────────────────────────────────────

    @GetMapping("/subscriptions")
    public ResponseEntity<Page<SubscriptionDto>> listSubscriptions(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(service.listSubscriptions(q, pageable));
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionDto> addSubscription(@RequestBody SubscriptionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addSubscription(dto));
    }

    @GetMapping("/subscriptions/{subscription_id}")
    public ResponseEntity<SubscriptionDto> getSubscription(@PathVariable("subscription_id") Long id) {
        return ResponseEntity.ok(service.getSubscription(id));
    }

    @PutMapping("/subscriptions/{subscription_id}")
    public ResponseEntity<SubscriptionDto> updateSubscription(@PathVariable("subscription_id") Long id, @RequestBody SubscriptionDto dto) {
        return ResponseEntity.ok(service.updateSubscription(id, dto));
    }

    @DeleteMapping("/subscriptions/{subscription_id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable("subscription_id") Long id) {
        service.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subscriptions/{subscription_id}/serials")
    public ResponseEntity<List<SerialDto>> listSubscriptionSerials(@PathVariable("subscription_id") Long id) {
        return ResponseEntity.ok(service.listSubscriptionSerials(id));
    }

    // ── Frequencies ───────────────────────────────────────────────────────────

    @GetMapping("/serial_frequencies")
    public ResponseEntity<List<SubscriptionFrequencyDto>> listSerialFrequencies() {
        return ResponseEntity.ok(service.listFrequencies());
    }

    @PostMapping("/serial_frequencies")
    public ResponseEntity<SubscriptionFrequencyDto> addSerialFrequency(@RequestBody SubscriptionFrequencyDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addFrequency(dto));
    }

    @GetMapping("/serial_frequencies/{frequency_id}")
    public ResponseEntity<SubscriptionFrequencyDto> getSerialFrequency(@PathVariable("frequency_id") Long id) {
        return ResponseEntity.ok(service.getFrequency(id));
    }

    @PutMapping("/serial_frequencies/{frequency_id}")
    public ResponseEntity<SubscriptionFrequencyDto> updateSerialFrequency(@PathVariable("frequency_id") Long id, @RequestBody SubscriptionFrequencyDto dto) {
        return ResponseEntity.ok(service.updateFrequency(id, dto));
    }

    @DeleteMapping("/serial_frequencies/{frequency_id}")
    public ResponseEntity<Void> deleteSerialFrequency(@PathVariable("frequency_id") Long id) {
        service.deleteFrequency(id);
        return ResponseEntity.noContent().build();
    }

    // ── Numbering Patterns ────────────────────────────────────────────────────

    @GetMapping("/numbering_patterns")
    public ResponseEntity<List<NumberingPatternDto>> listNumberingPatterns() {
        return ResponseEntity.ok(service.listNumberingPatterns());
    }

    @PostMapping("/numbering_patterns")
    public ResponseEntity<NumberingPatternDto> addNumberingPattern(@RequestBody NumberingPatternDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addNumberingPattern(dto));
    }

    @GetMapping("/numbering_patterns/{pattern_id}")
    public ResponseEntity<NumberingPatternDto> getNumberingPattern(@PathVariable("pattern_id") Long id) {
        return ResponseEntity.ok(service.getNumberingPattern(id));
    }

    @PutMapping("/numbering_patterns/{pattern_id}")
    public ResponseEntity<NumberingPatternDto> updateNumberingPattern(@PathVariable("pattern_id") Long id, @RequestBody NumberingPatternDto dto) {
        return ResponseEntity.ok(service.updateNumberingPattern(id, dto));
    }

    @DeleteMapping("/numbering_patterns/{pattern_id}")
    public ResponseEntity<Void> deleteNumberingPattern(@PathVariable("pattern_id") Long id) {
        service.deleteNumberingPattern(id);
        return ResponseEntity.noContent().build();
    }
}

