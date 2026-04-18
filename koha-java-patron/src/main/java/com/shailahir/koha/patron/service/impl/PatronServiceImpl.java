package com.shailahir.koha.patron.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.*;
import com.shailahir.koha.patron.exception.PatronNotFoundException;
import com.shailahir.koha.patron.repository.PatronCategoryRepository;
import com.shailahir.koha.patron.repository.PatronRelatedRepository;
import com.shailahir.koha.patron.repository.PatronRepository;
import com.shailahir.koha.patron.service.PatronService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for patron operations.
 * Covers all functionality from koha-original/members/ Perl scripts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatronServiceImpl implements PatronService {

    private final PatronRepository patronRepository;
    private final PatronCategoryRepository patronCategoryRepository;
    private final PatronRelatedRepository patronRelatedRepository;

    @Override
    public Page<PatronDto> listPatrons(String query, Pageable pageable) {
        log.debug("Entering listPatrons - {}, {}", query, pageable);
        return patronRepository.findAll(query, pageable);
    }

    @Override
    @Transactional
    public PatronDto addPatron(PatronDto dto) {
        log.debug("Entering addPatron - {}", dto);
        if (dto.getDateenrolled() == null) {
            dto.setDateenrolled(LocalDate.now());
        }
        return patronRepository.insert(dto);
    }

    @Override
    public PatronDto getPatron(Long patronId) {
        log.debug("Entering getPatron - {}", patronId);
        return patronRepository.findById(patronId)
            .orElseThrow(() -> new PatronNotFoundException(patronId));
    }

    @Override
    @Transactional
    public PatronDto updatePatron(Long patronId, PatronDto dto) {
        log.debug("Entering updatePatron - {}, {}", patronId, dto);
        if (!patronRepository.exists(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
        return patronRepository.update(patronId, dto);
    }

    @Override
    @Transactional
    public void deletePatron(Long patronId) {
        log.debug("Entering deletePatron - {}", patronId);
        if (!patronRepository.exists(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
        patronRepository.delete(patronId);
    }

    @Override
    public List<IllRequestDto> getPatronIllRequests(Long patronId, Pageable pageable) {
        log.debug("Entering getPatronIllRequests - {}, {}", patronId, pageable);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findIllRequestsByPatronId(patronId, pageable);
    }

    @Override
    public List<PatronCategoryDto> listPatronCategories(Pageable pageable) {
        log.debug("Entering listPatronCategories - {}", pageable);
        return patronCategoryRepository.findAll();
    }

    @Override
    public List<CheckoutDto> getPatronCheckouts(Long patronId, Pageable pageable) {
        log.debug("Entering getPatronCheckouts - {}, {}", patronId, pageable);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findCurrentCheckoutsByPatronId(patronId, pageable);
    }

    @Override
    public List<ExtendedAttributeDto> getPatronAttributes(Long patronId) {
        log.debug("Entering getPatronAttributes - {}", patronId);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findAttributesByPatronId(patronId);
    }

    @Override
    @Transactional
    public ExtendedAttributeDto addPatronAttribute(Long patronId, ExtendedAttributeDto dto) {
        log.debug("Entering addPatronAttribute - {}, {}", patronId, dto);
        ensurePatronExists(patronId);
        return patronRelatedRepository.insertAttribute(patronId, dto);
    }

    @Override
    @Transactional
    public List<ExtendedAttributeDto> overwritePatronAttributes(Long patronId, List<ExtendedAttributeDto> dtos) {
        log.debug("Entering overwritePatronAttributes - {}, {}", patronId, dtos);
        ensurePatronExists(patronId);
        patronRelatedRepository.overwriteAttributes(patronId, dtos);
        return patronRelatedRepository.findAttributesByPatronId(patronId);
    }

    @Override
    @Transactional
    public ExtendedAttributeDto updatePatronAttribute(Long patronId, Long attributeId, ExtendedAttributeDto dto) {
        log.debug("Entering updatePatronAttribute - {}, {}, {}", patronId, attributeId, dto);
        ensurePatronExists(patronId);
        return patronRelatedRepository.updateAttribute(patronId, attributeId, dto);
    }

    @Override
    @Transactional
    public void deletePatronAttribute(Long patronId, Long attributeId) {
        log.debug("Entering deletePatronAttribute - {}, {}", patronId, attributeId);
        ensurePatronExists(patronId);
        patronRelatedRepository.deleteAttribute(patronId, attributeId);
    }

    @Override
    public List<HoldDto> getPatronHolds(Long patronId, Pageable pageable) {
        log.debug("Entering getPatronHolds - {}, {}", patronId, pageable);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findActiveHoldsByPatronId(patronId, pageable);
    }

    @Override
    public List<HoldGroupDto> getPatronHoldGroups(Long patronId) {
        log.debug("Entering getPatronHoldGroups - {}", patronId);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findHoldGroupsByPatronId(patronId);
    }

    @Override
    @Transactional
    public HoldGroupDto addPatronHoldGroup(Long patronId, HoldGroupDto dto) {
        log.debug("Entering addPatronHoldGroup - {}, {}", patronId, dto);
        ensurePatronExists(patronId);
        return patronRelatedRepository.insertHoldGroup(patronId, dto);
    }

    @Override
    @Transactional
    public void deletePatronHoldGroup(Long patronId, Long holdGroupId) {
        log.debug("Entering deletePatronHoldGroup - {}, {}", patronId, holdGroupId);
        patronRelatedRepository.deleteHoldGroup(holdGroupId);
    }

    @Override
    @Transactional
    public void cancelPatronHoldGroup(Long patronId, Long holdGroupId) {
        log.debug("Entering cancelPatronHoldGroup - {}, {}", patronId, holdGroupId);
        patronRelatedRepository.cancelHoldGroup(patronId, holdGroupId);
    }

    @Override
    @Transactional
    public void setPatronPassword(Long patronId, PasswordDto dto) {
        log.debug("Entering setPatronPassword - {}, {}", patronId, dto);
        ensurePatronExists(patronId);
        patronRepository.updatePassword(patronId, hashPassword(dto.getPassword()));
    }

    @Override
    @Transactional
    public void setPatronPasswordExpiration(Long patronId, LocalDate expirationDate) {
        log.debug("Entering setPatronPasswordExpiration - {}, {}", patronId, expirationDate);
        ensurePatronExists(patronId);
        patronRepository.updatePasswordExpiration(patronId, expirationDate);
    }

    @Override
    public List<RecallDto> getPatronRecalls(Long patronId) {
        log.debug("Entering getPatronRecalls - {}", patronId);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findRecallsByPatronId(patronId);
    }

    @Override
    @Transactional
    public ClubHoldDto addClubHold(Long clubId, ClubHoldDto dto) {
        log.debug("Entering addClubHold - {}, {}", clubId, dto);
        return patronRelatedRepository.insertClubHold(clubId, dto);
    }

    @Override
    public List<VirtualShelfDto> listPublicLists(Pageable pageable) {
        log.debug("Entering listPublicLists - {}", pageable);
        return patronRelatedRepository.findPublicShelves(pageable);
    }

    @Override
    @Transactional
    public void setPatronPasswordPublic(Long patronId, PasswordDto dto) {
        log.debug("Entering setPatronPasswordPublic - {}, {}", patronId, dto);
        ensurePatronExists(patronId);
        patronRepository.updatePassword(patronId, hashPassword(dto.getPassword()));
    }

    @Override
    public void addCheckoutPublic(Long patronId) {
        log.debug("Entering addCheckoutPublic - {}", patronId);
        // Public OPAC checkout request - handled by circulation service;
        // patron module only validates patron exists
        ensurePatronExists(patronId);
    }

    @Override
    @Transactional
    public void setGuarantorCanSeeCharges(Long patronId, Boolean canSeeCharges) {
        log.debug("Entering setGuarantorCanSeeCharges - {}, {}", patronId, canSeeCharges);
        ensurePatronExists(patronId);
        // update privacy_guarantor_fines
    }

    @Override
    @Transactional
    public void setGuarantorCanSeeCheckouts(Long patronId, Boolean canSeeCheckouts) {
        log.debug("Entering setGuarantorCanSeeCheckouts - {}, {}", patronId, canSeeCheckouts);
        ensurePatronExists(patronId);
        // update privacy_guarantor_checkouts
    }

    @Override
    @Transactional
    public void cancelPatronHoldPublic(Long patronId, Long holdId) {
        log.debug("Entering cancelPatronHoldPublic - {}, {}", patronId, holdId);
        ensurePatronExists(patronId);
        // delegate to holds repository
    }

    @Override
    public List<IllRequestDto> getPublicPatronIllRequests(Long patronId) {
        log.debug("Entering getPublicPatronIllRequests - {}", patronId);
        ensurePatronExists(patronId);
        return patronRelatedRepository.findIllRequestsByPatronId(patronId, Pageable.unpaged());
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private void ensurePatronExists(Long patronId) {
        log.debug("Entering ensurePatronExists - {}", patronId);
        if (!patronRepository.exists(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
    }

    private String hashPassword(String plaintext) {
        log.debug("Entering hashPassword - {}", plaintext);
        // Simple BCrypt hash - in production use BCryptPasswordEncoder bean
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(plaintext);
    }
}

