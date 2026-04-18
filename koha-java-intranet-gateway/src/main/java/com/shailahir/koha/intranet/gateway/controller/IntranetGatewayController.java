package com.shailahir.koha.intranet.gateway.controller;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * Intranet Gateway controller - serves as the staff-facing API gateway.
 * Routes requests internally to Wave 1+2 microservices.
 * Health/status endpoint for monitoring.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class IntranetGatewayController {

    @GetMapping("/gateway/health", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Entering health");
        return ResponseEntity.ok(Map.of("status", "UP", "service", "koha-java-intranet-gateway"));
    }
}

