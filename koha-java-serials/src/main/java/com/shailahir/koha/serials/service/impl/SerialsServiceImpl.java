package com.shailahir.koha.serials.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.serials.dto.*;
import com.shailahir.koha.serials.repository.SerialsRepository;
import com.shailahir.koha.serials.service.SerialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SerialsServiceImpl implements SerialsService {

    private final SerialsRepository repo;

    @Override
    public Page<SubscriptionDto> listSubscriptions(String query, Pageable pageable) {
        log.debug("Entering listSubscriptions - {}, {}", query, pageable);
        return repo.findAllSubscriptions(query, pageable);
    }

    @Override
    public SubscriptionDto addSubscription(SubscriptionDto dto) {
        log.debug("Entering addSubscription - {}", dto);
        return repo.insertSubscription(dto);
    }

    @Override
    public SubscriptionDto getSubscription(Long id) {
        log.debug("Entering getSubscription - {}", id);
        return repo.findSubscriptionById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }

    @Override
    public SubscriptionDto updateSubscription(Long id, SubscriptionDto dto) {
        log.debug("Entering updateSubscription - {}, {}", id, dto);
        repo.findSubscriptionById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        return repo.updateSubscription(id, dto);
    }

    @Override
    public void deleteSubscription(Long id) {
        log.debug("Entering deleteSubscription - {}", id);
        repo.deleteSubscription(id);
    }

    @Override
    public List<SerialDto> listSubscriptionSerials(Long subscriptionId) {
        log.debug("Entering listSubscriptionSerials - {}", subscriptionId);
        return repo.findSerialsBySubscriptionId(subscriptionId);
    }

    @Override
    public List<SubscriptionFrequencyDto> listFrequencies() {
        log.debug("Entering listFrequencies");
        return repo.findAllFrequencies();
    }

    @Override
    public SubscriptionFrequencyDto addFrequency(SubscriptionFrequencyDto dto) {
        log.debug("Entering addFrequency - {}", dto);
        return repo.insertFrequency(dto);
    }

    @Override
    public SubscriptionFrequencyDto getFrequency(Long id) {
        log.debug("Entering getFrequency - {}", id);
        return repo.findFrequencyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Frequency not found"));
    }

    @Override
    public SubscriptionFrequencyDto updateFrequency(Long id, SubscriptionFrequencyDto dto) {
        log.debug("Entering updateFrequency - {}, {}", id, dto);
        repo.findFrequencyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Frequency not found"));
        return repo.updateFrequency(id, dto);
    }

    @Override
    public void deleteFrequency(Long id) {
        log.debug("Entering deleteFrequency - {}", id);
        repo.deleteFrequency(id);
    }

    @Override
    public List<NumberingPatternDto> listNumberingPatterns() {
        log.debug("Entering listNumberingPatterns");
        return repo.findAllNumberingPatterns();
    }

    @Override
    public NumberingPatternDto addNumberingPattern(NumberingPatternDto dto) {
        log.debug("Entering addNumberingPattern - {}", dto);
        return repo.insertNumberingPattern(dto);
    }

    @Override
    public NumberingPatternDto getNumberingPattern(Long id) {
        log.debug("Entering getNumberingPattern - {}", id);
        return repo.findNumberingPatternById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Numbering pattern not found"));
    }

    @Override
    public NumberingPatternDto updateNumberingPattern(Long id, NumberingPatternDto dto) {
        log.debug("Entering updateNumberingPattern - {}, {}", id, dto);
        repo.findNumberingPatternById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Numbering pattern not found"));
        return repo.updateNumberingPattern(id, dto);
    }

    @Override
    public void deleteNumberingPattern(Long id) {
        log.debug("Entering deleteNumberingPattern - {}", id);
        repo.deleteNumberingPattern(id);
    }

    @Override
    public Page<SubscriptionDto> searchSubscriptions(String query, Pageable pageable) {
        log.debug("Entering searchSubscriptions - {}, {}", query, pageable);
        return repo.findAllSubscriptions(query, pageable);
    }

    @Override
    public List<SerialDto> getSubscriptionHistory(Long subscriptionId) {
        log.debug("Entering getSubscriptionHistory - {}", subscriptionId);
        return repo.findSerialsBySubscriptionId(subscriptionId);
    }

    @Override
    public SubscriptionDto renewSubscription(Long subscriptionId, SubscriptionDto dto) {
        log.debug("Entering renewSubscription - {}, {}", subscriptionId, dto);
        repo.findSubscriptionById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        return repo.renewSubscription(subscriptionId, dto);
    }

    @Override
    public List<SerialDto> listClaims(Pageable pageable) {
        log.debug("Entering listClaims - {}", pageable);
        return repo.findClaimedSerials(pageable);
    }

    @Override
    public Map<String, Object> getHomeSummary() {
        log.debug("Entering getHomeSummary");
        Integer subscriptions = repo.countSubscriptions();
        Integer serials = repo.countSerials();
        return Map.of(
                "subscriptions", subscriptions != null ? subscriptions : 0,
                "serials", serials != null ? serials : 0);
    }

    @Override
    public List<Map<String, Object>> getCollection(Pageable pageable) {
        log.debug("Entering getCollection - {}", pageable);
        return repo.findSerialCollection(pageable);
    }

    @Override
    public List<Map<String, Object>> getRouting(Long subscriptionId) {
        log.debug("Entering getRouting - {}", subscriptionId);
        return repo.findRoutingListBySubscription(subscriptionId);
    }

    @Override
    public void reorderRoutingMembers(List<Map<String, Object>> payload) {
        log.debug("Entering reorderRoutingMembers - {}", payload);
        for (Map<String, Object> row : payload) {
            repo.updateRoutingRanking(row.get("routingid"), row.get("ranking"));
        }
    }

    @Override
    public List<Map<String, Object>> searchBiblio(String query) {
        log.debug("Entering searchBiblio - {}", query);
        String needle = query == null ? "" : query;
        return repo.searchBiblio(needle);
    }

    @Override
    public List<Map<String, Object>> getLateIssuesExport() {
        log.debug("Entering getLateIssuesExport");
        return repo.findLateIssues();
    }

    @Override
    public List<Map<String, Object>> getExpiredSubscriptions() {
        log.debug("Entering getExpiredSubscriptions");
        return repo.findExpiredSubscriptions();
    }

    @Override
    public List<Map<String, Object>> searchAcquisitions(String query) {
        log.debug("Entering searchAcquisitions - {}", query);
        String needle = query == null ? "" : query;
        return repo.searchAcquisitions(needle);
    }
}

