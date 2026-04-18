package com.shailahir.koha.patron.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PatronFilesService {
    List<Map<String, Object>> listFiles(Long patronId);
    Map<String, Object> uploadFile(Long patronId, MultipartFile file, String description);
    byte[] downloadFile(Long patronId, Long fileId);
    void deleteFile(Long patronId, Long fileId);
    List<Map<String, Object>> listApiKeys(Long patronId);
    Map<String, Object> generateApiKey(Long patronId);
    void revokeApiKey(Long patronId, Long apiKeyId);
    Map<String, Object> getHouseboundProfile(Long patronId);
    void updateHouseboundProfile(Long patronId, Map<String, Object> profile);
    Map<String, Object> getMessagePreferences(Long patronId);
    void updateMessagePreferences(Long patronId, Map<String, Object> prefs);
}

