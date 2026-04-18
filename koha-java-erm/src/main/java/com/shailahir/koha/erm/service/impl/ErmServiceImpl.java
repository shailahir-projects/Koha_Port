package com.shailahir.koha.erm.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.erm.dto.*;
import com.shailahir.koha.erm.repository.ErmRepository;
import com.shailahir.koha.erm.service.ErmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErmServiceImpl implements ErmService {

    private final ErmRepository repo;

    @Override
    public Page<AgreementDto> listAgreements(String query, Pageable pageable) {
        log.debug("Entering listAgreements - {}, {}", query, pageable);
        return repo.findAllAgreements(query, pageable);
    }

    @Override
    public AgreementDto addAgreement(AgreementDto dto) {
        log.debug("Entering addAgreement - {}", dto);
        return repo.insertAgreement(dto);
    }

    @Override
    public AgreementDto getAgreement(Long id) {
        log.debug("Entering getAgreement - {}", id);
        return repo.findAgreementById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agreement not found"));
    }

    @Override
    public AgreementDto updateAgreement(Long id, AgreementDto dto) {
        log.debug("Entering updateAgreement - {}, {}", id, dto);
        repo.findAgreementById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agreement not found"));
        return repo.updateAgreement(id, dto);
    }

    @Override
    public void deleteAgreement(Long id) {
        log.debug("Entering deleteAgreement - {}", id);
        repo.deleteAgreement(id);
    }

    @Override
    public Page<LicenseDto> listLicenses(String query, Pageable pageable) {
        log.debug("Entering listLicenses - {}, {}", query, pageable);
        return repo.findAllLicenses(query, pageable);
    }

    @Override
    public LicenseDto addLicense(LicenseDto dto) {
        log.debug("Entering addLicense - {}", dto);
        return repo.insertLicense(dto);
    }

    @Override
    public LicenseDto getLicense(Long id) {
        log.debug("Entering getLicense - {}", id);
        return repo.findLicenseById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
    }

    @Override
    public LicenseDto updateLicense(Long id, LicenseDto dto) {
        log.debug("Entering updateLicense - {}, {}", id, dto);
        repo.findLicenseById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
        return repo.updateLicense(id, dto);
    }

    @Override
    public void deleteLicense(Long id) {
        log.debug("Entering deleteLicense - {}", id);
        repo.deleteLicense(id);
    }

    @Override
    public Page<ErmPackageDto> listPackages(String query, Pageable pageable) {
        log.debug("Entering listPackages - {}, {}", query, pageable);
        return repo.findAllPackages(query, pageable);
    }

    @Override
    public ErmPackageDto addPackage(ErmPackageDto dto) {
        log.debug("Entering addPackage - {}", dto);
        return repo.insertPackage(dto);
    }

    @Override
    public ErmPackageDto getPackage(Long id) {
        log.debug("Entering getPackage - {}", id);
        return repo.findPackageById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));
    }

    @Override
    public ErmPackageDto updatePackage(Long id, ErmPackageDto dto) {
        log.debug("Entering updatePackage - {}, {}", id, dto);
        repo.findPackageById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));
        return repo.updatePackage(id, dto);
    }

    @Override
    public void deletePackage(Long id) {
        log.debug("Entering deletePackage - {}", id);
        repo.deletePackage(id);
    }

    // ── Generic CRUD ──────────────────────────────────────────────────────────

    @Override
    public Page<Map<String, Object>> pageQuery(String table, String orderColumn, Pageable pageable) {
        log.debug("Entering pageQuery - {}, {}, {}", table, orderColumn, pageable);
        return repo.pageQuery(table, orderColumn, pageable);
    }

    @Override
    public Page<Map<String, Object>> pageQueryByForeignKey(String table, String orderColumn, String foreignKeyColumn, Long foreignKeyValue, Pageable pageable) {
        log.debug("Entering pageQueryByForeignKey - {}, {}, {}, {}, {}", table, orderColumn, foreignKeyColumn, foreignKeyValue, pageable);
        return repo.pageQueryByForeignKey(table, orderColumn, foreignKeyColumn, foreignKeyValue, pageable);
    }

    @Override
    public Map<String, Object> getRow(String table, String idColumn, Long id) {
        log.debug("Entering getRow - {}, {}, {}", table, idColumn, id);
        Map<String, Object> row = repo.getRow(table, idColumn, id);
        if (row == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return row;
    }

    @Override
    public long insertRow(String table, Map<String, Object> body, String idColumn) {
        log.debug("Entering insertRow - {}, {}, {}", table, body, idColumn);
        if (body == null || body.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be empty");
        }
        return repo.insertRow(table, body, idColumn);
    }

    @Override
    public void updateRow(String table, String idColumn, Long id, Map<String, Object> body) {
        log.debug("Entering updateRow - {}, {}, {}, {}", table, idColumn, id, body);
        repo.updateRow(table, idColumn, id, body);
    }

    @Override
    public void deleteRow(String table, String idColumn, Long id) {
        log.debug("Entering deleteRow - {}, {}, {}", table, idColumn, id);
        repo.deleteRow(table, idColumn, id);
    }

    @Override
    public void updatePackageSelected(Long id, Object isSelected) {
        log.debug("Entering updatePackageSelected - {}, {}", id, isSelected);
        repo.updatePackageSelected(id, isSelected);
    }

    @Override
    public List<Map<String, Object>> listCounterRegistries() {
        log.debug("Entering listCounterRegistries");
        return repo.findCounterRegistries();
    }

    @Override
    public List<Map<String, Object>> listCustomReports() {
        log.debug("Entering listCustomReports");
        return repo.findCustomReports();
    }

    @Override
    public List<Map<String, Object>> listSushiServices() {
        log.debug("Entering listSushiServices");
        return repo.findSushiServices();
    }

    @Override
    public List<Map<String, Object>> listExtendedAttributeTypes() {
        log.debug("Entering listExtendedAttributeTypes");
        return repo.findExtendedAttributeTypes();
    }
}

