package com.shailahir.koha.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Notification controller covering:
 * - /public/additional_contents (GET)
 * - /config/smtp_servers (GET, POST)
 * - /config/smtp_servers/{smtp_server_id} (GET, PUT, DELETE)
 * - /config/file_transports (GET, POST)
 * - /config/file_transports/{file_transport_id} (GET, PUT, DELETE)
 */
@RestController
@RequiredArgsConstructor
public class NotificationController {

    @GetMapping("/public/additional_contents")
    public ResponseEntity<List<Map<String, Object>>> listAdditionalContents(Pageable pageable) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/config/smtp_servers")
    public ResponseEntity<List<Map<String, Object>>> listSMTPServers(Pageable pageable) {
        return ResponseEntity.ok(List.of());
    }
    @PostMapping("/config/smtp_servers")
    public ResponseEntity<Map<String, Object>> addSMTPServer(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of());
    }
    @GetMapping("/config/smtp_servers/{smtp_server_id}")
    public ResponseEntity<Map<String, Object>> getSMTPServer(@PathVariable("smtp_server_id") Long id) {
        return ResponseEntity.ok(Map.of());
    }
    @PutMapping("/config/smtp_servers/{smtp_server_id}")
    public ResponseEntity<Map<String, Object>> updateSMTPServer(@PathVariable("smtp_server_id") Long id, @RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(Map.of());
    }
    @DeleteMapping("/config/smtp_servers/{smtp_server_id}")
    public ResponseEntity<Void> deleteSMTPServer(@PathVariable("smtp_server_id") Long id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config/file_transports")
    public ResponseEntity<List<Map<String, Object>>> listFileTransports(Pageable pageable) {
        return ResponseEntity.ok(List.of());
    }
    @PostMapping("/config/file_transports")
    public ResponseEntity<Map<String, Object>> addFileTransport(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of());
    }
    @GetMapping("/config/file_transports/{file_transport_id}")
    public ResponseEntity<Map<String, Object>> getFileTransport(@PathVariable("file_transport_id") Long id) {
        return ResponseEntity.ok(Map.of());
    }
    @PutMapping("/config/file_transports/{file_transport_id}")
    public ResponseEntity<Map<String, Object>> updateFileTransport(@PathVariable("file_transport_id") Long id, @RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(Map.of());
    }
    @DeleteMapping("/config/file_transports/{file_transport_id}")
    public ResponseEntity<Void> deleteFileTransport(@PathVariable("file_transport_id") Long id) {
        return ResponseEntity.noContent().build();
    }
}

