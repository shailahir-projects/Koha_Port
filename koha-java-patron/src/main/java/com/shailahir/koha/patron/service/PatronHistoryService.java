package com.shailahir.koha.patron.service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PatronHistoryService {
    List<Map<String, Object>> getReadingRecord(Long patronId, Pageable pageable);
    List<Map<String, Object>> getHoldsHistory(Long patronId, Pageable pageable);
    List<Map<String, Object>> getRecallsHistory(Long patronId);
    List<Map<String, Object>> getNotices(Long patronId, Pageable pageable);
    List<Map<String, Object>> getAlertSubscriptions(Long patronId);
    void cancelAlertSubscription(Long patronId, Long subscriptionId);
    List<Map<String, Object>> getRoutingLists(Long patronId);
    List<Map<String, Object>> getPurchaseSuggestions(Long patronId, Pageable pageable);
    Map<String, Object> getStatistics(Long patronId);
}

