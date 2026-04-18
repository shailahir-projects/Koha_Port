package com.shailahir.koha.reporting.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.reporting.dto.ReportResultDto;
import com.shailahir.koha.reporting.dto.SavedReportDto;
import com.shailahir.koha.reporting.repository.ReportingRepository;
import com.shailahir.koha.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final ReportingRepository repo;

    @Override
    public Page<SavedReportDto> listReports(String query, Pageable pageable) {
        log.debug("Entering listReports - {}, {}", query, pageable);
        return repo.findAllReports(query, pageable);
    }

    @Override
    public SavedReportDto addReport(SavedReportDto report) {
        log.debug("Entering addReport - {}", report);
        return repo.insertReport(report);
    }

    @Override
    public SavedReportDto getReport(Long reportId) {
        log.debug("Entering getReport - {}", reportId);
        return repo.findReportById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    @Override
    public SavedReportDto updateReport(Long reportId, SavedReportDto report) {
        log.debug("Entering updateReport - {}, {}", reportId, report);
        repo.findReportById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return repo.updateReport(reportId, report);
    }

    @Override
    public void deleteReport(Long reportId) {
        log.debug("Entering deleteReport - {}", reportId);
        repo.deleteReport(reportId);
    }

    @Override
    public ReportResultDto runReport(Long reportId, Map<String, String> params) {
        log.debug("Entering runReport - {}, {}", reportId, params);
        return repo.executeReport(reportId, params);
    }
}

