package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.service.BasketGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * REST controller porting all operations from basketgroup.pl.
 *
 * <pre>
 *  GET    /acquisitions/basket-groups?booksellerid=X      — op=display
 *  GET    /acquisitions/basket-groups/{id}                — op=add_form (single group detail)
 *  POST   /acquisitions/basket-groups                     — op=cud-attachbasket (create)
 *  PUT    /acquisitions/basket-groups/{id}                — op=cud-attachbasket (modify)
 *  DELETE /acquisitions/basket-groups/{id}                — op=cud-delete
 *  POST   /acquisitions/basket-groups/{id}/close          — op=closeandprint (close only)
 *  POST   /acquisitions/basket-groups/{id}/reopen         — op=cud-reopen
 *  PUT    /acquisitions/basket-groups/{id}/baskets/{no}   — op=cud-mod_basket (assign basket)
 *  GET    /acquisitions/basket-groups/{id}/export         — op=export (CSV)
 * </pre>
 *
 * Note: the PDF print operations (op=print, op=closeandprint print part, op=cud-ediprint)
 * involve external PDF/EDI libraries (Koha::pdfformat, Koha::EDI) whose Java equivalents
 * belong in dedicated rendering / EDI microservices. Those endpoints are documented
 * but return 501 Not Implemented here.
 */
@RestController
@RequiredArgsConstructor
public class BasketGroupController {

    private final BasketGroupService basketGroupService;

    /**
     * op=display — list all basket groups for a vendor plus unassigned closed baskets.
     * Mirrors displaybasketgroups() + BasketTotal().
     */
    @GetMapping("/acquisitions/basket-groups")
    public ResponseEntity<BasketGroupPageDto> getPage(
            @RequestParam("booksellerid") Long booksellerid) {
        return ResponseEntity.ok(basketGroupService.getPage(booksellerid));
    }

    /**
     * op=add_form — get single basket group detail with its assigned baskets.
     */
    @GetMapping("/acquisitions/basket-groups/{id}")
    public ResponseEntity<BasketGroupDto> getBasketGroup(@PathVariable Long id) {
        return ResponseEntity.ok(basketGroupService.getBasketGroup(id));
    }

    /**
     * op=cud-attachbasket (create) — creates a new basket group and assigns baskets.
     * Mirrors NewBasketgroup() + basket assignment loop.
     */
    @PostMapping("/acquisitions/basket-groups")
    public ResponseEntity<BasketGroupDto> createBasketGroup(
            @RequestBody BasketGroupRequest request) {
        BasketGroupDto created = basketGroupService.saveBasketGroup(null, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * op=cud-attachbasket (modify) — updates an existing basket group and reassigns baskets.
     * Mirrors ModBasketgroup() + basket assignment loop.
     */
    @PutMapping("/acquisitions/basket-groups/{id}")
    public ResponseEntity<BasketGroupDto> updateBasketGroup(
            @PathVariable Long id,
            @RequestBody BasketGroupRequest request) {
        return ResponseEntity.ok(basketGroupService.saveBasketGroup(id, request));
    }

    /**
     * op=cud-delete — deletes the basket group.
     * Unlinks all its baskets first (sets basketgroupid = NULL).
     * Mirrors DelBasketgroup().
     */
    @DeleteMapping("/acquisitions/basket-groups/{id}")
    public ResponseEntity<Void> deleteBasketGroup(@PathVariable Long id) {
        basketGroupService.deleteBasketGroup(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * op=closeandprint (close part) — closes the basket group.
     * The PDF generation part (printbasketgrouppdf) is not implemented here;
     * call GET /acquisitions/basket-groups/{id}/export for CSV export instead.
     * Mirrors CloseBasketgroup().
     */
    @PostMapping("/acquisitions/basket-groups/{id}/close")
    public ResponseEntity<Map<String, Object>> closeBasketGroup(@PathVariable Long id) {
        basketGroupService.closeBasketGroup(id);
        return ResponseEntity.ok(Map.of("closed", true, "basketgroupid", id));
    }

    /**
     * op=cud-reopen — reopens a closed basket group.
     * Mirrors ReOpenBasketgroup().
     */
    @PostMapping("/acquisitions/basket-groups/{id}/reopen")
    public ResponseEntity<Void> reopenBasketGroup(@PathVariable Long id) {
        basketGroupService.reopenBasketGroup(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * op=cud-mod_basket — assigns a single basket to this basket group.
     * Mirrors ModBasket({ basketno => ..., basketgroupid => ... }).
     */
    @PutMapping("/acquisitions/basket-groups/{id}/baskets/{basketno}")
    public ResponseEntity<Void> assignBasket(
            @PathVariable Long id,
            @PathVariable Long basketno) {
        basketGroupService.assignBasketToGroup(basketno, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * op=export — export all orders in the basket group as CSV.
     * Mirrors GetBasketGroupAsCSV().
     */
    @GetMapping("/acquisitions/basket-groups/{id}/export")
    public ResponseEntity<byte[]> exportAsCsv(@PathVariable Long id) {
        String csv = basketGroupService.exportAsCsv(id);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "basketgroup" + id + ".csv");
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * op=print / op=closeandprint (PDF) — not implemented.
     * PDF generation requires an external rendering library (Koha::pdfformat).
     */
    @GetMapping("/acquisitions/basket-groups/{id}/print")
    public ResponseEntity<Map<String, Object>> printPdf(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("error", "pdf_not_implemented",
                        "message", "PDF generation is handled by a dedicated rendering service"));
    }

    /**
     * op=cud-ediprint — not implemented here.
     * EDI order generation belongs in the EDI microservice.
     */
    @PostMapping("/acquisitions/basket-groups/{id}/ediprint")
    public ResponseEntity<Map<String, Object>> ediPrint(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("error", "edi_not_implemented",
                        "message", "EDI order generation is handled by the EDI microservice"));
    }
}

