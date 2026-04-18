package com.shailahir.koha.ill.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.ill.dto.*;
import com.shailahir.koha.ill.repository.IllRepository;
import com.shailahir.koha.ill.service.IllService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IllServiceImpl implements IllService {

    private final IllRepository repo;

    @Override
    public Page<IllRequestDto> listRequests(String query, Pageable pageable) {
        log.debug("Entering listRequests - {}, {}", query, pageable);
        return repo.findAllRequests(query, pageable);
    }

    @Override
    public IllRequestDto createRequest(IllRequestDto dto) {
        log.debug("Entering createRequest - {}", dto);
        return repo.insertRequest(dto);
    }

    @Override
    public IllRequestDto getRequest(Long id) {
        log.debug("Entering getRequest - {}", id);
        return repo.findRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL request not found"));
    }

    @Override
    public IllRequestDto updateRequest(Long id, IllRequestDto dto) {
        log.debug("Entering updateRequest - {}, {}", id, dto);
        repo.findRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL request not found"));
        return repo.updateRequest(id, dto);
    }

    @Override
    public void deleteRequest(Long id) {
        log.debug("Entering deleteRequest - {}", id);
        repo.deleteRequest(id);
    }

    @Override
    public List<IllRequestCommentDto> listComments(Long requestId) {
        log.debug("Entering listComments - {}", requestId);
        return repo.findCommentsByRequestId(requestId);
    }

    @Override
    public IllRequestCommentDto addComment(Long requestId, IllRequestCommentDto dto) {
        log.debug("Entering addComment - {}, {}", requestId, dto);
        return repo.insertComment(requestId, dto);
    }

    @Override
    public Page<IllBatchDto> listBatches(String query, Pageable pageable) {
        log.debug("Entering listBatches - {}, {}", query, pageable);
        return repo.findAllBatches(query, pageable);
    }

    @Override
    public IllBatchDto createBatch(IllBatchDto dto) {
        log.debug("Entering createBatch - {}", dto);
        return repo.insertBatch(dto);
    }

    @Override
    public IllBatchDto getBatch(Long id) {
        log.debug("Entering getBatch - {}", id);
        return repo.findBatchById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL batch not found"));
    }

    @Override
    public IllBatchDto updateBatch(Long id, IllBatchDto dto) {
        log.debug("Entering updateBatch - {}, {}", id, dto);
        repo.findBatchById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL batch not found"));
        return repo.updateBatch(id, dto);
    }

    @Override
    public void deleteBatch(Long id) {
        log.debug("Entering deleteBatch - {}", id);
        repo.deleteBatch(id);
    }

    @Override
    public List<IllBatchStatusDto> listBatchStatuses() {
        log.debug("Entering listBatchStatuses");
        return repo.findAllBatchStatuses();
    }

    @Override
    public IllBatchStatusDto createBatchStatus(IllBatchStatusDto dto) {
        log.debug("Entering createBatchStatus - {}", dto);
        return repo.insertBatchStatus(dto);
    }

    @Override
    public IllBatchStatusDto getBatchStatus(String code) {
        log.debug("Entering getBatchStatus - {}", code);
        return repo.findBatchStatusByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL batch status not found"));
    }

    @Override
    public IllBatchStatusDto updateBatchStatus(String code, IllBatchStatusDto dto) {
        log.debug("Entering updateBatchStatus - {}, {}", code, dto);
        repo.findBatchStatusByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL batch status not found"));
        return repo.updateBatchStatus(code, dto);
    }

    @Override
    public void deleteBatchStatus(String code) {
        log.debug("Entering deleteBatchStatus - {}", code);
        repo.deleteBatchStatus(code);
    }

    @Override
    public List<IllBackendDto> listBackends() {
        log.debug("Entering listBackends");
        return repo.findAllBackends();
    }

    @Override
    public IllBackendDto getBackend(String id) {
        log.debug("Entering getBackend - {}", id);
        return repo.findBackendById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL backend not found"));
    }

    @Override
    public Page<IllUserDto> listUsers(String query, Pageable pageable) {
        log.debug("Entering listUsers - {}, {}", query, pageable);
        return repo.findAllUsers(query, pageable);
    }
}

