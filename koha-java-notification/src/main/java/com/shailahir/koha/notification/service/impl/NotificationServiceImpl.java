package com.shailahir.koha.notification.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.notification.dto.*;
import com.shailahir.koha.notification.repository.NotificationRepository;
import com.shailahir.koha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    @Override
    public Page<NoticeTemplateDto> listNotices(String query, Pageable pageable) {
        log.debug("Entering listNotices - {}, {}", query, pageable);
        return repo.findAllNotices(query, pageable);
    }

    @Override
    public NoticeTemplateDto addNotice(NoticeTemplateDto dto) {
        log.debug("Entering addNotice - {}", dto);
        return repo.insertNotice(dto);
    }

    @Override
    public NoticeTemplateDto getNotice(Long id) {
        log.debug("Entering getNotice - {}", id);
        return repo.findNoticeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));
    }

    @Override
    public NoticeTemplateDto updateNotice(Long id, NoticeTemplateDto dto) {
        log.debug("Entering updateNotice - {}, {}", id, dto);
        repo.findNoticeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));
        return repo.updateNotice(id, dto);
    }

    @Override
    public void deleteNotice(Long id) {
        log.debug("Entering deleteNotice - {}", id);
        repo.deleteNotice(id);
    }

    @Override
    public Page<AdditionalContentDto> listContent(String query, Pageable pageable) {
        log.debug("Entering listContent - {}, {}", query, pageable);
        return repo.findAllContent(query, pageable);
    }

    @Override
    public AdditionalContentDto addContent(AdditionalContentDto dto) {
        log.debug("Entering addContent - {}", dto);
        return repo.insertContent(dto);
    }

    @Override
    public AdditionalContentDto getContent(Long id) {
        log.debug("Entering getContent - {}", id);
        return repo.findContentById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
    }

    @Override
    public AdditionalContentDto updateContent(Long id, AdditionalContentDto dto) {
        log.debug("Entering updateContent - {}, {}", id, dto);
        repo.findContentById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
        return repo.updateContent(id, dto);
    }

    @Override
    public void deleteContent(Long id) {
        log.debug("Entering deleteContent - {}", id);
        repo.deleteContent(id);
    }
}

