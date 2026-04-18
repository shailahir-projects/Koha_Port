package com.shailahir.koha.erm.service;

import com.shailahir.koha.erm.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ErmService {
    Page<AgreementDto> listAgreements(String query, Pageable pageable);
    AgreementDto addAgreement(AgreementDto dto);
    AgreementDto getAgreement(Long id);
    AgreementDto updateAgreement(Long id, AgreementDto dto);
    void deleteAgreement(Long id);

    Page<LicenseDto> listLicenses(String query, Pageable pageable);
    LicenseDto addLicense(LicenseDto dto);
    LicenseDto getLicense(Long id);
    LicenseDto updateLicense(Long id, LicenseDto dto);
    void deleteLicense(Long id);

    Page<ErmPackageDto> listPackages(String query, Pageable pageable);
    ErmPackageDto addPackage(ErmPackageDto dto);
    ErmPackageDto getPackage(Long id);
    ErmPackageDto updatePackage(Long id, ErmPackageDto dto);
    void deletePackage(Long id);
}

