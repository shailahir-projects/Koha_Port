package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.repository.InvoiceFilesRepository;
import com.shailahir.koha.acquisitions.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller porting invoice-files.pl — file management for invoices.
 * Uses Koha's misc_files table (tabletag='aqinvoices').
 *
 * <pre>
 *  GET    /acquisitions/invoices/{id}/files           — list files (GetFilesInfo)
 *  GET    /acquisitions/invoices/{id}/files/{fileId}  — download file (op=download)
 *  POST   /acquisitions/invoices/{id}/files           — upload file (op=cud-upload)
 *  DELETE /acquisitions/invoices/{id}/files/{fileId}  — delete file (op=cud-delete)
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class InvoiceFilesController {

    private final InvoiceFilesRepository filesRepo;
    private final InvoiceRepository      invoiceRepo;

    // ── List files (GetFilesInfo) ──────────────────────────────────────────────

    /**
     * Returns metadata for all files attached to the invoice.
     * Mirrors Koha::Misc::Files->GetFilesInfo() called from invoice-files.pl.
     */
    @GetMapping("/acquisitions/invoices/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> listFiles(@PathVariable Long id) {
        ensureInvoiceExists(id);
        return ResponseEntity.ok(filesRepo.getFilesInfo(id));
    }

    // ── Download (op=download) ─────────────────────────────────────────────────

    /**
     * Downloads a single file with the original filename and MIME type.
     * Mirrors the op=download branch in invoice-files.pl:
     * {@code print $input->header(-type => $ftype, -attachment => $fname)}.
     */
    @GetMapping("/acquisitions/invoices/{id}/files/{fileId}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        Map<String, Object> file = filesRepo.getFile(fileId, id)
                .orElseThrow(() -> new NoSuchElementException(
                        "File " + fileId + " not found for invoice " + id));

        String fileName = (String) file.getOrDefault("file_name", "file");
        String mimeType = (String) file.getOrDefault("file_type", "application/octet-stream");
        byte[] content  = (byte[]) file.get("file_content");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));
        headers.setContentDispositionFormData("attachment", fileName);
        if (content != null) headers.setContentLength(content.length);

        return ResponseEntity.ok().headers(headers).body(content);
    }

    // ── Upload (op=cud-upload) ─────────────────────────────────────────────────

    /**
     * Uploads and stores a new file for the invoice.
     * Mirrors the op=cud-upload branch in invoice-files.pl:
     * <ul>
     *   <li>Validates the file is not empty (mirrors {@code -z $uploaded_file})</li>
     *   <li>Normalises {@code application/force-download} and
     *       {@code application/unknown} to {@code application/pdf} for .pdf files</li>
     *   <li>Calls Koha::Misc::Files->AddFile()</li>
     * </ul>
     *
     * @param id          invoice id
     * @param file        multipart file upload (field name: uploadfile)
     * @param description optional file description
     * @return metadata of the newly stored file
     */
    @PostMapping(value = "/acquisitions/invoices/{id}/files",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long id,
            @RequestPart("uploadfile") MultipartFile file,
            @RequestParam(value = "description", required = false, defaultValue = "") String description) {

        ensureInvoiceExists(id);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "empty_upload", "message", "Uploaded file is empty"));
        }

        String originalFilename = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "upload";
        String mimeType = file.getContentType() != null
                ? file.getContentType() : "application/octet-stream";

        // Normalise force-download / unknown MIME for PDF files (mirrors invoice-files.pl)
        if (mimeType.matches("(?i)application/(force-download|unknown)")
                && originalFilename.toLowerCase().endsWith(".pdf")) {
            mimeType = "application/pdf";
        }

        byte[] content;
        try {
            content = file.getBytes();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "read_error", "message", e.getMessage()));
        }

        Long fileId = filesRepo.addFile(id, originalFilename, mimeType, content, description);
        log.info("Invoice {} — file '{}' uploaded (file_id={})", id, originalFilename, fileId);

        return ResponseEntity.status(201).body(Map.of(
                "file_id",          fileId,
                "invoiceid",        id,
                "file_name",        originalFilename,
                "file_type",        mimeType,
                "file_description", description
        ));
    }

    // ── Delete (op=cud-delete) ─────────────────────────────────────────────────

    /**
     * Deletes a file attached to the invoice.
     * Mirrors Koha::Misc::Files->DelFile(id => $file_id) in invoice-files.pl.
     */
    @DeleteMapping("/acquisitions/invoices/{id}/files/{fileId}")
    @Transactional
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        ensureInvoiceExists(id);
        filesRepo.deleteFile(fileId, id);
        log.info("Invoice {} — file {} deleted", id, fileId);
        return ResponseEntity.noContent().build();
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private void ensureInvoiceExists(Long id) {
        invoiceRepo.findInvoice(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
    }
}

