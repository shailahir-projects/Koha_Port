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
}

