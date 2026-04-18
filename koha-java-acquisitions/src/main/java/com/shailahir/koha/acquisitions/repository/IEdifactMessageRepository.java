package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.EdifactMessageDto;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EDIFACT messages.
 * Ported from edifactmsgs.pl and edimsg.pl.
 */
public interface IEdifactMessageRepository {

    List<EdifactMessageDto> findAll();
    Optional<EdifactMessageDto> findById(Long id);
    void softDelete(Long id);
    void markAsProcessed(Long id);
    Optional<String> getRawMsg(Long id);
}

