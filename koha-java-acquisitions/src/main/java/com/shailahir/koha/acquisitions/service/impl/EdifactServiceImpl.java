package com.shailahir.koha.acquisitions.service.impl;

import com.shailahir.koha.acquisitions.dto.EdifactMessageDto;
import com.shailahir.koha.acquisitions.repository.EdifactMessageRepository;
import com.shailahir.koha.acquisitions.service.EdifactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link EdifactService}.
 * Ports edifactmsgs.pl and edimsg.pl business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EdifactServiceImpl implements EdifactService {

    private final EdifactMessageRepository edifactRepo;

    @Override
    public List<EdifactMessageDto> listMessages() {
        log.debug("Entering listMessages");
        return edifactRepo.findAll();
    }

    @Override
    public Optional<EdifactMessageDto> getMessage(Long id) {
        log.debug("Entering getMessage - {}", id);
        return edifactRepo.findById(id);
    }

    @Override
    public Optional<String> getRawMessage(Long id) {
        log.debug("Entering getRawMessage - {}", id);
        return edifactRepo.getRawMsg(id);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        log.info("Soft-deleting EDIFACT message {}", id);
        edifactRepo.softDelete(id);
    }

    @Override
    @Transactional
    public void markProcessed(Long id) {
        log.info("Marking EDIFACT message {} as processed", id);
        edifactRepo.markAsProcessed(id);
    }
}

