package com.shailahir.koha.patron.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.exception.PatronNotFoundException;
import com.shailahir.koha.patron.repository.PatronRepository;
import com.shailahir.koha.patron.service.PatronFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for patron file attachments, API keys, housebound, and message preferences.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatronFilesServiceImpl implements PatronFilesService {

    private final JdbcTemplate jdbc;
    private final PatronRepository patronRepository;

    @Override
    public List<Map<String, Object>> listFiles(Long patronId) {
        log.debug("Entering listFiles - {}", patronId);
        ensurePatronExists(patronId);
        try {
            return jdbc.queryForList("""
                SELECT id, file_name, file_type, date_uploaded
                FROM borrower_files WHERE borrowernumber = ?
                ORDER BY date_uploaded DESC
                """, patronId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Map<String, Object> uploadFile(Long patronId, MultipartFile file, String description) {
        log.debug("Entering uploadFile - {}, {}, {}", patronId, file, description);
        ensurePatronExists(patronId);
        try {
            byte[] fileBytes = file.getBytes();
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO borrower_files (borrowernumber, file_name, file_type, file_content, date_uploaded)
                    VALUES (?, ?, ?, ?, NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, patronId);
                ps.setString(2, file.getOriginalFilename());
                ps.setString(3, file.getContentType());
                ps.setBytes(4, fileBytes);
                return ps;
            }, kh);
            Map<String, Object> result = new HashMap<>();
            result.put("file_id", kh.getKeys().get("id"));
            result.put("file_name", file.getOriginalFilename());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public byte[] downloadFile(Long patronId, Long fileId) {
        log.debug("Entering downloadFile - {}, {}", patronId, fileId);
        ensurePatronExists(patronId);
        try {
            return jdbc.queryForObject(
                "SELECT file_content FROM borrower_files WHERE id = ? AND borrowernumber = ?",
                byte[].class, fileId, patronId);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    @Transactional
    public void deleteFile(Long patronId, Long fileId) {
        log.debug("Entering deleteFile - {}, {}", patronId, fileId);
        jdbc.update("DELETE FROM borrower_files WHERE id = ? AND borrowernumber = ?", fileId, patronId);
    }

    @Override
    public List<Map<String, Object>> listApiKeys(Long patronId) {
        log.debug("Entering listApiKeys - {}", patronId);
        ensurePatronExists(patronId);
        try {
            return jdbc.queryForList("""
                SELECT api_key_id, description, active
                FROM api_keys WHERE patron_id = ?
                ORDER BY api_key_id
                """, patronId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Map<String, Object> generateApiKey(Long patronId) {
        log.debug("Entering generateApiKey - {}", patronId);
        ensurePatronExists(patronId);
        String key = UUID.randomUUID().toString().replace("-", "");
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO api_keys (patron_id, api_key, active) VALUES (?, ?, 1)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, patronId);
            ps.setString(2, key);
            return ps;
        }, kh);
        Map<String, Object> result = new HashMap<>();
        result.put("api_key_id", kh.getKeys() != null ? kh.getKeys().get("api_key_id") : null);
        result.put("api_key", key);
        result.put("patron_id", patronId);
        return result;
    }

    @Override
    @Transactional
    public void revokeApiKey(Long patronId, Long apiKeyId) {
        log.debug("Entering revokeApiKey - {}, {}", patronId, apiKeyId);
        jdbc.update("UPDATE api_keys SET active = 0 WHERE api_key_id = ? AND patron_id = ?", apiKeyId, patronId);
    }

    @Override
    public Map<String, Object> getHouseboundProfile(Long patronId) {
        log.debug("Entering getHouseboundProfile - {}", patronId);
        ensurePatronExists(patronId);
        try {
            return jdbc.queryForMap("SELECT * FROM housebound_profile WHERE borrowernumber = ?", patronId);
        } catch (Exception e) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("patron_id", patronId);
            empty.put("housebound", false);
            return empty;
        }
    }

    @Override
    @Transactional
    public void updateHouseboundProfile(Long patronId, Map<String, Object> profile) {
        log.debug("Entering updateHouseboundProfile - {}, {}", patronId, profile);
        ensurePatronExists(patronId);
        try {
            int count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM housebound_profile WHERE borrowernumber = ?", Integer.class, patronId);
            if (count > 0) {
                jdbc.update("""
                    UPDATE housebound_profile SET day=?, frequency=?, fav_itemtypes=?,
                        fav_authors=?, notes=? WHERE borrowernumber=?
                    """,
                    profile.get("day"), profile.get("frequency"),
                    profile.get("fav_itemtypes"), profile.get("fav_authors"),
                    profile.get("notes"), patronId);
            } else {
                jdbc.update("""
                    INSERT INTO housebound_profile (borrowernumber, day, frequency, fav_itemtypes, fav_authors, notes)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, patronId, profile.get("day"), profile.get("frequency"),
                    profile.get("fav_itemtypes"), profile.get("fav_authors"), profile.get("notes"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update housebound profile", e);
        }
    }

    @Override
    public Map<String, Object> getMessagePreferences(Long patronId) {
        log.debug("Entering getMessagePreferences - {}", patronId);
        ensurePatronExists(patronId);
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("patron_id", patronId);
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT mpa.*, mt.message_name, mt.takes_days
                FROM message_preferences mp
                JOIN message_preference_attributes mpa ON mp.message_preference_id = mpa.message_preference_id
                JOIN message_transports mt ON mpa.message_transport_type = mt.message_transport_type
                WHERE mp.borrowernumber = ?
                """, patronId);
            prefs.put("preferences", rows);
        } catch (Exception e) {
            prefs.put("preferences", new ArrayList<>());
        }
        return prefs;
    }

    @Override
    @Transactional
    public void updateMessagePreferences(Long patronId, Map<String, Object> prefs) {
        log.debug("Entering updateMessagePreferences - {}, {}", patronId, prefs);
        ensurePatronExists(patronId);
        // Delete and re-insert message preferences
        jdbc.update("""
            DELETE mpa FROM message_preference_attributes mpa
            JOIN message_preferences mp ON mpa.message_preference_id = mp.message_preference_id
            WHERE mp.borrowernumber = ?
            """, patronId);
        // Re-insert logic would go here based on prefs structure
    }

    private void ensurePatronExists(Long patronId) {
        log.debug("Entering ensurePatronExists - {}", patronId);
        if (!patronRepository.exists(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
    }
}

