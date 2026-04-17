package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.EdifactMessageDto;
import com.shailahir.koha.acquisitions.repository.EdifactMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REST controller porting edifactmsgs.pl and edimsg.pl — EDIFACT message management.
 *
 * <pre>
 *  GET    /acquisitions/edifact-messages              — list all non-deleted messages
 *  GET    /acquisitions/edifact-messages/{id}         — get single message (edimsg.pl)
 *  GET    /acquisitions/edifact-messages/{id}/raw     — raw EDI content
 *  GET    /acquisitions/edifact-messages/{id}/segments— segmentized EDI (edimsg.pl HTML view)
 *  DELETE /acquisitions/edifact-messages/{id}         — op=cud-delete (soft-delete)
 *  POST   /acquisitions/edifact-messages/{id}/import  — op=import (process INVOIC)
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class EdifactMsgsController {

    private final EdifactMessageRepository edifactRepo;

    /**
     * Returns all non-deleted EDIFACT messages ordered by transfer_date descending.
     * Used to populate the edifactmsgs.tt listing page.
     */
    @GetMapping("/acquisitions/edifact-messages")
    public ResponseEntity<List<EdifactMessageDto>> listMessages() {
        return ResponseEntity.ok(edifactRepo.findAll());
    }

    /**
     * Returns a single EDIFACT message by id.
     * Mirrors edimsg.pl — page load for a known message.
     */
    @GetMapping("/acquisitions/edifact-messages/{id}")
    public ResponseEntity<EdifactMessageDto> getMessage(@PathVariable Long id) {
        return edifactRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns the raw EDI transmission for a message.
     * Mirrors edimsg.pl: GET ?id=X&format=raw
     */
    @GetMapping("/acquisitions/edifact-messages/{id}/raw")
    public ResponseEntity<String> getRawMessage(@PathVariable Long id) {
        String raw = edifactRepo.getRawMsg(id)
                .orElseThrow(() -> new NoSuchElementException("EDIFACT message not found: " + id));
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(raw);
    }

    /**
     * Returns the EDI transmission split into its component segments.
     * Mirrors the HTML view of edimsg.pl — calls segmentize() on the raw_msg.
     * <p>
     * EDI segment splitting rule (mirrors the Perl regex in edimsg.pl):
     * A segment ends at an unescaped {@code '} character.
     * The escape character is {@code ?} — {@code ?'} is a literal apostrophe, not a separator.
     * <p>
     * Example response:
     * <pre>
     * { "segments": ["UNA:+.? '", "UNB+UNOC:3+...'", ...], "id": 42 }
     * </pre>
     */
    @GetMapping("/acquisitions/edifact-messages/{id}/segments")
    public ResponseEntity<Map<String, Object>> getSegments(@PathVariable Long id) {
        edifactRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("EDIFACT message not found: " + id));

        String raw = edifactRepo.getRawMsg(id).orElse("");
        List<String> segments = segmentize(raw);

        return ResponseEntity.ok(Map.of(
                "id", id,
                "segments", segments
        ));
    }

    /**
     * Soft-deletes an EDIFACT message — op=cud-delete.
     * Sets {@code deleted = 1} on the message row.
     * Mirrors: $msg->deleted(1); $msg->update;
     */
    @DeleteMapping("/acquisitions/edifact-messages/{id}")
    @Transactional
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        edifactRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("EDIFACT message not found: " + id));
        edifactRepo.softDelete(id);
        log.info("EDIFACT message {} soft-deleted", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Triggers processing of an EDIFACT INVOIC message — op=import.
     * <p>
     * The full {@code process_invoice()} logic (parsing the EDI transmission,
     * creating {@code aqinvoices} rows, matching order lines, updating
     * {@code quantityreceived}) belongs in a dedicated EDI microservice that
     * has access to the EDI parsing libraries (Koha::Edifact).
     * <p>
     * This endpoint records that the import was requested and marks the message
     * status as {@code 'processed'}, providing the raw message content in the
     * response so the EDI service can consume it asynchronously.
     *
     * @param id EDIFACT message id (must be of type INVOIC)
     * @return message details + raw EDI content for downstream processing
     */
    @PostMapping("/acquisitions/edifact-messages/{id}/import")
    @Transactional
    public ResponseEntity<Map<String, Object>> importMessage(@PathVariable Long id) {
        EdifactMessageDto msg = edifactRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("EDIFACT message not found: " + id));

        String rawMsg = edifactRepo.getRawMsg(id).orElse(null);

        // Mark as processed in DB (mirrors status change from process_invoice)
        edifactRepo.markAsProcessed(id);
        log.info("EDIFACT message {} (type={}) marked as processed", id, msg.getMessageType());

        return ResponseEntity.ok(Map.of(
                "message_id",    id,
                "message_type",  msg.getMessageType() != null ? msg.getMessageType() : "",
                "vendor_id",     msg.getVendorId()    != null ? msg.getVendorId()    : 0,
                "basketno",      msg.getBasketno()    != null ? msg.getBasketno()    : 0,
                "status",        "processed",
                "raw_msg",       rawMsg != null ? rawMsg : "",
                "note",          "Full EDI invoice processing is delegated to the EDI microservice"
        ));
    }

    // ── EDI segmentize ─────────────────────────────────────────────────────────

    /**
     * Splits a raw EDI transmission into its component segments.
     * Mirrors the segmentize() sub in edimsg.pl.
     * <p>
     * An EDI segment ends at an unescaped {@code '} character.
     * The escape character is {@code ?}: {@code ?'} is a literal apostrophe,
     * not a segment terminator.
     * Each matched run is appended with a trailing {@code '} to restore the
     * segment terminator that was consumed as a delimiter.
     */
    private List<String> segmentize(String raw) {
        List<String> segments = new ArrayList<>();
        if (raw == null || raw.isBlank()) return segments;
        // Match runs of: escaped-pair (?.) OR any char that is not ' or ?
        Pattern p = Pattern.compile("(?:[?].|[^'?])+");
        Matcher m = p.matcher(raw);
        while (m.find()) {
            segments.add(m.group() + "'");
        }
        return segments;
    }
}

