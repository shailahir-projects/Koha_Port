package com.shailahir.koha.catalog.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.catalog.dto.RecordSourceDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.RecordSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordSourceServiceImpl implements RecordSourceService {

    private final CatalogRepository repo;

    @Override
    public Page<RecordSourceDto> listRecordSources(String query, Pageable pageable) {
        log.debug("Entering listRecordSources - {}, {}", query, pageable);
        return repo.findAllRecordSources(query, pageable);
    }

    @Override
    public RecordSourceDto addRecordSource(RecordSourceDto recordSource) {
        log.debug("Entering addRecordSource - {}", recordSource);
        return repo.insertRecordSource(recordSource);
    }

    @Override
    public RecordSourceDto getRecordSource(Long recordSourceId) {
        log.debug("Entering getRecordSource - {}", recordSourceId);
        return repo.findRecordSourceById(recordSourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record source not found"));
    }

    @Override
    public RecordSourceDto updateRecordSource(Long recordSourceId, RecordSourceDto recordSource) {
        log.debug("Entering updateRecordSource - {}, {}", recordSourceId, recordSource);
        repo.findRecordSourceById(recordSourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record source not found"));
        return repo.updateRecordSource(recordSourceId, recordSource);
    }

    @Override
    public void deleteRecordSource(Long recordSourceId) {
        log.debug("Entering deleteRecordSource - {}", recordSourceId);
        repo.deleteRecordSource(recordSourceId);
    }
}

