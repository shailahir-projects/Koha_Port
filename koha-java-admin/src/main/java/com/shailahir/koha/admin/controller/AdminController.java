package com.shailahir.koha.admin.controller;

import com.shailahir.koha.admin.dto.*;
import com.shailahir.koha.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── /libraries ──
    @GetMapping("/libraries", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<LibraryDto>> listLibraries(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(adminService.listLibraries(query, pageable));
    }
    @PostMapping("/libraries", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LibraryDto> addLibrary(@RequestBody LibraryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addLibrary(dto));
    }
    @GetMapping("/libraries/{library_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LibraryDto> getLibrary(@PathVariable("library_id") String libraryId) {
        return ResponseEntity.ok(adminService.getLibrary(libraryId));
    }
    @PutMapping("/libraries/{library_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LibraryDto> updateLibrary(@PathVariable("library_id") String libraryId, @RequestBody LibraryDto dto) {
        return ResponseEntity.ok(adminService.updateLibrary(libraryId, dto));
    }
    @DeleteMapping("/libraries/{library_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteLibrary(@PathVariable("library_id") String libraryId) {
        adminService.deleteLibrary(libraryId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/libraries/{library_id}/desks", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<DeskDto>> listLibraryDesks(@PathVariable("library_id") String libraryId) {
        return ResponseEntity.ok(adminService.listLibraryDesks(libraryId));
    }
    @GetMapping("/libraries/{library_id}/cash_registers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<CashRegisterDto>> listLibraryCashRegisters(@PathVariable("library_id") String libraryId) {
        return ResponseEntity.ok(adminService.listLibraryCashRegisters(libraryId));
    }
    @GetMapping("/public/libraries", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<LibraryDto>> listLibrariesPublic(Pageable pageable) {
        return ResponseEntity.ok(adminService.listLibrariesPublic(pageable));
    }
    @GetMapping("/public/libraries/{library_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LibraryDto> getLibraryPublic(@PathVariable("library_id") String libraryId) {
        return ResponseEntity.ok(adminService.getLibraryPublic(libraryId));
    }

    // ── /cities ──
    @GetMapping("/cities", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<CityDto>> listCities(@RequestParam(value = "q", required = false) String query, Pageable pageable) {
        return ResponseEntity.ok(adminService.listCities(query, pageable));
    }
    @PostMapping("/cities", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CityDto> addCity(@RequestBody CityDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addCity(dto));
    }
    @GetMapping("/cities/{city_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CityDto> getCity(@PathVariable("city_id") Long cityId) {
        return ResponseEntity.ok(adminService.getCity(cityId));
    }
    @PutMapping("/cities/{city_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CityDto> updateCity(@PathVariable("city_id") Long cityId, @RequestBody CityDto dto) {
        return ResponseEntity.ok(adminService.updateCity(cityId, dto));
    }
    @DeleteMapping("/cities/{city_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteCity(@PathVariable("city_id") Long cityId) {
        adminService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }

    // ── /authorised_value_categories/{name}/authorised_values ──
    @GetMapping("/authorised_value_categories/{authorised_value_category_name}/authorised_values", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<AuthorisedValueDto>> listAuthorisedValues(@PathVariable("authorised_value_category_name") String categoryName, Pageable pageable) {
        return ResponseEntity.ok(adminService.listAuthorisedValues(categoryName, pageable));
    }

    // ── /authorised_value_categories ──
    @GetMapping("/authorised_value_categories", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<AuthorisedValueCategoryDto>> listAuthorisedValueCategories() {
        return ResponseEntity.ok(adminService.listAuthorisedValueCategories());
    }

    // ── /advanced_editor/macros ──
    @GetMapping("/advanced_editor/macros", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<AdvancedEditorMacroDto>> listMacros(Pageable pageable) {
        return ResponseEntity.ok(adminService.listMacros(pageable));
    }
    @PostMapping("/advanced_editor/macros", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdvancedEditorMacroDto> addMacro(@RequestBody AdvancedEditorMacroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addMacro(dto));
    }
    @PostMapping("/advanced_editor/macros/shared", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdvancedEditorMacroDto> addSharedMacro(@RequestBody AdvancedEditorMacroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addSharedMacro(dto));
    }
    @GetMapping("/advanced_editor/macros/{advancededitormacro_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdvancedEditorMacroDto> getMacro(@PathVariable("advancededitormacro_id") Long macroId) {
        return ResponseEntity.ok(adminService.getMacro(macroId));
    }
    @PutMapping("/advanced_editor/macros/{advancededitormacro_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdvancedEditorMacroDto> updateMacro(@PathVariable("advancededitormacro_id") Long macroId, @RequestBody AdvancedEditorMacroDto dto) {
        return ResponseEntity.ok(adminService.updateMacro(macroId, dto));
    }
    @DeleteMapping("/advanced_editor/macros/{advancededitormacro_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteMacro(@PathVariable("advancededitormacro_id") Long macroId) {
        adminService.deleteMacro(macroId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/advanced_editor/macros/shared/{advancededitormacro_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdvancedEditorMacroDto> getSharedMacro(@PathVariable("advancededitormacro_id") Long macroId) {
        return ResponseEntity.ok(adminService.getSharedMacro(macroId));
    }
    @PutMapping("/advanced_editor/macros/shared/{advancededitormacro_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdvancedEditorMacroDto> updateSharedMacro(@PathVariable("advancededitormacro_id") Long macroId, @RequestBody AdvancedEditorMacroDto dto) {
        return ResponseEntity.ok(adminService.updateSharedMacro(macroId, dto));
    }
    @DeleteMapping("/advanced_editor/macros/shared/{advancededitormacro_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSharedMacro(@PathVariable("advancededitormacro_id") Long macroId) {
        adminService.deleteSharedMacro(macroId);
        return ResponseEntity.noContent().build();
    }

    // ── /extended_attribute_types ──
    @GetMapping("/extended_attribute_types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ExtendedAttributeTypeDto>> listExtendedAttributeTypes() {
        return ResponseEntity.ok(adminService.listExtendedAttributeTypes());
    }

    // ── /transfer_limits ──
    @GetMapping("/transfer_limits", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<TransferLimitDto>> listTransferLimits() {
        return ResponseEntity.ok(adminService.listTransferLimits());
    }
    @PostMapping("/transfer_limits", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TransferLimitDto> addTransferLimit(@RequestBody TransferLimitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addTransferLimit(dto));
    }
    @DeleteMapping("/transfer_limits/{limit_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteTransferLimit(@PathVariable("limit_id") Long limitId) {
        adminService.deleteTransferLimit(limitId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/transfer_limits/batch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<TransferLimitDto>> batchAddTransferLimits(@RequestBody List<TransferLimitDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.batchAddTransferLimits(dtos));
    }
    @DeleteMapping("/transfer_limits/batch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> batchDeleteTransferLimits(@RequestBody List<Long> limitIds) {
        adminService.batchDeleteTransferLimits(limitIds);
        return ResponseEntity.noContent().build();
    }

    // ── /tickets ──
    @GetMapping("/tickets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<TicketDto>> listTickets(Pageable pageable) {
        return ResponseEntity.ok(adminService.listTickets(pageable));
    }
    @PostMapping("/tickets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TicketDto> addTicket(@RequestBody TicketDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addTicket(dto));
    }
    @GetMapping("/tickets/{ticket_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TicketDto> getTicket(@PathVariable("ticket_id") Long ticketId) {
        return ResponseEntity.ok(adminService.getTicket(ticketId));
    }
    @PutMapping("/tickets/{ticket_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TicketDto> updateTicket(@PathVariable("ticket_id") Long ticketId, @RequestBody TicketDto dto) {
        return ResponseEntity.ok(adminService.updateTicket(ticketId, dto));
    }
    @DeleteMapping("/tickets/{ticket_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteTicket(@PathVariable("ticket_id") Long ticketId) {
        adminService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/tickets/{ticket_id}/updates", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<TicketUpdateDto>> listTicketUpdates(@PathVariable("ticket_id") Long ticketId) {
        return ResponseEntity.ok(adminService.listTicketUpdates(ticketId));
    }
    @PostMapping("/tickets/{ticket_id}/updates", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TicketUpdateDto> addTicketUpdate(@PathVariable("ticket_id") Long ticketId, @RequestBody TicketUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addTicketUpdate(ticketId, dto));
    }
    @PostMapping("/public/tickets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TicketDto> addTicketPublic(@RequestBody TicketDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addTicketPublic(dto));
    }

    // ── /sip2/accounts ──
    @GetMapping("/sip2/accounts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Sip2AccountDto>> listSip2Accounts(Pageable pageable) {
        return ResponseEntity.ok(adminService.listSip2Accounts(pageable));
    }
    @PostMapping("/sip2/accounts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2AccountDto> addSip2Account(@RequestBody Sip2AccountDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addSip2Account(dto));
    }
    @GetMapping("/sip2/accounts/{sip_account_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2AccountDto> getSip2Account(@PathVariable("sip_account_id") Long sipAccountId) {
        return ResponseEntity.ok(adminService.getSip2Account(sipAccountId));
    }
    @PutMapping("/sip2/accounts/{sip_account_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2AccountDto> updateSip2Account(@PathVariable("sip_account_id") Long sipAccountId, @RequestBody Sip2AccountDto dto) {
        return ResponseEntity.ok(adminService.updateSip2Account(sipAccountId, dto));
    }
    @DeleteMapping("/sip2/accounts/{sip_account_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSip2Account(@PathVariable("sip_account_id") Long sipAccountId) {
        adminService.deleteSip2Account(sipAccountId);
        return ResponseEntity.noContent().build();
    }

    // ── /sip2/institutions ──
    @GetMapping("/sip2/institutions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Sip2InstitutionDto>> listSip2Institutions(Pageable pageable) {
        return ResponseEntity.ok(adminService.listSip2Institutions(pageable));
    }
    @PostMapping("/sip2/institutions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2InstitutionDto> addSip2Institution(@RequestBody Sip2InstitutionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addSip2Institution(dto));
    }
    @GetMapping("/sip2/institutions/{sip_institution_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2InstitutionDto> getSip2Institution(@PathVariable("sip_institution_id") Long sipInstitutionId) {
        return ResponseEntity.ok(adminService.getSip2Institution(sipInstitutionId));
    }
    @PutMapping("/sip2/institutions/{sip_institution_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2InstitutionDto> updateSip2Institution(@PathVariable("sip_institution_id") Long sipInstitutionId, @RequestBody Sip2InstitutionDto dto) {
        return ResponseEntity.ok(adminService.updateSip2Institution(sipInstitutionId, dto));
    }
    @DeleteMapping("/sip2/institutions/{sip_institution_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSip2Institution(@PathVariable("sip_institution_id") Long sipInstitutionId) {
        adminService.deleteSip2Institution(sipInstitutionId);
        return ResponseEntity.noContent().build();
    }

    // ── /sip2/system_preference_overrides ──
    @GetMapping("/sip2/system_preference_overrides", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Sip2SystemPreferenceOverrideDto>> listSip2SystemPreferenceOverrides(Pageable pageable) {
        return ResponseEntity.ok(adminService.listSip2SystemPreferenceOverrides(pageable));
    }
    @PostMapping("/sip2/system_preference_overrides", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2SystemPreferenceOverrideDto> addSip2SystemPreferenceOverride(@RequestBody Sip2SystemPreferenceOverrideDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addSip2SystemPreferenceOverride(dto));
    }
    @GetMapping("/sip2/system_preference_overrides/{sip_system_preference_override_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2SystemPreferenceOverrideDto> getSip2SystemPreferenceOverride(@PathVariable("sip_system_preference_override_id") Long overrideId) {
        return ResponseEntity.ok(adminService.getSip2SystemPreferenceOverride(overrideId));
    }
    @PutMapping("/sip2/system_preference_overrides/{sip_system_preference_override_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Sip2SystemPreferenceOverrideDto> updateSip2SystemPreferenceOverride(@PathVariable("sip_system_preference_override_id") Long overrideId, @RequestBody Sip2SystemPreferenceOverrideDto dto) {
        return ResponseEntity.ok(adminService.updateSip2SystemPreferenceOverride(overrideId, dto));
    }
    @DeleteMapping("/sip2/system_preference_overrides/{sip_system_preference_override_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSip2SystemPreferenceOverride(@PathVariable("sip_system_preference_override_id") Long overrideId) {
        adminService.deleteSip2SystemPreferenceOverride(overrideId);
        return ResponseEntity.noContent().build();
    }

    // ── /preservation/config ──
    @GetMapping("/preservation/config", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationConfigDto> getPreservationConfig() {
        return ResponseEntity.ok(adminService.getPreservationConfig());
    }

    // ── /preservation/processings ──
    @GetMapping("/preservation/processings", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PreservationProcessingDto>> listPreservationProcessings() {
        return ResponseEntity.ok(adminService.listPreservationProcessings());
    }
    @PostMapping("/preservation/processings", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationProcessingDto> addPreservationProcessing(@RequestBody PreservationProcessingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addPreservationProcessing(dto));
    }
    @GetMapping("/preservation/processings/{processing_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationProcessingDto> getPreservationProcessing(@PathVariable("processing_id") Long processingId) {
        return ResponseEntity.ok(adminService.getPreservationProcessing(processingId));
    }
    @PutMapping("/preservation/processings/{processing_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationProcessingDto> updatePreservationProcessing(@PathVariable("processing_id") Long processingId, @RequestBody PreservationProcessingDto dto) {
        return ResponseEntity.ok(adminService.updatePreservationProcessing(processingId, dto));
    }
    @DeleteMapping("/preservation/processings/{processing_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePreservationProcessing(@PathVariable("processing_id") Long processingId) {
        adminService.deletePreservationProcessing(processingId);
        return ResponseEntity.noContent().build();
    }

    // ── /preservation/trains ──
    @GetMapping("/preservation/trains", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PreservationTrainDto>> listPreservationTrains() {
        return ResponseEntity.ok(adminService.listPreservationTrains());
    }
    @PostMapping("/preservation/trains", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainDto> addPreservationTrain(@RequestBody PreservationTrainDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addPreservationTrain(dto));
    }
    @GetMapping("/preservation/trains/{train_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainDto> getPreservationTrain(@PathVariable("train_id") Long trainId) {
        return ResponseEntity.ok(adminService.getPreservationTrain(trainId));
    }
    @PutMapping("/preservation/trains/{train_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainDto> updatePreservationTrain(@PathVariable("train_id") Long trainId, @RequestBody PreservationTrainDto dto) {
        return ResponseEntity.ok(adminService.updatePreservationTrain(trainId, dto));
    }
    @DeleteMapping("/preservation/trains/{train_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePreservationTrain(@PathVariable("train_id") Long trainId) {
        adminService.deletePreservationTrain(trainId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/preservation/trains/{train_id}/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainItemDto> addItemToTrain(@PathVariable("train_id") Long trainId, @RequestBody PreservationTrainItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addItemToTrain(trainId, dto));
    }
    @PostMapping("/preservation/trains/{train_id}/items/batch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PreservationTrainItemDto>> addItemsToTrain(@PathVariable("train_id") Long trainId, @RequestBody List<PreservationTrainItemDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addItemsToTrain(trainId, dtos));
    }
    @GetMapping("/preservation/trains/{train_id}/items/{train_item_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainItemDto> getItemFromTrain(@PathVariable("train_id") Long trainId, @PathVariable("train_item_id") Long trainItemId) {
        return ResponseEntity.ok(adminService.getItemFromTrain(trainId, trainItemId));
    }
    @PutMapping("/preservation/trains/{train_id}/items/{train_item_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainItemDto> updateItemFromTrain(@PathVariable("train_id") Long trainId, @PathVariable("train_item_id") Long trainItemId, @RequestBody PreservationTrainItemDto dto) {
        return ResponseEntity.ok(adminService.updateItemFromTrain(trainId, trainItemId, dto));
    }
    @DeleteMapping("/preservation/trains/{train_id}/items/{train_item_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> removeItemFromTrain(@PathVariable("train_id") Long trainId, @PathVariable("train_item_id") Long trainItemId) {
        adminService.removeItemFromTrain(trainId, trainItemId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/preservation/trains/{train_id}/items/{train_item_id}/copy", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PreservationTrainItemDto> copyItemToAnotherTrain(@PathVariable("train_id") Long trainId, @PathVariable("train_item_id") Long trainItemId, @RequestParam("target_train_id") Long targetTrainId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.copyItemToAnotherTrain(trainId, trainItemId, targetTrainId));
    }

    // ── /preservation/waiting-list/items ──
    @GetMapping("/preservation/waiting-list/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PreservationWaitingListItemDto>> listWaitingListItems() {
        return ResponseEntity.ok(adminService.listWaitingListItems());
    }
    @PostMapping("/preservation/waiting-list/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PreservationWaitingListItemDto>> addItemsToWaitingList(@RequestBody List<PreservationWaitingListItemDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addItemsToWaitingList(dtos));
    }
    @DeleteMapping("/preservation/waiting-list/items/{item_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> removeItemFromWaitingList(@PathVariable("item_id") Long itemId) {
        adminService.removeItemFromWaitingList(itemId);
        return ResponseEntity.noContent().build();
    }
}

