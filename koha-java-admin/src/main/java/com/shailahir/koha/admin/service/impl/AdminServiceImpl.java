package com.shailahir.koha.admin.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.admin.dto.*;
import com.shailahir.koha.admin.repository.AdminRepository;
import com.shailahir.koha.admin.repository.Sip2PreservationRepository;
import com.shailahir.koha.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for administration operations.
 * Covers all admin/ Perl script functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final Sip2PreservationRepository sip2PreservationRepository;
    private final JdbcTemplate jdbc;

    // ── Libraries ──────────────────────────────────────────────────────────────

    @Override
    public Page<LibraryDto> listLibraries(String query, Pageable pageable) {
        log.debug("Entering listLibraries - {}, {}", query, pageable);
        return adminRepository.findAllLibraries(query, pageable);
    }

    @Override
    @Transactional
    public LibraryDto addLibrary(LibraryDto dto) {
        log.debug("Entering addLibrary - {}", dto);
        return adminRepository.insertLibrary(dto);
    }

    @Override
    public LibraryDto getLibrary(String libraryId) {
        log.debug("Entering getLibrary - {}", libraryId);
        return adminRepository.findLibraryById(libraryId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Library not found: " + libraryId));
    }

    @Override
    @Transactional
    public LibraryDto updateLibrary(String libraryId, LibraryDto dto) {
        log.debug("Entering updateLibrary - {}, {}", libraryId, dto);
        return adminRepository.updateLibrary(libraryId, dto);
    }

    @Override
    @Transactional
    public void deleteLibrary(String libraryId) {
        log.debug("Entering deleteLibrary - {}", libraryId);
        adminRepository.deleteLibrary(libraryId);
    }

    @Override
    public List<DeskDto> listLibraryDesks(String libraryId) {
        log.debug("Entering listLibraryDesks - {}", libraryId);
        return adminRepository.findDesksByLibrary(libraryId);
    }

    @Override
    public List<CashRegisterDto> listLibraryCashRegisters(String libraryId) {
        log.debug("Entering listLibraryCashRegisters - {}", libraryId);
        return adminRepository.findCashRegistersByLibrary(libraryId);
    }

    @Override
    public List<LibraryDto> listLibrariesPublic(Pageable pageable) {
        log.debug("Entering listLibrariesPublic - {}", pageable);
        return adminRepository.findAllLibraries(null, pageable).getContent();
    }

    @Override
    public LibraryDto getLibraryPublic(String libraryId) {
        log.debug("Entering getLibraryPublic - {}", libraryId);
        return getLibrary(libraryId);
    }

    // ── Cities ─────────────────────────────────────────────────────────────────

    @Override
    public Page<CityDto> listCities(String query, Pageable pageable) {
        log.debug("Entering listCities - {}, {}", query, pageable);
        return adminRepository.findAllCities(query, pageable);
    }

    @Override
    @Transactional
    public CityDto addCity(CityDto dto) {
        log.debug("Entering addCity - {}", dto);
        return adminRepository.insertCity(dto);
    }

    @Override
    public CityDto getCity(Long cityId) {
        log.debug("Entering getCity - {}", cityId);
        return adminRepository.findCityById(cityId)
            .orElseThrow(() -> new java.util.NoSuchElementException("City not found: " + cityId));
    }

    @Override
    @Transactional
    public CityDto updateCity(Long cityId, CityDto dto) {
        log.debug("Entering updateCity - {}, {}", cityId, dto);
        return adminRepository.updateCity(cityId, dto);
    }

    @Override
    @Transactional
    public void deleteCity(Long cityId) {
        log.debug("Entering deleteCity - {}", cityId);
        adminRepository.deleteCity(cityId);
    }

    // ── Authorised Values ──────────────────────────────────────────────────────

    @Override
    public List<AuthorisedValueDto> listAuthorisedValues(String categoryName, Pageable pageable) {
        log.debug("Entering listAuthorisedValues - {}, {}", categoryName, pageable);
        return adminRepository.findAuthorisedValuesByCategory(categoryName, pageable);
    }

    @Override
    public List<AuthorisedValueCategoryDto> listAuthorisedValueCategories() {
        log.debug("Entering listAuthorisedValueCategories");
        return adminRepository.findAllAuthorisedValueCategories();
    }

    // ── Advanced Editor Macros ─────────────────────────────────────────────────

    @Override
    public Page<AdvancedEditorMacroDto> listMacros(Pageable pageable) {
        log.debug("Entering listMacros - {}", pageable);
        return adminRepository.findAllMacros(pageable);
    }

    @Override
    @Transactional
    public AdvancedEditorMacroDto addMacro(AdvancedEditorMacroDto dto) {
        log.debug("Entering addMacro - {}", dto);
        dto.setShared(false);
        return adminRepository.insertMacro(dto);
    }

    @Override
    @Transactional
    public AdvancedEditorMacroDto addSharedMacro(AdvancedEditorMacroDto dto) {
        log.debug("Entering addSharedMacro - {}", dto);
        dto.setShared(true);
        return adminRepository.insertMacro(dto);
    }

    @Override
    public AdvancedEditorMacroDto getMacro(Long macroId) {
        log.debug("Entering getMacro - {}", macroId);
        return adminRepository.findMacroById(macroId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Macro not found: " + macroId));
    }

    @Override
    @Transactional
    public AdvancedEditorMacroDto updateMacro(Long macroId, AdvancedEditorMacroDto dto) {
        log.debug("Entering updateMacro - {}, {}", macroId, dto);
        return adminRepository.updateMacro(macroId, dto);
    }

    @Override
    @Transactional
    public void deleteMacro(Long macroId) {
        log.debug("Entering deleteMacro - {}", macroId);
        adminRepository.deleteMacro(macroId);
    }

    @Override
    public AdvancedEditorMacroDto getSharedMacro(Long macroId) {
        log.debug("Entering getSharedMacro - {}", macroId);
        return getMacro(macroId);
    }

    @Override
    @Transactional
    public AdvancedEditorMacroDto updateSharedMacro(Long macroId, AdvancedEditorMacroDto dto) {
        log.debug("Entering updateSharedMacro - {}, {}", macroId, dto);
        dto.setShared(true);
        return adminRepository.updateMacro(macroId, dto);
    }

    @Override
    @Transactional
    public void deleteSharedMacro(Long macroId) {
        log.debug("Entering deleteSharedMacro - {}", macroId);
        adminRepository.deleteMacro(macroId);
    }

    // ── Extended Attribute Types ───────────────────────────────────────────────

    @Override
    public List<ExtendedAttributeTypeDto> listExtendedAttributeTypes() {
        log.debug("Entering listExtendedAttributeTypes");
        return adminRepository.findAllExtendedAttributeTypes();
    }

    // ── Transfer Limits ────────────────────────────────────────────────────────

    @Override
    public List<TransferLimitDto> listTransferLimits() {
        log.debug("Entering listTransferLimits");
        return adminRepository.findAllTransferLimits();
    }

    @Override
    @Transactional
    public TransferLimitDto addTransferLimit(TransferLimitDto dto) {
        log.debug("Entering addTransferLimit - {}", dto);
        return adminRepository.insertTransferLimit(dto);
    }

    @Override
    @Transactional
    public void deleteTransferLimit(Long limitId) {
        log.debug("Entering deleteTransferLimit - {}", limitId);
        adminRepository.deleteTransferLimit(limitId);
    }

    @Override
    @Transactional
    public List<TransferLimitDto> batchAddTransferLimits(List<TransferLimitDto> dtos) {
        log.debug("Entering batchAddTransferLimits - {}", dtos);
        List<TransferLimitDto> result = new ArrayList<>();
        for (TransferLimitDto dto : dtos) {
            result.add(adminRepository.insertTransferLimit(dto));
        }
        return result;
    }

    @Override
    @Transactional
    public void batchDeleteTransferLimits(List<Long> limitIds) {
        log.debug("Entering batchDeleteTransferLimits - {}", limitIds);
        for (Long limitId : limitIds) {
            adminRepository.deleteTransferLimit(limitId);
        }
    }

    // ── Tickets ────────────────────────────────────────────────────────────────

    @Override
    public Page<TicketDto> listTickets(Pageable pageable) {
        log.debug("Entering listTickets - {}", pageable);
        return adminRepository.findAllTickets(pageable);
    }

    @Override
    @Transactional
    public TicketDto addTicket(TicketDto dto) {
        log.debug("Entering addTicket - {}", dto);
        return adminRepository.insertTicket(dto);
    }

    @Override
    public TicketDto getTicket(Long ticketId) {
        log.debug("Entering getTicket - {}", ticketId);
        return adminRepository.findTicketById(ticketId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Ticket not found: " + ticketId));
    }

    @Override
    @Transactional
    public TicketDto updateTicket(Long ticketId, TicketDto dto) {
        log.debug("Entering updateTicket - {}, {}", ticketId, dto);
        return adminRepository.updateTicket(ticketId, dto);
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId) {
        log.debug("Entering deleteTicket - {}", ticketId);
        adminRepository.deleteTicket(ticketId);
    }

    @Override
    public List<TicketUpdateDto> listTicketUpdates(Long ticketId) {
        log.debug("Entering listTicketUpdates - {}", ticketId);
        return adminRepository.findTicketUpdates(ticketId);
    }

    @Override
    @Transactional
    public TicketUpdateDto addTicketUpdate(Long ticketId, TicketUpdateDto dto) {
        log.debug("Entering addTicketUpdate - {}, {}", ticketId, dto);
        return adminRepository.insertTicketUpdate(ticketId, dto);
    }

    @Override
    @Transactional
    public TicketDto addTicketPublic(TicketDto dto) {
        log.debug("Entering addTicketPublic - {}", dto);
        return adminRepository.insertTicket(dto);
    }

    // ── SIP2 Accounts ──────────────────────────────────────────────────────────

    @Override
    public Page<Sip2AccountDto> listSip2Accounts(Pageable pageable) {
        log.debug("Entering listSip2Accounts - {}", pageable);
        return sip2PreservationRepository.findAllSip2Accounts(pageable);
    }

    @Override
    @Transactional
    public Sip2AccountDto addSip2Account(Sip2AccountDto dto) {
        log.debug("Entering addSip2Account - {}", dto);
        return sip2PreservationRepository.insertSip2Account(dto);
    }

    @Override
    public Sip2AccountDto getSip2Account(Long sipAccountId) {
        log.debug("Entering getSip2Account - {}", sipAccountId);
        return sip2PreservationRepository.findSip2AccountById(sipAccountId)
            .orElseThrow(() -> new java.util.NoSuchElementException("SIP2 account not found: " + sipAccountId));
    }

    @Override
    @Transactional
    public Sip2AccountDto updateSip2Account(Long sipAccountId, Sip2AccountDto dto) {
        log.debug("Entering updateSip2Account - {}, {}", sipAccountId, dto);
        return sip2PreservationRepository.updateSip2Account(sipAccountId, dto);
    }

    @Override
    @Transactional
    public void deleteSip2Account(Long sipAccountId) {
        log.debug("Entering deleteSip2Account - {}", sipAccountId);
        sip2PreservationRepository.deleteSip2Account(sipAccountId);
    }

    // ── SIP2 Institutions ──────────────────────────────────────────────────────

    @Override
    public Page<Sip2InstitutionDto> listSip2Institutions(Pageable pageable) {
        log.debug("Entering listSip2Institutions - {}", pageable);
        return sip2PreservationRepository.findAllSip2Institutions(pageable);
    }

    @Override
    @Transactional
    public Sip2InstitutionDto addSip2Institution(Sip2InstitutionDto dto) {
        log.debug("Entering addSip2Institution - {}", dto);
        return sip2PreservationRepository.insertSip2Institution(dto);
    }

    @Override
    public Sip2InstitutionDto getSip2Institution(Long sipInstitutionId) {
        log.debug("Entering getSip2Institution - {}", sipInstitutionId);
        return sip2PreservationRepository.findSip2InstitutionById(sipInstitutionId)
            .orElseThrow(() -> new java.util.NoSuchElementException("SIP2 institution not found: " + sipInstitutionId));
    }

    @Override
    @Transactional
    public Sip2InstitutionDto updateSip2Institution(Long sipInstitutionId, Sip2InstitutionDto dto) {
        log.debug("Entering updateSip2Institution - {}, {}", sipInstitutionId, dto);
        return sip2PreservationRepository.updateSip2Institution(sipInstitutionId, dto);
    }

    @Override
    @Transactional
    public void deleteSip2Institution(Long sipInstitutionId) {
        log.debug("Entering deleteSip2Institution - {}", sipInstitutionId);
        sip2PreservationRepository.deleteSip2Institution(sipInstitutionId);
    }

    // ── SIP2 System Preference Overrides ──────────────────────────────────────

    @Override
    public Page<Sip2SystemPreferenceOverrideDto> listSip2SystemPreferenceOverrides(Pageable pageable) {
        log.debug("Entering listSip2SystemPreferenceOverrides - {}", pageable);
        return sip2PreservationRepository.findAllSip2Overrides(pageable);
    }

    @Override
    @Transactional
    public Sip2SystemPreferenceOverrideDto addSip2SystemPreferenceOverride(Sip2SystemPreferenceOverrideDto dto) {
        log.debug("Entering addSip2SystemPreferenceOverride - {}", dto);
        return sip2PreservationRepository.insertSip2Override(dto);
    }

    @Override
    public Sip2SystemPreferenceOverrideDto getSip2SystemPreferenceOverride(Long overrideId) {
        log.debug("Entering getSip2SystemPreferenceOverride - {}", overrideId);
        return sip2PreservationRepository.findSip2OverrideById(overrideId)
            .orElseThrow(() -> new java.util.NoSuchElementException("SIP2 override not found: " + overrideId));
    }

    @Override
    @Transactional
    public Sip2SystemPreferenceOverrideDto updateSip2SystemPreferenceOverride(Long overrideId, Sip2SystemPreferenceOverrideDto dto) {
        log.debug("Entering updateSip2SystemPreferenceOverride - {}, {}", overrideId, dto);
        return sip2PreservationRepository.updateSip2Override(overrideId, dto);
    }

    @Override
    @Transactional
    public void deleteSip2SystemPreferenceOverride(Long overrideId) {
        log.debug("Entering deleteSip2SystemPreferenceOverride - {}", overrideId);
        sip2PreservationRepository.deleteSip2Override(overrideId);
    }

    // ── Preservation Config ────────────────────────────────────────────────────

    @Override
    public PreservationConfigDto getPreservationConfig() {
        log.debug("Entering getPreservationConfig");
        Map<String, Object> config = new HashMap<>();
        try {
            config.put("waiting_list_items", sip2PreservationRepository.findWaitingListItems().size());
            config.put("active_trains", sip2PreservationRepository.findAllTrains().size());
            config.put("processings", sip2PreservationRepository.findAllProcessings().size());
        } catch (Exception e) {
            config.put("error", "Preservation module not configured");
        }
        return PreservationConfigDto.builder().config(config).build();
    }

    // ── Preservation Processings ───────────────────────────────────────────────

    @Override
    public List<PreservationProcessingDto> listPreservationProcessings() {
        log.debug("Entering listPreservationProcessings");
        return sip2PreservationRepository.findAllProcessings();
    }

    @Override
    @Transactional
    public PreservationProcessingDto addPreservationProcessing(PreservationProcessingDto dto) {
        log.debug("Entering addPreservationProcessing - {}", dto);
        return sip2PreservationRepository.insertProcessing(dto);
    }

    @Override
    public PreservationProcessingDto getPreservationProcessing(Long processingId) {
        log.debug("Entering getPreservationProcessing - {}", processingId);
        return sip2PreservationRepository.findProcessingById(processingId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Processing not found: " + processingId));
    }

    @Override
    @Transactional
    public PreservationProcessingDto updatePreservationProcessing(Long processingId, PreservationProcessingDto dto) {
        log.debug("Entering updatePreservationProcessing - {}, {}", processingId, dto);
        return sip2PreservationRepository.updateProcessing(processingId, dto);
    }

    @Override
    @Transactional
    public void deletePreservationProcessing(Long processingId) {
        log.debug("Entering deletePreservationProcessing - {}", processingId);
        sip2PreservationRepository.deleteProcessing(processingId);
    }

    // ── Preservation Trains ────────────────────────────────────────────────────

    @Override
    public List<PreservationTrainDto> listPreservationTrains() {
        log.debug("Entering listPreservationTrains");
        return sip2PreservationRepository.findAllTrains();
    }

    @Override
    @Transactional
    public PreservationTrainDto addPreservationTrain(PreservationTrainDto dto) {
        log.debug("Entering addPreservationTrain - {}", dto);
        return sip2PreservationRepository.insertTrain(dto);
    }

    @Override
    public PreservationTrainDto getPreservationTrain(Long trainId) {
        log.debug("Entering getPreservationTrain - {}", trainId);
        return sip2PreservationRepository.findTrainById(trainId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Train not found: " + trainId));
    }

    @Override
    @Transactional
    public PreservationTrainDto updatePreservationTrain(Long trainId, PreservationTrainDto dto) {
        log.debug("Entering updatePreservationTrain - {}, {}", trainId, dto);
        return sip2PreservationRepository.updateTrain(trainId, dto);
    }

    @Override
    @Transactional
    public void deletePreservationTrain(Long trainId) {
        log.debug("Entering deletePreservationTrain - {}", trainId);
        sip2PreservationRepository.deleteTrain(trainId);
    }

    @Override
    @Transactional
    public PreservationTrainItemDto addItemToTrain(Long trainId, PreservationTrainItemDto dto) {
        log.debug("Entering addItemToTrain - {}, {}", trainId, dto);
        return sip2PreservationRepository.insertTrainItem(trainId, dto);
    }

    @Override
    @Transactional
    public List<PreservationTrainItemDto> addItemsToTrain(Long trainId, List<PreservationTrainItemDto> dtos) {
        log.debug("Entering addItemsToTrain - {}, {}", trainId, dtos);
        List<PreservationTrainItemDto> result = new ArrayList<>();
        for (PreservationTrainItemDto dto : dtos) {
            result.add(sip2PreservationRepository.insertTrainItem(trainId, dto));
        }
        return result;
    }

    @Override
    public PreservationTrainItemDto getItemFromTrain(Long trainId, Long trainItemId) {
        log.debug("Entering getItemFromTrain - {}, {}", trainId, trainItemId);
        return sip2PreservationRepository.findTrainItemById(trainItemId)
            .orElseThrow(() -> new java.util.NoSuchElementException("Train item not found: " + trainItemId));
    }

    @Override
    @Transactional
    public PreservationTrainItemDto updateItemFromTrain(Long trainId, Long trainItemId, PreservationTrainItemDto dto) {
        log.debug("Entering updateItemFromTrain - {}, {}, {}", trainId, trainItemId, dto);
        return sip2PreservationRepository.updateTrainItem(trainId, trainItemId, dto);
    }

    @Override
    @Transactional
    public void removeItemFromTrain(Long trainId, Long trainItemId) {
        log.debug("Entering removeItemFromTrain - {}, {}", trainId, trainItemId);
        sip2PreservationRepository.deleteTrainItem(trainId, trainItemId);
    }

    @Override
    @Transactional
    public PreservationTrainItemDto copyItemToAnotherTrain(Long trainId, Long trainItemId, Long targetTrainId) {
        log.debug("Entering copyItemToAnotherTrain - {}, {}, {}", trainId, trainItemId, targetTrainId);
        PreservationTrainItemDto source = getItemFromTrain(trainId, trainItemId);
        PreservationTrainItemDto copy = new PreservationTrainItemDto();
        copy.setItemId(source.getItemId());
        copy.setProcessingId(source.getProcessingId());
        return sip2PreservationRepository.insertTrainItem(targetTrainId, copy);
    }

    // ── Preservation Waiting List ──────────────────────────────────────────────

    @Override
    public List<PreservationWaitingListItemDto> listWaitingListItems() {
        log.debug("Entering listWaitingListItems");
        return sip2PreservationRepository.findWaitingListItems();
    }

    @Override
    @Transactional
    public List<PreservationWaitingListItemDto> addItemsToWaitingList(List<PreservationWaitingListItemDto> dtos) {
        log.debug("Entering addItemsToWaitingList - {}", dtos);
        for (PreservationWaitingListItemDto dto : dtos) {
            sip2PreservationRepository.insertWaitingListItem(dto.getItemId());
        }
        return listWaitingListItems();
    }

    @Override
    @Transactional
    public void removeItemFromWaitingList(Long itemId) {
        log.debug("Entering removeItemFromWaitingList - {}", itemId);
        sip2PreservationRepository.deleteWaitingListItem(itemId);
    }
}

