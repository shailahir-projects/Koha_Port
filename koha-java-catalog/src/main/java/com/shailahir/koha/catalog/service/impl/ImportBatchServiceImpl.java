package com.shailahir.koha.catalog.service.impl;
import lombok.extern.slf4j.Slf4j;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.ImportBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportBatchServiceImpl implements ImportBatchService {
    private final CatalogRepository repo;
    @Override
    public Page<ImportBatchDto> listImportBatches(String query, Pageable pageable) {
        log.debug("Entering listImportBatches - {}, {}", query, pageable);
        return repo.findAllImportBatches(query, pageable);
    }
    @Override
    public ImportBatchDto getImportBatch(Long id) {
        log.debug("Entering getImportBatch - {}", id);
        return repo.findImportBatchById(id).orElseThrow();
    }
}
