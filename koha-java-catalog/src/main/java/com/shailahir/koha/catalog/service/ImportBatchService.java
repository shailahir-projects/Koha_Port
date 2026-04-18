package com.shailahir.koha.catalog.service;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ImportBatchService {
    Page<ImportBatchDto> listImportBatches(String query, Pageable pageable);
    ImportBatchDto getImportBatch(Long id);
}
