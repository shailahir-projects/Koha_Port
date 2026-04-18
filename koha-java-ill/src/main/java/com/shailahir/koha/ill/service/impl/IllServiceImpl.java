package com.shailahir.koha.ill.service.impl;

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

@Service
@RequiredArgsConstructor
public class IllServiceImpl implements IllService {

    private final IllRepository repo;

    @Override
    public Page<IllRequestDto> listRequests(String query, Pageable pageable) {
        return repo.findAllRequests(query, pageable);
    }

    @Override
    public IllRequestDto createRequest(IllRequestDto dto) {
        return repo.insertRequest(dto);
    }

    @Override
    public IllRequestDto getRequest(Long id) {
        return repo.findRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL request not found"));
    }

    @Override
    public IllRequestDto updateRequest(Long id, IllRequestDto dto) {
        repo.findRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ILL request not found"));
        return repo.updateRequest(id, dto);
    }

    @Override
    public void deleteRequest(Long id) {
        repo.deleteRequest(id);
    }

    @Override
    public List<IllRequestCommentDto> listComments(Long requestId) {
        return repo.findCommentsByRequestId(requestId);
    }

    @Override
    public IllRequestCommentDto addComment(Long requestId, IllRequestCommentDto dto) {
        return repo.insertComment(requestId, dto);
    }
}

