package com.shailahir.koha.patron.service;

import com.shailahir.koha.patron.dto.PatronDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PatronAdminService {
    Map<String, Object> getHome();
    Map<String, Object> issueDischarge(Long patronId);
    List<Map<String, Object>> listDischarges(String status);
    PatronDto mergePatrons(Long keepPatronId, Long deletePatronId);
    void setStatus(Long patronId, Map<String, Object> statusRequest);
    void addDebarment(Long patronId, Map<String, String> debarmentRequest);
    void removeDebarment(Long patronId);
    void updateFlags(Long patronId, Long flags);
    Map<String, Object> getTwoFactorAuthStatus(Long patronId);
    Map<String, Object> enrollTwoFactorAuth(Long patronId, String secret, String pin);
    void disableTwoFactorAuth(Long patronId);
    byte[] getPatronImage(Long patronId);
    void uploadPatronImage(Long patronId, MultipartFile image);
    void deletePatronImage(Long patronId);
    PatronDto updateCategory(Long patronId, String categorycode);
    List<Map<String, Object>> listPendingUpdates();
    void approveUpdate(Long patronId);
    void rejectUpdate(Long patronId);
}

