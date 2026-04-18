package com.shailahir.koha.notification.service;

import com.shailahir.koha.notification.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NoticeTemplateDto> listNotices(String query, Pageable pageable);
    NoticeTemplateDto addNotice(NoticeTemplateDto dto);
    NoticeTemplateDto getNotice(Long id);
    NoticeTemplateDto updateNotice(Long id, NoticeTemplateDto dto);
    void deleteNotice(Long id);

    Page<AdditionalContentDto> listContent(String query, Pageable pageable);
    AdditionalContentDto addContent(AdditionalContentDto dto);
    AdditionalContentDto getContent(Long id);
    AdditionalContentDto updateContent(Long id, AdditionalContentDto dto);
    void deleteContent(Long id);
}

