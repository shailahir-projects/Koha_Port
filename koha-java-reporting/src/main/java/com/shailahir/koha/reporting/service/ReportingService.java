package com.shailahir.koha.reporting.service;

import com.shailahir.koha.reporting.dto.ReportResultDto;
import com.shailahir.koha.reporting.dto.SavedReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReportingService {
    Page<SavedReportDto> listReports(String query, Pageable pageable);
    SavedReportDto addReport(SavedReportDto report);
    SavedReportDto getReport(Long reportId);
    SavedReportDto updateReport(Long reportId, SavedReportDto report);
    void deleteReport(Long reportId);
    ReportResultDto runReport(Long reportId, Map<String, String> params);
}

