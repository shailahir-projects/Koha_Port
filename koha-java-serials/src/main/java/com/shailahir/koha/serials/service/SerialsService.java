package com.shailahir.koha.serials.service;

import com.shailahir.koha.serials.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SerialsService {
    Page<SubscriptionDto> listSubscriptions(String query, Pageable pageable);
    SubscriptionDto addSubscription(SubscriptionDto dto);
    SubscriptionDto getSubscription(Long id);
    SubscriptionDto updateSubscription(Long id, SubscriptionDto dto);
    void deleteSubscription(Long id);
    List<SerialDto> listSubscriptionSerials(Long subscriptionId);

    List<SubscriptionFrequencyDto> listFrequencies();
    SubscriptionFrequencyDto addFrequency(SubscriptionFrequencyDto dto);
    SubscriptionFrequencyDto getFrequency(Long id);
    SubscriptionFrequencyDto updateFrequency(Long id, SubscriptionFrequencyDto dto);
    void deleteFrequency(Long id);

    List<NumberingPatternDto> listNumberingPatterns();
    NumberingPatternDto addNumberingPattern(NumberingPatternDto dto);
    NumberingPatternDto getNumberingPattern(Long id);
    NumberingPatternDto updateNumberingPattern(Long id, NumberingPatternDto dto);
    void deleteNumberingPattern(Long id);

    Page<SubscriptionDto> searchSubscriptions(String query, Pageable pageable);
    List<SerialDto> getSubscriptionHistory(Long subscriptionId);
    SubscriptionDto renewSubscription(Long subscriptionId, SubscriptionDto dto);
    List<SerialDto> listClaims(Pageable pageable);

    Map<String, Object> getHomeSummary();
    List<Map<String, Object>> getCollection(Pageable pageable);
    List<Map<String, Object>> getRouting(Long subscriptionId);
    void reorderRoutingMembers(List<Map<String, Object>> payload);
    List<Map<String, Object>> searchBiblio(String query);
    List<Map<String, Object>> getLateIssuesExport();
    List<Map<String, Object>> getExpiredSubscriptions();
    List<Map<String, Object>> searchAcquisitions(String query);
}

