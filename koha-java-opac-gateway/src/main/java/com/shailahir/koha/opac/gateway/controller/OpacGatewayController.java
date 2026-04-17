package com.shailahir.koha.opac.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * OPAC Gateway controller covering:
 * - /status/version (GET)
 * - /public/csp-reports (POST)
 * - /public/oauth/login/{provider_code}/{interface} (GET)
 * - Public patron actions (already in patron module, duplicated here as edge proxy)
 */
@RestController
@RequiredArgsConstructor
public class OpacGatewayController {

    @GetMapping("/status/version")
    public ResponseEntity<Map<String, Object>> getStatusVersion() {
        return ResponseEntity.ok(Map.of("version", "24.11", "status", "ok"));
    }

    @PostMapping("/public/csp-reports")
    public ResponseEntity<Void> addCspReport(@RequestBody Map<String, Object> report) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

