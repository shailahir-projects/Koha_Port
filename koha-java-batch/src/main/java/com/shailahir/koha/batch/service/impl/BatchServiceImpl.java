package com.shailahir.koha.batch.service.impl;

import com.shailahir.koha.batch.dto.JobDto;
import com.shailahir.koha.batch.repository.BatchRepository;
import com.shailahir.koha.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final BatchRepository repo;

    @Override
    public Page<JobDto> listJobs(String query, Pageable pageable) {
        return repo.findAllJobs(query, pageable);
    }

    @Override
    public JobDto addJob(JobDto dto) {
        return repo.insertJob(dto);
    }

    @Override
    public JobDto getJob(Long id) {
        return repo.findJobById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
    }

    @Override
    public JobDto updateJob(Long id, JobDto dto) {
        repo.findJobById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        return repo.updateJob(id, dto);
    }

    @Override
    public void deleteJob(Long id) {
        repo.deleteJob(id);
    }
}

