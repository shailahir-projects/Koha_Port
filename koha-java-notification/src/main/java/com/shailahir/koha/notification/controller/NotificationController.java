package com.shailahir.koha.notification.controller;

import com.shailahir.koha.notification.dto.AdditionalContentDto;
import com.shailahir.koha.notification.dto.NoticeTemplateDto;
import com.shailahir.koha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

/**
 * Notification controller - implements notice templates and additional content endpoints.
 */
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    // ── Notice Templates ──────────────────────────────────────────────────────

    @GetMapping("/notice_templates", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<NoticeTemplateDto>> listNotices(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(service.listNotices(q, pageable));
    }

    @PostMapping("/notice_templates", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NoticeTemplateDto> addNotice(@RequestBody NoticeTemplateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addNotice(dto));
    }

    @GetMapping("/notice_templates/{notice_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NoticeTemplateDto> getNotice(@PathVariable("notice_id") Long id) {
        return ResponseEntity.ok(service.getNotice(id));
    }

    @PutMapping("/notice_templates/{notice_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NoticeTemplateDto> updateNotice(@PathVariable("notice_id") Long id, @RequestBody NoticeTemplateDto dto) {
        return ResponseEntity.ok(service.updateNotice(id, dto));
    }

    @DeleteMapping("/notice_templates/{notice_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteNotice(@PathVariable("notice_id") Long id) {
        service.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    // ── Additional Contents ───────────────────────────────────────────────────

    @GetMapping("/additional_contents", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<AdditionalContentDto>> listAdditionalContents(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(service.listContent(q, pageable));
    }

    @PostMapping("/additional_contents", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdditionalContentDto> addAdditionalContent(@RequestBody AdditionalContentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addContent(dto));
    }

    @GetMapping("/additional_contents/{content_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdditionalContentDto> getAdditionalContent(@PathVariable("content_id") Long id) {
        return ResponseEntity.ok(service.getContent(id));
    }

    @PutMapping("/additional_contents/{content_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AdditionalContentDto> updateAdditionalContent(@PathVariable("content_id") Long id, @RequestBody AdditionalContentDto dto) {
        return ResponseEntity.ok(service.updateContent(id, dto));
    }

    @DeleteMapping("/additional_contents/{content_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteAdditionalContent(@PathVariable("content_id") Long id) {
        service.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    // SMTP/File Transport endpoints remain TODO
}

