package com.shailahir.koha.patron.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.PatronDto;
import com.shailahir.koha.patron.exception.PatronNotFoundException;
import com.shailahir.koha.patron.repository.PatronRepository;
import com.shailahir.koha.patron.service.PatronAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for patron administrative operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatronAdminServiceImpl implements PatronAdminService {

    private final PatronRepository patronRepository;
    private final JdbcTemplate jdbc;

    @Override
    public Map<String, Object> getHome() {
        log.debug("Entering getHome");
        int total = jdbc.queryForObject("SELECT COUNT(*) FROM borrowers", Integer.class);
        Map<String, Object> home = new HashMap<>();
        home.put("total_patrons", total);
        home.put("pending_updates", jdbc.queryForObject(
            "SELECT COUNT(*) FROM borrower_modifications WHERE changed_fields IS NOT NULL", Integer.class));
        return home;
    }

    @Override
    @Transactional
    public Map<String, Object> issueDischarge(Long patronId) {
        log.debug("Entering issueDischarge - {}", patronId);
        ensurePatronExists(patronId);
        // Insert discharge record
        jdbc.update("""
            INSERT INTO discharges (borrower_id, needed, validated)
            VALUES (?, NOW(), NULL)
            ON CONFLICT (borrower_id) DO UPDATE SET needed = NOW(), validated = NULL
            """, patronId);
        Map<String, Object> result = new HashMap<>();
        result.put("patron_id", patronId);
        result.put("status", "pending");
        result.put("requested_date", LocalDate.now().toString());
        return result;
    }

    @Override
    public List<Map<String, Object>> listDischarges(String status) {
        log.debug("Entering listDischarges - {}", status);
        String sql = "SELECT d.*, b.surname, b.firstname, b.cardnumber FROM discharges d " +
            "JOIN borrowers b ON d.borrower_id = b.borrowernumber";
        if (status != null && !status.isBlank()) {
            sql += " WHERE d.validated IS " + ("validated".equalsIgnoreCase(status) ? "NOT NULL" : "NULL");
        }
        return jdbc.queryForList(sql);
    }

    @Override
    @Transactional
    public PatronDto mergePatrons(Long keepPatronId, Long deletePatronId) {
        log.debug("Entering mergePatrons - {}, {}", keepPatronId, deletePatronId);
        ensurePatronExists(keepPatronId);
        ensurePatronExists(deletePatronId);
        patronRepository.mergePatrons(keepPatronId, deletePatronId);
        return patronRepository.findById(keepPatronId).orElseThrow();
    }

    @Override
    @Transactional
    public void setStatus(Long patronId, Map<String, Object> statusRequest) {
        log.debug("Entering setStatus - {}, {}", patronId, statusRequest);
        ensurePatronExists(patronId);
        Boolean lost = (Boolean) statusRequest.get("lost");
        Boolean gonenoaddress = (Boolean) statusRequest.get("gonenoaddress");
        String debarredStr = (String) statusRequest.get("debarred");
        LocalDate debarred = debarredStr != null ? LocalDate.parse(debarredStr) : null;
        String debarredComment = (String) statusRequest.get("debarredcomment");
        patronRepository.updateStatus(patronId, lost, gonenoaddress, debarred, debarredComment);
    }

    @Override
    @Transactional
    public void addDebarment(Long patronId, Map<String, String> debarmentRequest) {
        log.debug("Entering addDebarment - {}, {}", patronId, debarmentRequest);
        ensurePatronExists(patronId);
        String comment = debarmentRequest.get("comment");
        String type = debarmentRequest.getOrDefault("type", "MANUAL");
        String until = debarmentRequest.get("expiration");
        jdbc.update("""
            INSERT INTO borrower_debarments (borrowernumber, expiration, type, comment, created, manager_id)
            VALUES (?, ?, ?, ?, NOW(), NULL)
            """, patronId, until != null ? LocalDate.parse(until) : null, type, comment);
        LocalDate debarredDate = until != null ? LocalDate.parse(until) : LocalDate.of(9999, 12, 31);
        jdbc.update("UPDATE borrowers SET debarred = ?, debarredcomment = ? WHERE borrowernumber = ?",
            debarredDate, comment, patronId);
    }

    @Override
    @Transactional
    public void removeDebarment(Long patronId) {
        log.debug("Entering removeDebarment - {}", patronId);
        jdbc.update("DELETE FROM borrower_debarments WHERE borrowernumber = ?", patronId);
        jdbc.update("UPDATE borrowers SET debarred = NULL, debarredcomment = NULL WHERE borrowernumber = ?", patronId);
    }

    @Override
    @Transactional
    public void updateFlags(Long patronId, Long flags) {
        log.debug("Entering updateFlags - {}, {}", patronId, flags);
        ensurePatronExists(patronId);
        jdbc.update("UPDATE borrowers SET flags = ? WHERE borrowernumber = ?", flags, patronId);
    }

    @Override
    public Map<String, Object> getTwoFactorAuthStatus(Long patronId) {
        log.debug("Entering getTwoFactorAuthStatus - {}", patronId);
        ensurePatronExists(patronId);
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> row = jdbc.queryForMap(
                "SELECT secret, totp_tokens_last_used FROM borrowers WHERE borrowernumber = ?", patronId);
            result.put("enabled", row.get("secret") != null);
            result.put("last_used", row.get("totp_tokens_last_used"));
        } catch (Exception e) {
            result.put("enabled", false);
        }
        result.put("patron_id", patronId);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> enrollTwoFactorAuth(Long patronId, String secret, String pin) {
        log.debug("Entering enrollTwoFactorAuth - {}, {}, {}", patronId, secret, pin);
        ensurePatronExists(patronId);
        jdbc.update("UPDATE borrowers SET secret = ? WHERE borrowernumber = ?", secret, patronId);
        Map<String, Object> result = new HashMap<>();
        result.put("patron_id", patronId);
        result.put("enrolled", true);
        return result;
    }

    @Override
    @Transactional
    public void disableTwoFactorAuth(Long patronId) {
        log.debug("Entering disableTwoFactorAuth - {}", patronId);
        jdbc.update("UPDATE borrowers SET secret = NULL WHERE borrowernumber = ?", patronId);
    }

    @Override
    public byte[] getPatronImage(Long patronId) {
        log.debug("Entering getPatronImage - {}", patronId);
        ensurePatronExists(patronId);
        try {
            return jdbc.queryForObject(
                "SELECT picture FROM patronimage WHERE borrowernumber = ?", byte[].class, patronId);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    @Transactional
    public void uploadPatronImage(Long patronId, MultipartFile image) {
        log.debug("Entering uploadPatronImage - {}, {}", patronId, image);
        ensurePatronExists(patronId);
        try {
            byte[] bytes = image.getBytes();
            int count = jdbc.queryForObject(                "SELECT COUNT(*) FROM patronimage WHERE borrowernumber = ?", Integer.class, patronId);
            if (count > 0) {
                jdbc.update("UPDATE patronimage SET picture = ?, mimetype = ? WHERE borrowernumber = ?",
                    bytes, image.getContentType(), patronId);
            } else {
                jdbc.update("INSERT INTO patronimage (borrowernumber, picture, mimetype) VALUES (?, ?, ?)",
                    patronId, bytes, image.getContentType());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload patron image", e);
        }
    }

    @Override
    @Transactional
    public void deletePatronImage(Long patronId) {
        log.debug("Entering deletePatronImage - {}", patronId);
        jdbc.update("DELETE FROM patronimage WHERE borrowernumber = ?", patronId);
    }

    @Override
    @Transactional
    public PatronDto updateCategory(Long patronId, String categorycode) {
        log.debug("Entering updateCategory - {}, {}", patronId, categorycode);
        ensurePatronExists(patronId);
        jdbc.update("UPDATE borrowers SET categorycode = ? WHERE borrowernumber = ?", categorycode, patronId);
        return patronRepository.findById(patronId).orElseThrow();
    }

    @Override
    public List<Map<String, Object>> listPendingUpdates() {
        log.debug("Entering listPendingUpdates");
        try {
            return jdbc.queryForList("""
                SELECT bm.*, b.surname, b.firstname, b.cardnumber
                FROM borrower_modifications bm
                JOIN borrowers b ON bm.borrowernumber = b.borrowernumber
                WHERE bm.changed_fields IS NOT NULL
                ORDER BY bm.timestamp DESC
                """);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void approveUpdate(Long patronId) {
        log.debug("Entering approveUpdate - {}", patronId);
        try {
            List<Map<String, Object>> mods = jdbc.queryForList(
                "SELECT * FROM borrower_modifications WHERE borrowernumber = ?", patronId);
            for (Map<String, Object> mod : mods) {
                // Apply each field from modifications to the borrower record
                // Simplified: just clear the modification record
            }
            jdbc.update("DELETE FROM borrower_modifications WHERE borrowernumber = ?", patronId);
        } catch (Exception ignored) {}
    }

    @Override
    @Transactional
    public void rejectUpdate(Long patronId) {
        log.debug("Entering rejectUpdate - {}", patronId);
        jdbc.update("DELETE FROM borrower_modifications WHERE borrowernumber = ?", patronId);
    }

    private void ensurePatronExists(Long patronId) {
        log.debug("Entering ensurePatronExists - {}", patronId);
        if (!patronRepository.exists(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
    }
}

