package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketInfoDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for vendor/bookseller read operations.
 * Ported from booksellers.pl, vendors.pl.
 */
public interface IVendorRepository {

    Optional<Map<String, Object>> findVendorById(Long booksellerid);
    int countBaskets(Long booksellerid);
    int countSubscriptions(Long booksellerid);
    List<BasketInfoDto> findBasketsInfoByVendor(Long booksellerid, boolean allBaskets);
    boolean hasActiveBudgets();
}

