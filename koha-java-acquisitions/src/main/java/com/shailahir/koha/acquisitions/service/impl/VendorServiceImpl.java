package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.BasketInfoDto;
import com.shailahir.koha.acquisitions.dto.VendorBasketsDto;
import com.shailahir.koha.acquisitions.dto.VendorSummaryDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import com.shailahir.koha.acquisitions.repository.VendorRepository;
import com.shailahir.koha.acquisitions.service.VendorService;
import com.shailahir.koha.acquisitions.transformer.VendorTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Implementation of {@link VendorService}.
 * Ports booksellers.pl and vendors.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final AcquisitionsExtRepository extRepo;
    private final VendorRepository vendorRepo;
    private final VendorTransformer transformer;

    @Override
    public List<VendorSummaryDto> listVendors(String name, Boolean active) {
        log.debug("listVendors name={} active={}", name, active);
        return extRepo.findVendors(name, active);
    }

    @Override
    public VendorBasketsDto getVendorWithBaskets(Long booksellerid, boolean allBaskets) {
        log.debug("getVendorWithBaskets booksellerid={} allBaskets={}", booksellerid, allBaskets);
        Map<String, Object> vendorRow = vendorRepo.findVendorById(booksellerid)
                .orElseThrow(() -> new NoSuchElementException("Vendor not found: " + booksellerid));

        List<BasketInfoDto> baskets = vendorRepo.findBasketsInfoByVendor(booksellerid, allBaskets);
        int subscriptionCount = vendorRepo.countSubscriptions(booksellerid);
        boolean hasActiveBudgets = vendorRepo.hasActiveBudgets();

        VendorBasketsDto dto = transformer.toVendorBasketsDto(vendorRow);
        dto.setBaskets(baskets);
        dto.setSubscriptioncount(subscriptionCount);
        dto.setHasBudgets(hasActiveBudgets);
        return dto;
    }

    @Override
    public List<BasketInfoDto> getBasketInfos(Long booksellerid, boolean allBaskets) {
        log.debug("Entering getBasketInfos - {}, {}", booksellerid, allBaskets);
        return vendorRepo.findBasketsInfoByVendor(booksellerid, allBaskets);
    }
}
