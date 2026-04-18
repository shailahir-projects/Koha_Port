package com.shailahir.koha.catalog.controller;

import com.shailahir.koha.catalog.dto.AuthorityDto;
import com.shailahir.koha.catalog.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

/**
 * REST controller for authority records.
 * Maps to Swagger paths: /authorities, /authorities/{authority_id}
 */
@RestController
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @GetMapping("/authorities", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<AuthorityDto>> listAuthorities(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(authorityService.listAuthorities(query, pageable));
    }

    @PostMapping("/authorities", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AuthorityDto> addAuthority(
            @RequestBody AuthorityDto authority,
            @RequestHeader(value = "x-authority-type", required = false) String authorityType,
            @RequestHeader(value = "x-koha-override", required = false) java.util.List<String> overrides) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorityService.addAuthority(authority, authorityType));
    }

    @GetMapping("/authorities/{authority_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AuthorityDto> getAuthority(@PathVariable("authority_id") Long authorityId) {
        return ResponseEntity.ok(authorityService.getAuthority(authorityId));
    }

    @PutMapping("/authorities/{authority_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<AuthorityDto> updateAuthority(
            @PathVariable("authority_id") Long authorityId,
            @RequestBody AuthorityDto authority,
            @RequestHeader(value = "x-authority-type", required = false) String authorityType) {
        return ResponseEntity.ok(authorityService.updateAuthority(authorityId, authority, authorityType));
    }

    @DeleteMapping("/authorities/{authority_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteAuthority(@PathVariable("authority_id") Long authorityId) {
        authorityService.deleteAuthority(authorityId);
        return ResponseEntity.noContent().build();
    }
}

