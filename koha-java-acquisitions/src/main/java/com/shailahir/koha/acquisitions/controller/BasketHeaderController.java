package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.repository.BasketHeaderRepository;
import com.shailahir.koha.acquisitions.repository.BasketRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * REST controller porting basketheader.pl — basket header add/edit form.
 *
 * <pre>
 *  GET  /acquisitions/baskets/{basketno}/header        — op=add_form (edit existing)
 *  GET  /acquisitions/vendors/{vendorid}/basket-form   — op=add_form (new basket)
 *  PUT  /acquisitions/baskets/{basketno}/header        — op=cud-add_validate (edit)
 *  POST /acquisitions/baskets/header                   — op=cud-add_validate (new)
 * </pre>
 */
@RestController
@RequiredArgsConstructor
public class BasketHeaderController {

    private final BasketHeaderRepository headerRepo;
    private final BasketRepository       basketRepo;

    // ── op=add_form (edit existing basket) ────────────────────────────────────

    /**
     * Returns form data for editing an existing basket header.
     * Mirrors the basketno-present branch of op=add_form in basketheader.pl:
     * <ul>
     *   <li>Fetches basket fields</li>
     *   <li>Loads active contracts for the vendor, marking the currently selected one</li>
     *   <li>Determines whether the basket name is read-only (EDI / po_is_basketname)</li>
     * </ul>
     */
    @GetMapping("/acquisitions/baskets/{basketno}/header", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BasketHeaderFormDto> getBasketHeaderForm(@PathVariable Long basketno) {
        BasketDto basket = basketRepo.findById(basketno)
                .orElseThrow(() -> new NoSuchElementException("Basket not found: " + basketno));

        Long booksellerid = basket.getBooksellerid();
        String vendorName = headerRepo.findVendorName(booksellerid).orElse("");

        List<ContractDto> contracts = headerRepo.findActiveContractsByVendor(booksellerid)
                .stream()
                .peek(c -> c.setSelected(
                        basket.getContractnumber() != null
                                && basket.getContractnumber().equals(c.getContractnumber())))
                .collect(Collectors.toList());

        boolean nameReadonly = headerRepo.isBasketNameReadonly(basketno);

        return ResponseEntity.ok(BasketHeaderFormDto.builder()
                .basketno(basketno)
                .basketname(basket.getBasketname())
                .note(basket.getNote())
                .booksellernote(basket.getBooksellernote())
                .booksellerid(booksellerid)
                .booksellername(vendorName)
                .contractnumber(basket.getContractnumber())
                .isStanding(basket.getIsStanding())
                .createItems(basket.getCreateItems())
                .billingplace(basket.getBillingplace())
                .deliveryplace(basket.getDeliveryplace())
                .contracts(contracts)
                .basketNameReadonly(nameReadonly)
                .isAnEdit(true)
                .build());
    }

    // ── op=add_form (new basket) ───────────────────────────────────────────────

    /**
     * Returns form data for creating a new basket for a given vendor.
     * Mirrors the no-basketno branch of op=add_form in basketheader.pl:
     * loads active contracts for the vendor (none pre-selected).
     */
    @GetMapping("/acquisitions/vendors/{booksellerid}/basket-form", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BasketHeaderFormDto> getNewBasketForm(@PathVariable Long booksellerid) {
        String vendorName = headerRepo.findVendorName(booksellerid).orElse("");
        List<ContractDto> contracts = headerRepo.findActiveContractsByVendor(booksellerid);

        return ResponseEntity.ok(BasketHeaderFormDto.builder()
                .booksellerid(booksellerid)
                .booksellername(vendorName)
                .contracts(contracts)
                .basketNameReadonly(false)
                .isAnEdit(false)
                .build());
    }

    // ── op=cud-add_validate (edit existing) ───────────────────────────────────

    /**
     * Saves changes to an existing basket header.
     * Mirrors ModBasketHeader() in basketheader.pl.
     * <p>
     * When the basket was created from an EDI order/quote and the vendor's EDI account
     * has {@code po_is_basketname} enabled, the basket name is preserved
     * (same protection as the Perl script applies).
     */
    @PutMapping("/acquisitions/baskets/{basketno}/header", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BasketDto> updateBasketHeader(
            @PathVariable Long basketno,
            @RequestBody @Valid BasketHeaderRequest request) {

        // Protect basket name when EDI PO-number setting is active
        String basketname = request.getBasketname();
        if (headerRepo.hasEdiOrderWithPoBasketname(basketno, request.getBooksellerid())) {
            basketname = headerRepo.findBasketName(basketno).orElse(basketname);
        }

        headerRepo.updateHeader(
                basketno,
                basketname,
                request.getNote(),
                request.getBooksellernote(),
                request.getContractnumber(),
                request.getBooksellerid(),
                request.getDeliveryplace(),
                request.getBillingplace(),
                request.getIsStanding(),
                request.getCreateItems());

        return basketRepo.findById(basketno)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── op=cud-add_validate (new basket) ──────────────────────────────────────

    /**
     * Creates a new basket.
     * Mirrors NewBasket() in basketheader.pl.
     * Returns the newly created basket with its assigned basketno.
     */
    @PostMapping("/acquisitions/baskets/header", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BasketDto> createBasket(@RequestBody @Valid BasketHeaderRequest request) {
        Long basketno = headerRepo.createBasket(
                request.getBooksellerid(),
                request.getAuthorisedby(),
                request.getBasketname(),
                request.getNote(),
                request.getBooksellernote(),
                request.getContractnumber(),
                request.getDeliveryplace(),
                request.getBillingplace(),
                request.getIsStanding(),
                request.getCreateItems());

        return basketRepo.findById(basketno)
                .map(b -> ResponseEntity.status(HttpStatus.CREATED).body(b))
                .orElse(ResponseEntity.internalServerError().build());
    }
}

