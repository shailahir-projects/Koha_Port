package com.shailahir.koha.catalog.service;

import com.shailahir.koha.catalog.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    Page<ItemDto> listItems(String externalId, String query, Pageable pageable);
    ItemDto getItem(Long itemId);
    void deleteItem(Long itemId);
    ItemDto addToBundle(Long itemId, BundleLinkDto bundleLink);
    List<ItemDto> listBundledItems(Long itemId, String externalId, String query, Pageable pageable);
    void removeFromBundle(Long itemId, String bundledItemId);
    List<BookingDto> getItemBookings(Long itemId, String query, Pageable pageable);
    List<LibraryDto> getItemPickupLocations(Long itemId, Long patronId, String query, Pageable pageable);
    Page<ItemDto> listItemsPublic(String externalId, String query, Pageable pageable);
}
