package com.shailahir.koha.erm.service;

import com.shailahir.koha.erm.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

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

    // Generic CRUD operations
    Page<Map<String, Object>> pageQuery(String table, String orderColumn, Pageable pageable);
    Page<Map<String, Object>> pageQueryByForeignKey(String table, String orderColumn, String foreignKeyColumn, Long foreignKeyValue, Pageable pageable);
    Map<String, Object> getRow(String table, String idColumn, Long id);
    long insertRow(String table, Map<String, Object> body, String idColumn);
    void updateRow(String table, String idColumn, Long id, Map<String, Object> body);
    void deleteRow(String table, String idColumn, Long id);
    void updatePackageSelected(Long id, Object isSelected);

    // Specific queries
    List<Map<String, Object>> listCounterRegistries();
    List<Map<String, Object>> listCustomReports();
    List<Map<String, Object>> listSushiServices();
    List<Map<String, Object>> listExtendedAttributeTypes();
}

