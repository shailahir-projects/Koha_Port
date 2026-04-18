package com.shailahir.koha.catalog.service.impl;

import com.shailahir.koha.catalog.dto.RecordSourceDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.RecordSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RecordSourceServiceImpl implements RecordSourceService {

    private final CatalogRepository repo;

    @Override
    public Page<RecordSourceDto> listRecordSources(String query, Pageable pageable) {
        return repo.findAllRecordSources(query, pageable);
    }

    @Override
    public RecordSourceDto addRecordSource(RecordSourceDto recordSource) {
        return repo.insertRecordSource(recordSource);
    }

    @Override
    public RecordSourceDto getRecordSource(Long recordSourceId) {
        return repo.findRecordSourceById(recordSourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record source not found"));
    }

    @Override
    public RecordSourceDto updateRecordSource(Long recordSourceId, RecordSourceDto recordSource) {
        repo.findRecordSourceById(recordSourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record source not found"));
        return repo.updateRecordSource(recordSourceId, recordSource);
    }

    @Override
    public void deleteRecordSource(Long recordSourceId) {
        repo.deleteRecordSource(recordSourceId);
    }
}

