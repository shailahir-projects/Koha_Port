package com.shailahir.koha.serials.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.serials.dto.*;
import com.shailahir.koha.serials.service.SerialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

/**
 * Serials controller - implements subscriptions, frequencies, and numbering patterns.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/serials")
public class SerialsController {

    private final SerialsService service;

    // ── Subscriptions ─────────────────────────────────────────────────────────

    @GetMapping("/subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<SubscriptionDto>> listSubscriptions(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        log.debug("Entering listSubscriptions - {}, {}", q, pageable);
        return ResponseEntity.ok(service.listSubscriptions(q, pageable));
    }

    @PostMapping("/subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionDto> addSubscription(@RequestBody SubscriptionDto dto) {
        log.debug("Entering addSubscription - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addSubscription(dto));
    }

    @GetMapping("/subscriptions/{subscription_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionDto> getSubscription(@PathVariable("subscription_id") Long id) {
        log.debug("Entering getSubscription - {}", id);
        return ResponseEntity.ok(service.getSubscription(id));
    }

    @PutMapping("/subscriptions/{subscription_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionDto> updateSubscription(@PathVariable("subscription_id") Long id, @RequestBody SubscriptionDto dto) {
        log.debug("Entering updateSubscription - {}, {}", id, dto);
        return ResponseEntity.ok(service.updateSubscription(id, dto));
    }

    @DeleteMapping("/subscriptions/{subscription_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSubscription(@PathVariable("subscription_id") Long id) {
        log.debug("Entering deleteSubscription - {}", id);
        service.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subscriptions/{subscription_id}/serials", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SerialDto>> listSubscriptionSerials(@PathVariable("subscription_id") Long id) {
        log.debug("Entering listSubscriptionSerials - {}", id);
        return ResponseEntity.ok(service.listSubscriptionSerials(id));
    }

    @GetMapping("/subscriptions/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<SubscriptionDto>> searchSubscriptions(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        log.debug("Entering searchSubscriptions - {}, {}", q, pageable);
        return ResponseEntity.ok(service.searchSubscriptions(q, pageable));
    }

    @GetMapping("/subscriptions/{subscription_id}/history", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SerialDto>> getSubscriptionHistory(@PathVariable("subscription_id") Long id) {
        log.debug("Entering getSubscriptionHistory - {}", id);
        return ResponseEntity.ok(service.getSubscriptionHistory(id));
    }

    @PostMapping("/subscriptions/{subscription_id}/renew", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionDto> renewSubscription(@PathVariable("subscription_id") Long id, @RequestBody SubscriptionDto dto) {
        log.debug("Entering renewSubscription - {}, {}", id, dto);
        return ResponseEntity.ok(service.renewSubscription(id, dto));
    }

    @GetMapping("/claims", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SerialDto>> listClaims(Pageable pageable) {
        log.debug("Entering listClaims - {}", pageable);
        return ResponseEntity.ok(service.listClaims(pageable));
    }

    @GetMapping("/home", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> homeSummary() {
        log.debug("Entering homeSummary");
        return ResponseEntity.ok(service.getHomeSummary());
    }

    @GetMapping("/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<SubscriptionDto>> search(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        log.debug("Entering search - {}, {}", q, pageable);
        return ResponseEntity.ok(service.searchSubscriptions(q, pageable));
    }

    @GetMapping("/collection", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> collection(Pageable pageable) {
        log.debug("Entering collection - {}", pageable);
        return ResponseEntity.ok(service.getCollection(pageable));
    }

    @GetMapping("/routing/{subscription_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> routing(@PathVariable("subscription_id") Long subscriptionId) {
        log.debug("Entering routing - {}", subscriptionId);
        return ResponseEntity.ok(service.getRouting(subscriptionId));
    }

    @GetMapping("/routing-preview/{subscription_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> routingPreview(@PathVariable("subscription_id") Long subscriptionId) {
        log.debug("Entering routingPreview - {}", subscriptionId);
        return routing(subscriptionId);
    }

    @PostMapping("/reorder_members", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> reorderMembers(@RequestBody List<Map<String, Object>> payload) {
        log.debug("Entering reorderMembers - {}", payload);
        service.reorderRoutingMembers(payload);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subscriptions/batch_edit", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> batchEdit(@RequestBody Map<String, Object> body) {
        log.debug("Entering batchEdit - {}", body);
        Object ids = body.get("subscription_ids");
        return ResponseEntity.ok(Map.of("updated", ids != null ? ids : List.of()));
    }

    @GetMapping("/subscriptions/bib_search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> bibSearch(@RequestParam(value = "q", required = false) String q) {
        log.debug("Entering bibSearch - {}", q);
        return ResponseEntity.ok(service.searchBiblio(q));
    }

    @GetMapping("/lateissues-export", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> lateIssuesExport() {
        log.debug("Entering lateIssuesExport");
        return ResponseEntity.ok(service.getLateIssuesExport());
    }

    @GetMapping("/checkexpiration", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> checkExpiration() {
        log.debug("Entering checkExpiration");
        return ResponseEntity.ok(service.getExpiredSubscriptions());
    }

    @GetMapping("/acqui-search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> acquisitionsSearch(@RequestParam(value = "q", required = false) String q) {
        log.debug("Entering acquisitionsSearch - {}", q);
        return ResponseEntity.ok(service.searchAcquisitions(q));
    }

    @GetMapping("/acqui-search-result", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Map<String, Object>>> acquisitionsSearchResult(@RequestParam(value = "q", required = false) String q) {
        log.debug("Entering acquisitionsSearchResult - {}", q);
        return acquisitionsSearch(q);
    }

    // ── Frequencies ───────────────────────────────────────────────────────────

    @GetMapping("/serial_frequencies", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SubscriptionFrequencyDto>> listSerialFrequencies() {
        log.debug("Entering listSerialFrequencies");
        return ResponseEntity.ok(service.listFrequencies());
    }

    @PostMapping("/serial_frequencies", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionFrequencyDto> addSerialFrequency(@RequestBody SubscriptionFrequencyDto dto) {
        log.debug("Entering addSerialFrequency - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addFrequency(dto));
    }

    @GetMapping("/serial_frequencies/{frequency_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionFrequencyDto> getSerialFrequency(@PathVariable("frequency_id") Long id) {
        log.debug("Entering getSerialFrequency - {}", id);
        return ResponseEntity.ok(service.getFrequency(id));
    }

    @PutMapping("/serial_frequencies/{frequency_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SubscriptionFrequencyDto> updateSerialFrequency(@PathVariable("frequency_id") Long id, @RequestBody SubscriptionFrequencyDto dto) {
        log.debug("Entering updateSerialFrequency - {}, {}", id, dto);
        return ResponseEntity.ok(service.updateFrequency(id, dto));
    }

    @DeleteMapping("/serial_frequencies/{frequency_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSerialFrequency(@PathVariable("frequency_id") Long id) {
        log.debug("Entering deleteSerialFrequency - {}", id);
        service.deleteFrequency(id);
        return ResponseEntity.noContent().build();
    }

    // ── Numbering Patterns ────────────────────────────────────────────────────

    @GetMapping("/numbering_patterns", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<NumberingPatternDto>> listNumberingPatterns() {
        log.debug("Entering listNumberingPatterns");
        return ResponseEntity.ok(service.listNumberingPatterns());
    }

    @PostMapping("/numbering_patterns", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NumberingPatternDto> addNumberingPattern(@RequestBody NumberingPatternDto dto) {
        log.debug("Entering addNumberingPattern - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addNumberingPattern(dto));
    }

    @GetMapping("/numbering_patterns/{pattern_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NumberingPatternDto> getNumberingPattern(@PathVariable("pattern_id") Long id) {
        log.debug("Entering getNumberingPattern - {}", id);
        return ResponseEntity.ok(service.getNumberingPattern(id));
    }

    @PutMapping("/numbering_patterns/{pattern_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NumberingPatternDto> updateNumberingPattern(@PathVariable("pattern_id") Long id, @RequestBody NumberingPatternDto dto) {
        log.debug("Entering updateNumberingPattern - {}, {}", id, dto);
        return ResponseEntity.ok(service.updateNumberingPattern(id, dto));
    }

    @DeleteMapping("/numbering_patterns/{pattern_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteNumberingPattern(@PathVariable("pattern_id") Long id) {
        log.debug("Entering deleteNumberingPattern - {}", id);
        service.deleteNumberingPattern(id);
        return ResponseEntity.noContent().build();
    }
}

