package com.shailahir.koha.catalog.service.impl;

import com.shailahir.koha.catalog.dto.*;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.BiblioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BiblioServiceImpl implements BiblioService {

    private final CatalogRepository repo;

    @Override
    public BiblioDto addBiblio(BiblioDto biblio, String frameworkId, String marcSchema,
                               Boolean confirmNotDuplicate, Long recordSourceId) {
        if (frameworkId != null) biblio.setFrameworkCode(frameworkId);
        return repo.insertBiblio(biblio);
    }

    @Override
    public Page<BiblioDto> listBiblios(String query, Pageable pageable) {
        return repo.findAllBiblios(query, pageable);
    }

    @Override
    public BiblioDto getBiblio(Long biblioId) {
        return repo.findBiblioById(biblioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Biblio not found"));
    }

    @Override
    public BiblioDto updateBiblio(Long biblioId, BiblioDto biblio, String frameworkId, String marcSchema,
                                   Boolean confirmNotDuplicate, Long recordSourceId) {
        repo.findBiblioById(biblioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Biblio not found"));
        if (frameworkId != null) biblio.setFrameworkCode(frameworkId);
        return repo.updateBiblio(biblioId, biblio);
    }

    @Override
    public void deleteBiblio(Long biblioId) {
        repo.deleteBiblio(biblioId);
    }

    @Override
    public List<ItemDto> getBiblioItems(Long biblioId, Boolean bookable, String query, Pageable pageable) {
        return repo.findItemsByBiblioId(biblioId, bookable);
    }

    @Override
    public ItemDto addBiblioItem(Long biblioId, ItemDto item) {
        return repo.insertItem(biblioId, item);
    }

    @Override
    public ItemDto updateBiblioItem(Long biblioId, Long itemId, ItemDto item) {
        return repo.updateItem(biblioId, itemId, item);
    }

    @Override
    public List<BookingDto> getBiblioBookings(Long biblioId, String query, Pageable pageable) {
        return repo.findBookingsByBiblioId(biblioId);
    }

    @Override
    public List<CheckoutDto> getBiblioCheckouts(Long biblioId, Boolean checkedIn, String query, Pageable pageable) {
        return repo.findCheckoutsByBiblioId(biblioId, checkedIn);
    }

    @Override
    public List<LibraryDto> getBiblioPickupLocations(Long biblioId, Long patronId, String query, Pageable pageable) {
        return repo.findPickupLocations();
    }

    @Override
    public BiblioDto mergeBiblio(Long biblioId, MergeBibliosDto mergeRequest) {
        if (mergeRequest.getBiblionumbers() != null) {
            for (Long fromId : mergeRequest.getBiblionumbers()) {
                if (!fromId.equals(biblioId)) {
                    repo.mergeBiblios(biblioId, fromId);
                }
            }
        }
        return repo.findBiblioById(biblioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Biblio not found"));
    }

    @Override
    public BiblioDto getPublicBiblio(Long biblioId) {
        return repo.findBiblioById(biblioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Biblio not found"));
    }

    @Override
    public List<ItemDto> getPublicBiblioItems(Long biblioId, String query, Pageable pageable) {
        return repo.findItemsByBiblioId(biblioId, null);
    }

    @Override
    public RatingResultDto setRating(Long biblioId, RatingDto rating) {
        // Use a placeholder borrower number of 0 when not authenticated context
        return repo.setRating(biblioId, 0L, rating.getRating());
    }

    @Override
    public Page<BiblioDto> listDeletedBiblios(String query, Pageable pageable) {
        return repo.findDeletedBiblios(query, pageable);
    }

    @Override
    public BiblioDto getDeletedBiblio(Long biblioId) {
        return repo.findDeletedBiblioById(biblioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deleted biblio not found"));
    }
}

