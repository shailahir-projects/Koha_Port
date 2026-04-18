package com.shailahir.koha.batch.service;

import com.shailahir.koha.batch.dto.JobDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BatchService {
    Page<JobDto> listJobs(String query, Pageable pageable);
    JobDto addJob(JobDto dto);
    JobDto getJob(Long id);
    JobDto updateJob(Long id, JobDto dto);
    void deleteJob(Long id);
}

