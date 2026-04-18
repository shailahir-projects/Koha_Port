package com.shailahir.koha.erm.service.impl;

import com.shailahir.koha.erm.dto.*;
import com.shailahir.koha.erm.repository.ErmRepository;
import com.shailahir.koha.erm.service.ErmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ErmServiceImpl implements ErmService {

    private final ErmRepository repo;

    @Override
    public Page<AgreementDto> listAgreements(String query, Pageable pageable) {
        return repo.findAllAgreements(query, pageable);
    }

    @Override
    public AgreementDto addAgreement(AgreementDto dto) {
        return repo.insertAgreement(dto);
    }

    @Override
    public AgreementDto getAgreement(Long id) {
        return repo.findAgreementById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agreement not found"));
    }

    @Override
    public AgreementDto updateAgreement(Long id, AgreementDto dto) {
        repo.findAgreementById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agreement not found"));
        return repo.updateAgreement(id, dto);
    }

    @Override
    public void deleteAgreement(Long id) {
        repo.deleteAgreement(id);
    }

    @Override
    public Page<LicenseDto> listLicenses(String query, Pageable pageable) {
        return repo.findAllLicenses(query, pageable);
    }

    @Override
    public LicenseDto addLicense(LicenseDto dto) {
        return repo.insertLicense(dto);
    }

    @Override
    public LicenseDto getLicense(Long id) {
        return repo.findLicenseById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
    }

    @Override
    public LicenseDto updateLicense(Long id, LicenseDto dto) {
        repo.findLicenseById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
        return repo.updateLicense(id, dto);
    }

    @Override
    public void deleteLicense(Long id) {
        repo.deleteLicense(id);
    }

    @Override
    public Page<ErmPackageDto> listPackages(String query, Pageable pageable) {
        return repo.findAllPackages(query, pageable);
    }

    @Override
    public ErmPackageDto addPackage(ErmPackageDto dto) {
        return repo.insertPackage(dto);
    }

    @Override
    public ErmPackageDto getPackage(Long id) {
        return repo.findPackageById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));
    }

    @Override
    public ErmPackageDto updatePackage(Long id, ErmPackageDto dto) {
        repo.findPackageById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));
        return repo.updatePackage(id, dto);
    }

    @Override
    public void deletePackage(Long id) {
        repo.deletePackage(id);
    }
}

