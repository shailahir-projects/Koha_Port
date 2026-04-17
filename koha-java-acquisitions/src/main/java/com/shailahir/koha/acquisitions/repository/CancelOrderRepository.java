package com.shailahir.koha.acquisitions.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * JDBC repository for order cancellation operations.
 * Mirrors $order->cancel({ reason => ..., delete_biblio => ... }) in Koha.
 */
@Repository
@RequiredArgsConstructor
public class CancelOrderRepository {

    private final JdbcTemplate jdbc;

    // ── Order state checks ─────────────────────────────────────────────────────

    public Optional<Map<String, Object>> findOrderById(Long ordernumber) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqorders WHERE ordernumber = ?", ordernumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Cancel order ───────────────────────────────────────────────────────────

    /**
     * Marks the order as cancelled:
     * <ul>
     *   <li>sets {@code orderstatus = 'cancelled'}</li>
     *   <li>sets {@code datecancellationprinted = NOW()}</li>
     *   <li>stores the cancellation reason in {@code cancellationreason}</li>
     * </ul>
     * Mirrors the SQL that Koha::Acquisition::Order->cancel() executes.
     */
    public void cancelOrder(Long ordernumber, String reason) {
        jdbc.update("""
                UPDATE aqorders
                   SET orderstatus             = 'cancelled',
                       datecancellationprinted = NOW(),
                       cancellationreason      = ?
                 WHERE ordernumber = ?
                """, reason, ordernumber);
    }

    // ── Item deletion (delete_biblio flow) ────────────────────────────────────

    /**
     * Returns the number of items linked to this order via aqorders_items
     * that are not linked to any other non-cancelled order.
     * Used to decide if items can be safely deleted.
     */
    public int countDeletableItems(Long ordernumber) {
        Integer c = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM aqorders_items oi
                 WHERE oi.ordernumber = ?
                   AND NOT EXISTS (
                       SELECT 1 FROM aqorders o2
                        JOIN aqorders_items oi2 ON oi2.ordernumber = o2.ordernumber
                       WHERE oi2.itemnumber = oi.itemnumber
                         AND o2.ordernumber != ?
                         AND o2.orderstatus != 'cancelled'
                   )
                """, Integer.class, ordernumber, ordernumber);
        return c != null ? c : 0;
    }

    /**
     * Deletes items linked exclusively to this order (no holds, no other orders).
     * Returns true if all items were successfully deleted.
     * Mirrors the item-deletion logic inside Koha::Acquisition::Order->cancel().
     */
    public boolean deleteOrderItems(Long ordernumber) {
        // Find items linked only to this order and having no holds
        var itemnumbers = jdbc.queryForList(
                """
                SELECT oi.itemnumber FROM aqorders_items oi
                 WHERE oi.ordernumber = ?
                   AND NOT EXISTS (
                       SELECT 1 FROM reserves r WHERE r.itemnumber = oi.itemnumber
                   )
                   AND NOT EXISTS (
                       SELECT 1 FROM aqorders_items oi2
                        JOIN aqorders o2 ON o2.ordernumber = oi2.ordernumber
                       WHERE oi2.itemnumber = oi.itemnumber
                         AND o2.ordernumber != ?
                         AND o2.orderstatus != 'cancelled'
                   )
                """, Long.class, ordernumber, ordernumber);

        for (Long itemnumber : itemnumbers) {
            jdbc.update("DELETE FROM aqorders_items WHERE itemnumber = ?", itemnumber);
            jdbc.update("DELETE FROM items WHERE itemnumber = ?", itemnumber);
        }
        return true;
    }

    /**
     * Attempts to delete the biblio associated with the order.
     * Only succeeds when:
     * <ul>
     *   <li>No other non-cancelled orders reference this biblio</li>
     *   <li>No items remain for this biblio</li>
     *   <li>No subscriptions reference this biblio</li>
     *   <li>No holds reference this biblio</li>
     * </ul>
     * Returns null on success, or an error code string on failure.
     * Mirrors the delete_biblio branch in Koha::Acquisition::Order->cancel().
     */
    public String tryDeleteBiblio(Long biblionumber) {
        if (biblionumber == null) return null;

        // Check for remaining items
        Integer itemCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM items WHERE biblionumber = ?", Integer.class, biblionumber);
        if (itemCount != null && itemCount > 0) {
            return "error_delitem";
        }

        // Check for other non-cancelled orders
        Integer otherOrders = jdbc.queryForObject(
                "SELECT COUNT(*) FROM aqorders WHERE biblionumber = ? AND orderstatus != 'cancelled'",
                Integer.class, biblionumber);
        if (otherOrders != null && otherOrders > 0) {
            return "error_delbiblio";
        }

        // Check for subscriptions
        Integer subs = jdbc.queryForObject(
                "SELECT COUNT(*) FROM subscription WHERE biblionumber = ?", Integer.class, biblionumber);
        if (subs != null && subs > 0) {
            return "error_delbiblio";
        }

        // Check for holds
        Integer holds = jdbc.queryForObject(
                "SELECT COUNT(*) FROM reserves WHERE biblionumber = ?", Integer.class, biblionumber);
        if (holds != null && holds > 0) {
            return "error_delbiblio";
        }

        // Safe to delete
        jdbc.update("DELETE FROM biblioitems WHERE biblionumber = ?", biblionumber);
        jdbc.update("DELETE FROM biblio WHERE biblionumber = ?", biblionumber);
        return null;
    }

    // ── Acquisition log ────────────────────────────────────────────────────────

    public void logCancellation(Long ordernumber) {
        jdbc.update("""
                INSERT INTO action_logs (timestamp, user, module, action, object, info)
                VALUES (NOW(), 0, 'ACQUISITIONS', 'CANCEL_ORDER', ?, '')
                """, ordernumber);
    }
}

