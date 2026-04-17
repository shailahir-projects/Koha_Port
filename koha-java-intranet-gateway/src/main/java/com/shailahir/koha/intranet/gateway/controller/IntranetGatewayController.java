package com.shailahir.koha.intranet.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Intranet Gateway controller - serves as the staff-facing API gateway.
 * Routes requests internally to Wave 1+2 microservices.
 * Health/status endpoint for monitoring.
 */
@RestController
@RequiredArgsConstructor
public class IntranetGatewayController {

    @GetMapping("/gateway/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "koha-java-intranet-gateway"));
    }
}

