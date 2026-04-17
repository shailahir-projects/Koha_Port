package com.shailahir.koha.patron.service;

import com.shailahir.koha.patron.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PatronService {
    Page<PatronDto> listPatrons(String query, Pageable pageable);
    PatronDto addPatron(PatronDto dto);
    PatronDto getPatron(Long patronId);
    PatronDto updatePatron(Long patronId, PatronDto dto);
    void deletePatron(Long patronId);
    List<IllRequestDto> getPatronIllRequests(Long patronId, Pageable pageable);
    List<PatronCategoryDto> listPatronCategories(Pageable pageable);
    List<CheckoutDto> getPatronCheckouts(Long patronId, Pageable pageable);
    List<ExtendedAttributeDto> getPatronAttributes(Long patronId);
    ExtendedAttributeDto addPatronAttribute(Long patronId, ExtendedAttributeDto dto);
    List<ExtendedAttributeDto> overwritePatronAttributes(Long patronId, List<ExtendedAttributeDto> dtos);
    ExtendedAttributeDto updatePatronAttribute(Long patronId, Long attributeId, ExtendedAttributeDto dto);
    void deletePatronAttribute(Long patronId, Long attributeId);
    List<HoldDto> getPatronHolds(Long patronId, Pageable pageable);
    List<HoldGroupDto> getPatronHoldGroups(Long patronId);
    HoldGroupDto addPatronHoldGroup(Long patronId, HoldGroupDto dto);
    void deletePatronHoldGroup(Long patronId, Long holdGroupId);
    void cancelPatronHoldGroup(Long patronId, Long holdGroupId);
    void setPatronPassword(Long patronId, PasswordDto dto);
    void setPatronPasswordExpiration(Long patronId, LocalDate expirationDate);
    List<RecallDto> getPatronRecalls(Long patronId);
    ClubHoldDto addClubHold(Long clubId, ClubHoldDto dto);
    List<VirtualShelfDto> listPublicLists(Pageable pageable);
    // public patron endpoints
    void setPatronPasswordPublic(Long patronId, PasswordDto dto);
    void addCheckoutPublic(Long patronId);
    void setGuarantorCanSeeCharges(Long patronId, Boolean canSeeCharges);
    void setGuarantorCanSeeCheckouts(Long patronId, Boolean canSeeCheckouts);
    void cancelPatronHoldPublic(Long patronId, Long holdId);
    List<IllRequestDto> getPublicPatronIllRequests(Long patronId);
}

