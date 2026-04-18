package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.BasketInfoDto;
import com.shailahir.koha.acquisitions.dto.VendorBasketsDto;
import com.shailahir.koha.acquisitions.dto.VendorSummaryDto;

import java.util.List;

/**
 * Business logic for vendor/bookseller management.
 * Ports booksellers.pl and vendors.pl.
 */
public interface VendorService {

    /** Lists/searches vendors — mirrors vendors.pl. */
    List<VendorSummaryDto> listVendors(String name, Boolean active);

    /** Returns full vendor detail with basket list — mirrors booksellers.pl. */
    VendorBasketsDto getVendorWithBaskets(Long booksellerid, boolean allBaskets);

    /** Returns basket info list for a vendor. */
    List<BasketInfoDto> getBasketInfos(Long booksellerid, boolean allBaskets);
}

