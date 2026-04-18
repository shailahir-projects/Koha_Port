package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.CancelOrderRequest;
import com.shailahir.koha.acquisitions.dto.CancelOrderResult;

/**
 * Business logic for order cancellation.
 * Ports cancelorder.pl.
 *
 * <p>Cancellation involves:
 * <ol>
 *   <li>Setting orderstatus = 'cancelled'</li>
 *   <li>Optionally deleting attached items</li>
 *   <li>Optionally deleting the biblio if no other orders remain</li>
 *   <li>Writing an acquisition log entry</li>
 * </ol>
 */
public interface CancelOrderService {

    /**
     * Cancels an order and performs all associated cleanup.
     *
     * @param ordernumber the order to cancel
     * @param req         cancellation options (reason, delete items, delete biblio)
     * @return result describing what was cleaned up
     */
    CancelOrderResult cancelOrder(Long ordernumber, CancelOrderRequest req);
}

