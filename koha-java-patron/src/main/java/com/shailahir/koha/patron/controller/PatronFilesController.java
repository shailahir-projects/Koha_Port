package com.shailahir.koha.patron.controller;

import com.shailahir.koha.patron.service.PatronFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Controller for patron file attachments and API keys.
 * Mirrors: members/files.pl, members/apikeys.pl, members/housebound.pl,
 *          members/ill-requests.pl (admin view), members/default_messageprefs.pl
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class PatronFilesController {

    private final PatronFilesService patronFilesService;

    /** members/files.pl - patron file attachments */
    @GetMapping("/patrons/{patron_id}/files")
    public ResponseEntity<List<Map<String, Object>>> listFiles(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronFilesService.listFiles(patronId));
    }

    @PostMapping(value = "/patrons/{patron_id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable("patron_id") Long patronId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronFilesService.uploadFile(patronId, file, description));
    }

    @GetMapping("/patrons/{patron_id}/files/{file_id}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable("patron_id") Long patronId,
            @PathVariable("file_id") Long fileId) {
        byte[] content = patronFilesService.downloadFile(patronId, fileId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(content);
    }

    @DeleteMapping("/patrons/{patron_id}/files/{file_id}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable("patron_id") Long patronId,
            @PathVariable("file_id") Long fileId) {
        patronFilesService.deleteFile(patronId, fileId);
        return ResponseEntity.noContent().build();
    }

    /** members/apikeys.pl - patron API keys */
    @GetMapping("/patrons/{patron_id}/api_keys")
    public ResponseEntity<List<Map<String, Object>>> listApiKeys(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronFilesService.listApiKeys(patronId));
    }

    @PostMapping("/patrons/{patron_id}/api_keys")
    public ResponseEntity<Map<String, Object>> generateApiKey(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patronFilesService.generateApiKey(patronId));
    }

    @DeleteMapping("/patrons/{patron_id}/api_keys/{api_key_id}")
    public ResponseEntity<Void> revokeApiKey(
            @PathVariable("patron_id") Long patronId,
            @PathVariable("api_key_id") Long apiKeyId) {
        patronFilesService.revokeApiKey(patronId, apiKeyId);
        return ResponseEntity.noContent().build();
    }

    /** members/housebound.pl - housebound patron delivery settings */
    @GetMapping("/patrons/{patron_id}/housebound")
    public ResponseEntity<Map<String, Object>> getHouseboundProfile(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronFilesService.getHouseboundProfile(patronId));
    }

    @PutMapping("/patrons/{patron_id}/housebound")
    public ResponseEntity<Void> updateHouseboundProfile(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, Object> profile) {
        patronFilesService.updateHouseboundProfile(patronId, profile);
        return ResponseEntity.ok().build();
    }

    /** members/default_messageprefs.pl - patron message preferences */
    @GetMapping("/patrons/{patron_id}/message_preferences")
    public ResponseEntity<Map<String, Object>> getMessagePreferences(@PathVariable("patron_id") Long patronId) {
        return ResponseEntity.ok(patronFilesService.getMessagePreferences(patronId));
    }

    @PutMapping("/patrons/{patron_id}/message_preferences")
    public ResponseEntity<Void> updateMessagePreferences(
            @PathVariable("patron_id") Long patronId,
            @RequestBody Map<String, Object> prefs) {
        patronFilesService.updateMessagePreferences(patronId, prefs);
        return ResponseEntity.ok().build();
    }
}

