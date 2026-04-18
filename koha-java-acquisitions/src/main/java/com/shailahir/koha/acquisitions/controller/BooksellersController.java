package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.BasketInfoDto;
import com.shailahir.koha.acquisitions.dto.VendorBasketsDto;
import com.shailahir.koha.acquisitions.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller porting booksellers.pl — vendor basket overview page.
 *
 * <pre>
 *  GET /acquisitions/vendors/{booksellerid}/baskets[?allbaskets=1]
 * </pre>
 *
 * Mirrors the full page assembled by booksellers.pl:
 * vendor summary (name, active, basket count, subscription count),
 * per-basket enrichment (total_items, total_biblios, expected_items,
 * basket group linkage, authorisedby patron name), and has_budgets flag.
 */
@RestController
@RequiredArgsConstructor
public class BooksellersController {

    private final VendorRepository vendorRepository;

    /**
     * Returns vendor info and an enriched list of baskets for that vendor.
     * <p>
     * Mirrors the main logic block of booksellers.pl:
     * <ol>
     *   <li>Loads vendor from aqbooksellers</li>
     *   <li>Calls GetBasketsInfosByBookseller (with allbaskets flag)</li>
     *   <li>For each basket: resolves authorisedby patron name and basket group name</li>
     *   <li>Checks has_budgets (at least one active budget exists)</li>
     * </ol>
     *
     * @param booksellerid vendor id
     * @param allbaskets   when 1, include closed baskets; when 0 (default), open only
     * @return vendor basket overview DTO
     */
    @GetMapping("/acquisitions/vendors/{booksellerid}/baskets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<VendorBasketsDto> getVendorBaskets(
            @PathVariable Long booksellerid,
            @RequestParam(value = "allbaskets", defaultValue = "0") int allbaskets) {

        Map<String, Object> vendor = vendorRepository.findVendorById(booksellerid)
                .orElseThrow(() -> new NoSuchElementException("Vendor not found: " + booksellerid));

        boolean showAll = allbaskets == 1;
        List<BasketInfoDto> baskets = vendorRepository.findBasketsInfoByVendor(booksellerid, showAll);

        boolean hasBudgets = vendorRepository.hasActiveBudgets();

        return ResponseEntity.ok(VendorBasketsDto.builder()
                .booksellerid(booksellerid)
                .name((String) vendor.get("name"))
                .active(intToBool(vendor.get("active")))
                .vendorType((String) vendor.get("type"))
                .basketcount(vendorRepository.countBaskets(booksellerid))
                .subscriptioncount(vendorRepository.countSubscriptions(booksellerid))
                .baskets(baskets)
                .hasBudgets(hasBudgets)
                .build());
    }

    private Boolean intToBool(Object v) {
        if (v == null) return false;
        if (v instanceof Boolean b) return b;
        return Integer.parseInt(v.toString()) != 0;
    }
}

