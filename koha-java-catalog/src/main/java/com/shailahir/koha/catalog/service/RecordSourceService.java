package com.shailahir.koha.catalog.service;

import com.shailahir.koha.catalog.dto.RecordSourceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordSourceService {
    Page<RecordSourceDto> listRecordSources(String query, Pageable pageable);
    RecordSourceDto addRecordSource(RecordSourceDto recordSource);
    RecordSourceDto getRecordSource(Long recordSourceId);
    RecordSourceDto updateRecordSource(Long recordSourceId, RecordSourceDto recordSource);
    void deleteRecordSource(Long recordSourceId);
}

