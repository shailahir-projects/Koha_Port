package com.shailahir.koha.ill.service;

import com.shailahir.koha.ill.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IllService {
    Page<IllRequestDto> listRequests(String query, Pageable pageable);
    IllRequestDto createRequest(IllRequestDto dto);
    IllRequestDto getRequest(Long id);
    IllRequestDto updateRequest(Long id, IllRequestDto dto);
    void deleteRequest(Long id);
    List<IllRequestCommentDto> listComments(Long requestId);
    IllRequestCommentDto addComment(Long requestId, IllRequestCommentDto dto);

    Page<IllBatchDto> listBatches(String query, Pageable pageable);
    IllBatchDto createBatch(IllBatchDto dto);
    IllBatchDto getBatch(Long id);
    IllBatchDto updateBatch(Long id, IllBatchDto dto);
    void deleteBatch(Long id);

    List<IllBatchStatusDto> listBatchStatuses();
    IllBatchStatusDto createBatchStatus(IllBatchStatusDto dto);
    IllBatchStatusDto getBatchStatus(String code);
    IllBatchStatusDto updateBatchStatus(String code, IllBatchStatusDto dto);
    void deleteBatchStatus(String code);

    List<IllBackendDto> listBackends();
    IllBackendDto getBackend(String id);

    Page<IllUserDto> listUsers(String query, Pageable pageable);
}

