package com.shailahir.koha.catalog.controller;

import com.shailahir.koha.catalog.dto.*;
import com.shailahir.koha.catalog.service.BiblioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * REST controller for bibliographic records.
 * Maps to Swagger paths: /biblios, /biblios/{biblio_id}, /biblios/{biblio_id}/items,
 * /biblios/{biblio_id}/items/{item_id}, /biblios/{biblio_id}/pickup_locations,
 * /biblios/{biblio_id}/bookings, /biblios/{biblio_id}/checkouts,
 * /biblios/{biblio_id}/merge,
 * /public/biblios/{biblio_id}, /public/biblios/{biblio_id}/items,
 * /public/biblios/{biblio_id}/ratings,
 * /deleted/biblios, /deleted/biblios/{biblio_id}
 */
@RestController
@RequiredArgsConstructor
public class BiblioController {

    private final BiblioService biblioService;

    // ── /biblios ──

    @PostMapping("/biblios", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BiblioDto> addBiblio(
            @RequestBody BiblioDto biblio,
            @RequestHeader(value = "x-framework-id", required = false) String frameworkId,
            @RequestHeader(value = "x-marc-schema", required = false) String marcSchema,
            @RequestHeader(value = "x-confirm-not-duplicate", required = false) Boolean confirmNotDuplicate,
            @RequestHeader(value = "x-record-source-id", required = false) Long recordSourceId) {
        BiblioDto created = biblioService.addBiblio(biblio, frameworkId, marcSchema, confirmNotDuplicate, recordSourceId);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/biblios", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<BiblioDto>> listBiblios(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.listBiblios(query, pageable));
    }

    // ── /biblios/{biblio_id} ──

    @GetMapping("/biblios/{biblio_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BiblioDto> getBiblio(@PathVariable("biblio_id") Long biblioId) {
        return ResponseEntity.ok(biblioService.getBiblio(biblioId));
    }

    @PutMapping("/biblios/{biblio_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BiblioDto> updateBiblio(
            @PathVariable("biblio_id") Long biblioId,
            @RequestBody BiblioDto biblio,
            @RequestHeader(value = "x-framework-id", required = false) String frameworkId,
            @RequestHeader(value = "x-marc-schema", required = false) String marcSchema,
            @RequestHeader(value = "x-confirm-not-duplicate", required = false) Boolean confirmNotDuplicate,
            @RequestHeader(value = "x-record-source-id", required = false) Long recordSourceId) {
        return ResponseEntity.ok(biblioService.updateBiblio(biblioId, biblio, frameworkId, marcSchema, confirmNotDuplicate, recordSourceId));
    }

    @DeleteMapping("/biblios/{biblio_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteBiblio(@PathVariable("biblio_id") Long biblioId) {
        biblioService.deleteBiblio(biblioId);
        return ResponseEntity.noContent().build();
    }

    // ── /biblios/{biblio_id}/bookings ──

    @GetMapping("/biblios/{biblio_id}/bookings", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<BookingDto>> getBiblioBookings(
            @PathVariable("biblio_id") Long biblioId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.getBiblioBookings(biblioId, query, pageable));
    }

    // ── /biblios/{biblio_id}/checkouts ──

    @GetMapping("/biblios/{biblio_id}/checkouts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CheckoutDto>> getBiblioCheckouts(
            @PathVariable("biblio_id") Long biblioId,
            @RequestParam(value = "checked_in", required = false) Boolean checkedIn,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.getBiblioCheckouts(biblioId, checkedIn, query, pageable));
    }

    // ── /biblios/{biblio_id}/items ──

    @GetMapping("/biblios/{biblio_id}/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ItemDto>> getBiblioItems(
            @PathVariable("biblio_id") Long biblioId,
            @RequestParam(value = "bookable", required = false) Boolean bookable,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.getBiblioItems(biblioId, bookable, query, pageable));
    }

    @PostMapping("/biblios/{biblio_id}/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemDto> addBiblioItem(
            @PathVariable("biblio_id") Long biblioId,
            @RequestBody ItemDto item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(biblioService.addBiblioItem(biblioId, item));
    }

    // ── /biblios/{biblio_id}/items/{item_id} ──

    @PutMapping("/biblios/{biblio_id}/items/{item_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemDto> updateBiblioItem(
            @PathVariable("biblio_id") Long biblioId,
            @PathVariable("item_id") Long itemId,
            @RequestBody ItemDto item) {
        return ResponseEntity.ok(biblioService.updateBiblioItem(biblioId, itemId, item));
    }

    // ── /biblios/{biblio_id}/pickup_locations ──

    @GetMapping("/biblios/{biblio_id}/pickup_locations", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<LibraryDto>> getBiblioPickupLocations(
            @PathVariable("biblio_id") Long biblioId,
            @RequestParam("patron_id") Long patronId,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.getBiblioPickupLocations(biblioId, patronId, query, pageable));
    }

    // ── /biblios/{biblio_id}/merge ──

    @PostMapping("/biblios/{biblio_id}/merge", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BiblioDto> mergeBiblio(
            @PathVariable("biblio_id") Long biblioId,
            @RequestBody MergeBibliosDto mergeRequest) {
        return ResponseEntity.ok(biblioService.mergeBiblio(biblioId, mergeRequest));
    }

    // ── /public/biblios/{biblio_id} ──

    @GetMapping("/public/biblios/{biblio_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BiblioDto> getBiblioPublic(@PathVariable("biblio_id") Long biblioId) {
        return ResponseEntity.ok(biblioService.getPublicBiblio(biblioId));
    }

    // ── /public/biblios/{biblio_id}/items ──

    @GetMapping("/public/biblios/{biblio_id}/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ItemDto>> getBiblioItemsPublic(
            @PathVariable("biblio_id") Long biblioId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.getPublicBiblioItems(biblioId, query, pageable));
    }

    // ── /public/biblios/{biblio_id}/ratings ──

    @PostMapping("/public/biblios/{biblio_id}/ratings", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RatingResultDto> setRating(
            @PathVariable("biblio_id") Long biblioId,
            @RequestBody RatingDto rating) {
        return ResponseEntity.ok(biblioService.setRating(biblioId, rating));
    }

    // ── /deleted/biblios ──

    @GetMapping("/deleted/biblios", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<BiblioDto>> listDeletedBiblios(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(biblioService.listDeletedBiblios(query, pageable));
    }

    @GetMapping("/deleted/biblios/{biblio_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BiblioDto> getDeletedBiblio(@PathVariable("biblio_id") Long biblioId) {
        return ResponseEntity.ok(biblioService.getDeletedBiblio(biblioId));
    }
}
