package com.shailahir.koha.patron.service.impl;

import com.shailahir.koha.patron.service.PatronHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for patron reading/transaction history and notices.
 */
@Service
@RequiredArgsConstructor
public class PatronHistoryServiceImpl implements PatronHistoryService {

    private final JdbcTemplate jdbc;

    @Override
    public List<Map<String, Object>> getReadingRecord(Long patronId, Pageable pageable) {
        return jdbc.queryForList("""
            SELECT oi.*, bi.title, bi.author, i.barcode
            FROM old_issues oi
            JOIN items i ON oi.itemnumber = i.itemnumber
            JOIN biblio bi ON i.biblionumber = bi.biblionumber
            WHERE oi.borrowernumber = ?
            ORDER BY oi.returndate DESC
            LIMIT ? OFFSET ?
            """, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    @Override
    public List<Map<String, Object>> getHoldsHistory(Long patronId, Pageable pageable) {
        return jdbc.queryForList("""
            SELECT oh.*, bi.title, bi.author
            FROM old_reserves oh
            JOIN biblio bi ON oh.biblionumber = bi.biblionumber
            WHERE oh.borrowernumber = ?
            ORDER BY oh.reservedate DESC
            LIMIT ? OFFSET ?
            """, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    @Override
    public List<Map<String, Object>> getRecallsHistory(Long patronId) {
        return jdbc.queryForList("""
            SELECT r.*, bi.title, bi.author
            FROM recalls r
            JOIN biblio bi ON r.biblio_id = bi.biblionumber
            WHERE r.patron_id = ? AND r.status IN ('fulfilled', 'cancelled', 'expired')
            ORDER BY r.created_date DESC
            """, patronId);
    }

    @Override
    public List<Map<String, Object>> getNotices(Long patronId, Pageable pageable) {
        return jdbc.queryForList("""
            SELECT mq.*, ml.name as letter_name
            FROM message_queue mq
            LEFT JOIN letter ml ON mq.letter_code = ml.code
            WHERE mq.borrowernumber = ?
            ORDER BY mq.time_queued DESC
            LIMIT ? OFFSET ?
            """, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    @Override
    public List<Map<String, Object>> getAlertSubscriptions(Long patronId) {
        return jdbc.queryForList("""
            SELECT al.*, s.title as subscription_title, s.biblionumber
            FROM alert al
            LEFT JOIN subscription s ON al.externalid = s.subscriptionid
            WHERE al.borrowernumber = ?
            ORDER BY al.alertid
            """, patronId);
    }

    @Override
    public void cancelAlertSubscription(Long patronId, Long subscriptionId) {
        jdbc.update("DELETE FROM alert WHERE borrowernumber = ? AND alertid = ?", patronId, subscriptionId);
    }

    @Override
    public List<Map<String, Object>> getRoutingLists(Long patronId) {
        return jdbc.queryForList("""
            SELECT rl.*, s.title as subscription_title, s.biblionumber
            FROM subscriptionroutinglist rl
            JOIN subscription s ON rl.subscriptionid = s.subscriptionid
            WHERE rl.borrowernumber = ?
            ORDER BY rl.ranking
            """, patronId);
    }

    @Override
    public List<Map<String, Object>> getPurchaseSuggestions(Long patronId, Pageable pageable) {
        return jdbc.queryForList("""
            SELECT * FROM suggestions WHERE suggestedby = ?
            ORDER BY suggesteddate DESC
            LIMIT ? OFFSET ?
            """, patronId, pageable.getPageSize(), pageable.getOffset());
    }

    @Override
    public Map<String, Object> getStatistics(Long patronId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("patron_id", patronId);
        stats.put("total_checkouts", jdbc.queryForObject(
            "SELECT COUNT(*) FROM old_issues WHERE borrowernumber = ?", Integer.class, patronId));
        stats.put("current_checkouts", jdbc.queryForObject(
            "SELECT COUNT(*) FROM issues WHERE borrowernumber = ?", Integer.class, patronId));
        stats.put("total_holds", jdbc.queryForObject(
            "SELECT COUNT(*) FROM old_reserves WHERE borrowernumber = ?", Integer.class, patronId));
        stats.put("active_holds", jdbc.queryForObject(
            "SELECT COUNT(*) FROM reserves WHERE borrowernumber = ?", Integer.class, patronId));
        stats.put("total_fines", jdbc.queryForObject(
            "SELECT COALESCE(SUM(amountoutstanding), 0) FROM accountlines WHERE borrowernumber = ? AND amountoutstanding > 0",
            java.math.BigDecimal.class, patronId));
        return stats;
    }
}

