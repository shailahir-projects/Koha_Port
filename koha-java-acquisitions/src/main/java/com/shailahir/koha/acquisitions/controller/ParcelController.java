package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.ParcelDetailDto;
import com.shailahir.koha.acquisitions.dto.ParcelSummaryDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for parcels.pl and parcel.pl
 *
 * <pre>
 * GET /api/v1/acquisitions/vendors/{booksellerid}/parcels             (parcels.pl – list)
 * GET /api/v1/acquisitions/parcels/{invoiceid}                        (parcel.pl  – detail)
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ParcelController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Lists all invoices (parcels) for a vendor.
     * Mirrors parcels.pl: shows shipment/billing dates and order totals.
     *
     * @param booksellerid the vendor identifier
     * @param fromDate     optional start date filter (yyyy-MM-dd)
     * @param toDate       optional end date filter (yyyy-MM-dd)
     */
    @GetMapping(
            value = "/api/v1/acquisitions/vendors/{booksellerid}/parcels",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<List<ParcelSummaryDto>> listParcels(
            @PathVariable Long booksellerid,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        log.debug("GET parcels booksellerid={} from={} to={}", booksellerid, fromDate, toDate);
        return ResponseEntity.ok(extRepo.findParcelsByVendor(booksellerid, fromDate, toDate));
    }

    /**
     * Returns the full detail of a single invoice/parcel including its orders.
     * Mirrors parcel.pl.
     *
     * @param invoiceid the invoice identifier (aqinvoices.invoiceid)
     */
    @GetMapping(
            value = "/api/v1/acquisitions/parcels/{invoiceid}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<ParcelDetailDto> getParcel(@PathVariable Long invoiceid) {
        log.debug("GET parcel invoiceid={}", invoiceid);
        return extRepo.findParcelById(invoiceid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

