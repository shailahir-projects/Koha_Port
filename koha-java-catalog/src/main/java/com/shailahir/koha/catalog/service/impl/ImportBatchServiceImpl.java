package com.shailahir.koha.catalog.service.impl;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.ImportBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ImportBatchServiceImpl implements ImportBatchService {
    private final CatalogRepository repo;
    @Override
    public Page<ImportBatchDto> listImportBatches(String query, Pageable pageable) {
        return repo.findAllImportBatches(query, pageable);
    }
    @Override
    public ImportBatchDto getImportBatch(Long id) {
        return repo.findImportBatchById(id).orElseThrow();
    }
}
