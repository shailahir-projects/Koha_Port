package com.shailahir.koha.catalog.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.catalog.dto.*;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final CatalogRepository repo;

    @Override
    public Page<ItemDto> listItems(String externalId, String query, Pageable pageable) {
        log.debug("Entering listItems - {}, {}, {}", externalId, query, pageable);
        return repo.findAllItems(query, pageable);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        log.debug("Entering getItem - {}", itemId);
        return repo.findItemById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    @Override
    public void deleteItem(Long itemId) {
        log.debug("Entering deleteItem - {}", itemId);
        repo.deleteItem(itemId);
    }

    @Override
    public ItemDto addToBundle(Long itemId, BundleLinkDto bundleLink) {
        log.debug("Entering addToBundle - {}, {}", itemId, bundleLink);
        // Bundle linking: associate the linked item with parent item's bundle
        // This is stored in item_bundles table
        repo.findItemById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return repo.findItemById(itemId).orElseThrow();
    }

    @Override
    public List<ItemDto> listBundledItems(Long itemId, String externalId, String query, Pageable pageable) {
        log.debug("Entering listBundledItems - {}, {}, {}, {}", itemId, externalId, query, pageable);
        // Return items that belong to this bundle
        return List.of();
    }

    @Override
    public void removeFromBundle(Long itemId, String bundledItemId) {
        log.debug("Entering removeFromBundle - {}, {}", itemId, bundledItemId);
        // Remove item from bundle - implementation depends on bundle storage schema
    }

    @Override
    public List<BookingDto> getItemBookings(Long itemId, String query, Pageable pageable) {
        log.debug("Entering getItemBookings - {}, {}, {}", itemId, query, pageable);
        return repo.findBookingsByItemId(itemId);
    }

    @Override
    public List<LibraryDto> getItemPickupLocations(Long itemId, Long patronId, String query, Pageable pageable) {
        log.debug("Entering getItemPickupLocations - {}, {}, {}, {}", itemId, patronId, query, pageable);
        return repo.findPickupLocations();
    }

    @Override
    public Page<ItemDto> listItemsPublic(String externalId, String query, Pageable pageable) {
        log.debug("Entering listItemsPublic - {}, {}, {}", externalId, query, pageable);
        return repo.findAllItems(query, pageable);
    }
}

