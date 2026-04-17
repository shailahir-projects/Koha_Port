package com.shailahir.koha.admin.service;

import com.shailahir.koha.admin.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    // Libraries
    Page<LibraryDto> listLibraries(String query, Pageable pageable);
    LibraryDto addLibrary(LibraryDto dto);
    LibraryDto getLibrary(String libraryId);
    LibraryDto updateLibrary(String libraryId, LibraryDto dto);
    void deleteLibrary(String libraryId);
    List<DeskDto> listLibraryDesks(String libraryId);
    List<CashRegisterDto> listLibraryCashRegisters(String libraryId);
    List<LibraryDto> listLibrariesPublic(Pageable pageable);
    LibraryDto getLibraryPublic(String libraryId);
    // Cities
    Page<CityDto> listCities(String query, Pageable pageable);
    CityDto addCity(CityDto dto);
    CityDto getCity(Long cityId);
    CityDto updateCity(Long cityId, CityDto dto);
    void deleteCity(Long cityId);
    // Authorised values
    List<AuthorisedValueDto> listAuthorisedValues(String categoryName, Pageable pageable);
    List<AuthorisedValueCategoryDto> listAuthorisedValueCategories();
    // Advanced editor macros
    Page<AdvancedEditorMacroDto> listMacros(Pageable pageable);
    AdvancedEditorMacroDto addMacro(AdvancedEditorMacroDto dto);
    AdvancedEditorMacroDto addSharedMacro(AdvancedEditorMacroDto dto);
    AdvancedEditorMacroDto getMacro(Long macroId);
    AdvancedEditorMacroDto updateMacro(Long macroId, AdvancedEditorMacroDto dto);
    void deleteMacro(Long macroId);
    AdvancedEditorMacroDto getSharedMacro(Long macroId);
    AdvancedEditorMacroDto updateSharedMacro(Long macroId, AdvancedEditorMacroDto dto);
    void deleteSharedMacro(Long macroId);
    // Extended attribute types
    List<ExtendedAttributeTypeDto> listExtendedAttributeTypes();
    // Transfer limits
    List<TransferLimitDto> listTransferLimits();
    TransferLimitDto addTransferLimit(TransferLimitDto dto);
    void deleteTransferLimit(Long limitId);
    List<TransferLimitDto> batchAddTransferLimits(List<TransferLimitDto> dtos);
    void batchDeleteTransferLimits(List<Long> limitIds);
    // Tickets
    Page<TicketDto> listTickets(Pageable pageable);
    TicketDto addTicket(TicketDto dto);
    TicketDto getTicket(Long ticketId);
    TicketDto updateTicket(Long ticketId, TicketDto dto);
    void deleteTicket(Long ticketId);
    List<TicketUpdateDto> listTicketUpdates(Long ticketId);
    TicketUpdateDto addTicketUpdate(Long ticketId, TicketUpdateDto dto);
    TicketDto addTicketPublic(TicketDto dto);
    // SIP2 accounts
    Page<Sip2AccountDto> listSip2Accounts(Pageable pageable);
    Sip2AccountDto addSip2Account(Sip2AccountDto dto);
    Sip2AccountDto getSip2Account(Long sipAccountId);
    Sip2AccountDto updateSip2Account(Long sipAccountId, Sip2AccountDto dto);
    void deleteSip2Account(Long sipAccountId);
    // SIP2 institutions
    Page<Sip2InstitutionDto> listSip2Institutions(Pageable pageable);
    Sip2InstitutionDto addSip2Institution(Sip2InstitutionDto dto);
    Sip2InstitutionDto getSip2Institution(Long sipInstitutionId);
    Sip2InstitutionDto updateSip2Institution(Long sipInstitutionId, Sip2InstitutionDto dto);
    void deleteSip2Institution(Long sipInstitutionId);
    // SIP2 system preference overrides
    Page<Sip2SystemPreferenceOverrideDto> listSip2SystemPreferenceOverrides(Pageable pageable);
    Sip2SystemPreferenceOverrideDto addSip2SystemPreferenceOverride(Sip2SystemPreferenceOverrideDto dto);
    Sip2SystemPreferenceOverrideDto getSip2SystemPreferenceOverride(Long overrideId);
    Sip2SystemPreferenceOverrideDto updateSip2SystemPreferenceOverride(Long overrideId, Sip2SystemPreferenceOverrideDto dto);
    void deleteSip2SystemPreferenceOverride(Long overrideId);
    // Preservation config
    PreservationConfigDto getPreservationConfig();
    // Preservation processings
    List<PreservationProcessingDto> listPreservationProcessings();
    PreservationProcessingDto addPreservationProcessing(PreservationProcessingDto dto);
    PreservationProcessingDto getPreservationProcessing(Long processingId);
    PreservationProcessingDto updatePreservationProcessing(Long processingId, PreservationProcessingDto dto);
    void deletePreservationProcessing(Long processingId);
    // Preservation trains
    List<PreservationTrainDto> listPreservationTrains();
    PreservationTrainDto addPreservationTrain(PreservationTrainDto dto);
    PreservationTrainDto getPreservationTrain(Long trainId);
    PreservationTrainDto updatePreservationTrain(Long trainId, PreservationTrainDto dto);
    void deletePreservationTrain(Long trainId);
    PreservationTrainItemDto addItemToTrain(Long trainId, PreservationTrainItemDto dto);
    List<PreservationTrainItemDto> addItemsToTrain(Long trainId, List<PreservationTrainItemDto> dtos);
    PreservationTrainItemDto getItemFromTrain(Long trainId, Long trainItemId);
    PreservationTrainItemDto updateItemFromTrain(Long trainId, Long trainItemId, PreservationTrainItemDto dto);
    void removeItemFromTrain(Long trainId, Long trainItemId);
    PreservationTrainItemDto copyItemToAnotherTrain(Long trainId, Long trainItemId, Long targetTrainId);
    // Preservation waiting list
    List<PreservationWaitingListItemDto> listWaitingListItems();
    List<PreservationWaitingListItemDto> addItemsToWaitingList(List<PreservationWaitingListItemDto> dtos);
    void removeItemFromWaitingList(Long itemId);
}

