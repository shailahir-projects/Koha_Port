package com.shailahir.koha.patron.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.service.PatronHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for patron reading/transaction history and notices.
 * Mirrors: members/readingrec.pl, members/holdshistory.pl,
 *          members/recallshistory.pl, members/notices.pl,
 *          members/alert-subscriptions.pl, members/print_overdues.pl,
 *          members/printnotice.pl, members/printslip.pl,
 *          members/summary-print.pl, members/routing-lists.pl,
 *          members/statistics.pl, members/purchase-suggestions.pl
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class PatronHistoryController {

    private final PatronHistoryService patronHistoryService;

    /** members/readingrec.pl - reading/checkout history */
    @GetMapping("/patrons/{patron_id}/checkouts/history")
    public ResponseEntity<List<Map<String, Object>>> getReadingRecord(
            @PathVariable("patron_id") Long patronId,
            Pageable pageable) {
        log.debug("Entering getReadingRecord - {}, {}", patronId, pageable);
        return ResponseEntity.ok(patronHistoryService.getReadingRecord(patronId, pageable));
    }

    /** members/holdshistory.pl - holds history */
    @GetMapping("/patrons/{patron_id}/holds/history")
    public ResponseEntity<List<Map<String, Object>>> getHoldsHistory(
            @PathVariable("patron_id") Long patronId,
            Pageable pageable) {
        log.debug("Entering getHoldsHistory - {}, {}", patronId, pageable);
        return ResponseEntity.ok(patronHistoryService.getHoldsHistory(patronId, pageable));
    }

    /** members/recallshistory.pl - recalls history */
    @GetMapping("/patrons/{patron_id}/recalls/history")
    public ResponseEntity<List<Map<String, Object>>> getRecallsHistory(
            @PathVariable("patron_id") Long patronId) {
        log.debug("Entering getRecallsHistory - {}", patronId);
        return ResponseEntity.ok(patronHistoryService.getRecallsHistory(patronId));
    }

    /** members/notices.pl - patron notices/messages */
    @GetMapping("/patrons/{patron_id}/notices")
    public ResponseEntity<List<Map<String, Object>>> getNotices(
            @PathVariable("patron_id") Long patronId,
            Pageable pageable) {
        log.debug("Entering getNotices - {}, {}", patronId, pageable);
        return ResponseEntity.ok(patronHistoryService.getNotices(patronId, pageable));
    }

    /** members/alert-subscriptions.pl - serial alert subscriptions */
    @GetMapping("/patrons/{patron_id}/alert_subscriptions")
    public ResponseEntity<List<Map<String, Object>>> getAlertSubscriptions(
            @PathVariable("patron_id") Long patronId) {
        log.debug("Entering getAlertSubscriptions - {}", patronId);
        return ResponseEntity.ok(patronHistoryService.getAlertSubscriptions(patronId));
    }

    @DeleteMapping("/patrons/{patron_id}/alert_subscriptions/{subscription_id}")
    public ResponseEntity<Void> cancelAlertSubscription(
            @PathVariable("patron_id") Long patronId,
            @PathVariable("subscription_id") Long subscriptionId) {
        log.debug("Entering cancelAlertSubscription - {}, {}", patronId, subscriptionId);
        patronHistoryService.cancelAlertSubscription(patronId, subscriptionId);
        return ResponseEntity.noContent().build();
    }

    /** members/routing-lists.pl - routing list memberships */
    @GetMapping("/patrons/{patron_id}/routing_lists")
    public ResponseEntity<List<Map<String, Object>>> getRoutingLists(
            @PathVariable("patron_id") Long patronId) {
        log.debug("Entering getRoutingLists - {}", patronId);
        return ResponseEntity.ok(patronHistoryService.getRoutingLists(patronId));
    }

    /** members/purchase-suggestions.pl - patron's purchase suggestions */
    @GetMapping("/patrons/{patron_id}/purchase_suggestions")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseSuggestions(
            @PathVariable("patron_id") Long patronId,
            Pageable pageable) {
        log.debug("Entering getPurchaseSuggestions - {}, {}", patronId, pageable);
        return ResponseEntity.ok(patronHistoryService.getPurchaseSuggestions(patronId, pageable));
    }

    /** members/statistics.pl - patron statistics summary */
    @GetMapping("/patrons/{patron_id}/statistics")
    public ResponseEntity<Map<String, Object>> getPatronStatistics(
            @PathVariable("patron_id") Long patronId) {
        log.debug("Entering getPatronStatistics - {}", patronId);
        return ResponseEntity.ok(patronHistoryService.getStatistics(patronId));
    }
}

