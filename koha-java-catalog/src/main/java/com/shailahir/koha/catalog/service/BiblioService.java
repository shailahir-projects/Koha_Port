package com.shailahir.koha.catalog.service;

import com.shailahir.koha.catalog.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BiblioService {
    BiblioDto addBiblio(BiblioDto biblio, String frameworkId, String marcSchema, Boolean confirmNotDuplicate, Long recordSourceId);
    Page<BiblioDto> listBiblios(String query, Pageable pageable);
    BiblioDto getBiblio(Long biblioId);
    BiblioDto updateBiblio(Long biblioId, BiblioDto biblio, String frameworkId, String marcSchema, Boolean confirmNotDuplicate, Long recordSourceId);
    void deleteBiblio(Long biblioId);
    List<ItemDto> getBiblioItems(Long biblioId, Boolean bookable, String query, Pageable pageable);
    ItemDto addBiblioItem(Long biblioId, ItemDto item);
    ItemDto updateBiblioItem(Long biblioId, Long itemId, ItemDto item);
    List<BookingDto> getBiblioBookings(Long biblioId, String query, Pageable pageable);
    List<CheckoutDto> getBiblioCheckouts(Long biblioId, Boolean checkedIn, String query, Pageable pageable);
    List<LibraryDto> getBiblioPickupLocations(Long biblioId, Long patronId, String query, Pageable pageable);
    BiblioDto mergeBiblio(Long biblioId, MergeBibliosDto mergeRequest);
    BiblioDto getPublicBiblio(Long biblioId);
    List<ItemDto> getPublicBiblioItems(Long biblioId, String query, Pageable pageable);
    RatingResultDto setRating(Long biblioId, RatingDto rating);
    Page<BiblioDto> listDeletedBiblios(String query, Pageable pageable);
    BiblioDto getDeletedBiblio(Long biblioId);
}
