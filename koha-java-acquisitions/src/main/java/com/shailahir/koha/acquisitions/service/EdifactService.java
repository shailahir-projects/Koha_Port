package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.EdifactMessageDto;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for EDIFACT message management.
 * Ports edifactmsgs.pl and edimsg.pl.
 */
public interface EdifactService {

    /** Lists all EDIFACT messages — mirrors edifactmsgs.pl. */
    List<EdifactMessageDto> listMessages();

    /** Gets a single EDIFACT message — mirrors edimsg.pl. */
    Optional<EdifactMessageDto> getMessage(Long id);

    /** Returns the raw EDIFACT message text. */
    Optional<String> getRawMessage(Long id);

    /** Soft-deletes an EDIFACT message. */
    void deleteMessage(Long id);

    /** Marks a message as processed. */
    void markProcessed(Long id);
}

