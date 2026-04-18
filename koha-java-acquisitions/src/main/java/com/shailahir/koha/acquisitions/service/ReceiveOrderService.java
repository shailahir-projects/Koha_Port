package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.ReceiveOrderRequest;
import com.shailahir.koha.acquisitions.dto.ReceiveOrderResult;

import java.util.Optional;

/**
 * Business logic for receiving orders against an invoice.
 * Ports finishreceive.pl and orderreceive.pl.
 *
 * <p>Receiving involves:
 * <ol>
 *   <li>Updating quantityreceived and unitprice on aqorders</li>
 *   <li>Setting datereceived on the order</li>
 *   <li>Marking orderstatus = 'complete' when fully received</li>
 *   <li>Updating item subfields (location, barcode, etc.)</li>
 *   <li>Updating the associated purchase suggestion</li>
 *   <li>Writing an acquisition log entry</li>
 * </ol>
 */
public interface ReceiveOrderService {

    /**
     * Returns the order detail needed to display the receive-order form.
     * Mirrors orderreceive.pl (GET/form display).
     */
    Optional<OrderDto> getOrderForReceive(Long ordernumber);

    /**
     * Processes the receive-order form submission.
     * Mirrors finishreceive.pl (POST/save).
     *
     * @param ordernumber the order being received
     * @param req         receive form data
     * @return result with updated ordernumber and status
     */
    ReceiveOrderResult receiveOrder(Long ordernumber, ReceiveOrderRequest req);
}

