package com.shailahir.koha.catalog.controller;

import com.shailahir.koha.catalog.dto.BookingDto;
import com.shailahir.koha.catalog.dto.BundleLinkDto;
import com.shailahir.koha.catalog.dto.ItemDto;
import com.shailahir.koha.catalog.dto.LibraryDto;
import com.shailahir.koha.catalog.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for items.
 * Maps to Swagger paths: /items, /items/{item_id}, /items/{item_id}/bundled_items,
 * /items/{item_id}/bundled_items/{bundled_item_id}, /items/{item_id}/bookings,
 * /items/{item_id}/pickup_locations, /public/items
 */
@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<Page<ItemDto>> listItems(
            @RequestParam(value = "external_id", required = false) String externalId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemService.listItems(externalId, query, pageable));
    }

    @GetMapping("/items/{item_id}")
    public ResponseEntity<ItemDto> getItem(
            @PathVariable("item_id") Long itemId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed) {
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

    @DeleteMapping("/items/{item_id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("item_id") Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // ── /items/{item_id}/bundled_items ──

    @PostMapping("/items/{item_id}/bundled_items")
    public ResponseEntity<ItemDto> addToBundle(
            @PathVariable("item_id") Long itemId,
            @RequestBody BundleLinkDto bundleLink) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addToBundle(itemId, bundleLink));
    }

    @GetMapping("/items/{item_id}/bundled_items")
    public ResponseEntity<List<ItemDto>> listBundledItems(
            @PathVariable("item_id") Long itemId,
            @RequestParam(value = "external_id", required = false) String externalId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemService.listBundledItems(itemId, externalId, query, pageable));
    }

    @DeleteMapping("/items/{item_id}/bundled_items/{bundled_item_id}")
    public ResponseEntity<Void> removeFromBundle(
            @PathVariable("item_id") Long itemId,
            @PathVariable("bundled_item_id") String bundledItemId) {
        itemService.removeFromBundle(itemId, bundledItemId);
        return ResponseEntity.noContent().build();
    }

    // ── /items/{item_id}/bookings ──

    @GetMapping("/items/{item_id}/bookings")
    public ResponseEntity<List<BookingDto>> getItemBookings(
            @PathVariable("item_id") Long itemId,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemService.getItemBookings(itemId, query, pageable));
    }

    // ── /items/{item_id}/pickup_locations ──

    @GetMapping("/items/{item_id}/pickup_locations")
    public ResponseEntity<List<LibraryDto>> getItemPickupLocations(
            @PathVariable("item_id") Long itemId,
            @RequestParam("patron_id") Long patronId,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemService.getItemPickupLocations(itemId, patronId, query, pageable));
    }

    // ── /public/items ──

    @GetMapping("/public/items")
    public ResponseEntity<Page<ItemDto>> listItemsPublic(
            @RequestParam(value = "external_id", required = false) String externalId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemService.listItemsPublic(externalId, query, pageable));
    }
}
