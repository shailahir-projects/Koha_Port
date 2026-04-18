package com.shailahir.koha.serials.controller;

import com.shailahir.koha.serials.dto.*;
import com.shailahir.koha.serials.service.SerialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Serials controller - implements subscriptions, frequencies, and numbering patterns.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/serials")
public class SerialsController {

    private final SerialsService service;
    private final JdbcTemplate jdbc;

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

    @GetMapping("/subscriptions/search")
    public ResponseEntity<Page<SubscriptionDto>> searchSubscriptions(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(service.searchSubscriptions(q, pageable));
    }

    @GetMapping("/subscriptions/{subscription_id}/history")
    public ResponseEntity<List<SerialDto>> getSubscriptionHistory(@PathVariable("subscription_id") Long id) {
        return ResponseEntity.ok(service.getSubscriptionHistory(id));
    }

    @PostMapping("/subscriptions/{subscription_id}/renew")
    public ResponseEntity<SubscriptionDto> renewSubscription(@PathVariable("subscription_id") Long id, @RequestBody SubscriptionDto dto) {
        return ResponseEntity.ok(service.renewSubscription(id, dto));
    }

    @GetMapping("/claims")
    public ResponseEntity<List<SerialDto>> listClaims(Pageable pageable) {
        return ResponseEntity.ok(service.listClaims(pageable));
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> homeSummary() {
        Integer subscriptions = jdbc.queryForObject("SELECT COUNT(*) FROM subscription", Integer.class);
        Integer serials = jdbc.queryForObject("SELECT COUNT(*) FROM serial", Integer.class);
        return ResponseEntity.ok(Map.of(
                "subscriptions", subscriptions != null ? subscriptions : 0,
                "serials", serials != null ? serials : 0));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SubscriptionDto>> search(@RequestParam(value = "q", required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(service.searchSubscriptions(q, pageable));
    }

    @GetMapping("/collection")
    public ResponseEntity<List<Map<String, Object>>> collection(Pageable pageable) {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT serialid, subscriptionid, status, serialseq, publisheddate FROM serial ORDER BY serialid DESC LIMIT ? OFFSET ?",
                pageable.getPageSize(), pageable.getOffset()));
    }

    @GetMapping("/routing/{subscription_id}")
    public ResponseEntity<List<Map<String, Object>>> routing(@PathVariable("subscription_id") Long subscriptionId) {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT * FROM subscriptionroutinglist WHERE subscriptionid = ? ORDER BY ranking",
                subscriptionId));
    }

    @GetMapping("/routing-preview/{subscription_id}")
    public ResponseEntity<List<Map<String, Object>>> routingPreview(@PathVariable("subscription_id") Long subscriptionId) {
        return routing(subscriptionId);
    }

    @PostMapping("/reorder_members")
    public ResponseEntity<Void> reorderMembers(@RequestBody List<Map<String, Object>> payload) {
        for (Map<String, Object> row : payload) {
            jdbc.update("UPDATE subscriptionroutinglist SET ranking = ? WHERE routingid = ?",
                    row.get("ranking"), row.get("routingid"));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subscriptions/batch_edit")
    public ResponseEntity<Map<String, Object>> batchEdit(@RequestBody Map<String, Object> body) {
        Object ids = body.get("subscription_ids");
        return ResponseEntity.ok(Map.of("updated", ids != null ? ids : List.of()));
    }

    @GetMapping("/subscriptions/bib_search")
    public ResponseEntity<List<Map<String, Object>>> bibSearch(@RequestParam(value = "q", required = false) String q) {
        String needle = q == null ? "" : q;
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT biblionumber, title FROM biblio WHERE title ILIKE ? ORDER BY biblionumber DESC LIMIT 50",
                "%" + needle + "%"));
    }

    @GetMapping("/lateissues-export")
    public ResponseEntity<List<Map<String, Object>>> lateIssuesExport() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT serialid, subscriptionid, serialseq, publisheddate FROM serial WHERE status ILIKE 'LATE%' ORDER BY serialid DESC"));
    }

    @GetMapping("/checkexpiration")
    public ResponseEntity<List<Map<String, Object>>> checkExpiration() {
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT subscriptionid, enddate, notes FROM subscription WHERE enddate < CURRENT_DATE ORDER BY enddate DESC"));
    }

    @GetMapping("/acqui-search")
    public ResponseEntity<List<Map<String, Object>>> acquisitionsSearch(@RequestParam(value = "q", required = false) String q) {
        String needle = q == null ? "" : q;
        return ResponseEntity.ok(jdbc.queryForList(
                "SELECT aqbooksellerid, name FROM aqbooksellers WHERE name ILIKE ? ORDER BY aqbooksellerid DESC LIMIT 50",
                "%" + needle + "%"));
    }

    @GetMapping("/acqui-search-result")
    public ResponseEntity<List<Map<String, Object>>> acquisitionsSearchResult(@RequestParam(value = "q", required = false) String q) {
        return acquisitionsSearch(q);
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

