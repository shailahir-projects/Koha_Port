package com.shailahir.koha.acquisitions.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for invoice-file attachment operations.
 * Ported from invoice-files.pl.
 */
public interface IInvoiceFilesRepository {

    List<Map<String, Object>> getFilesInfo(Long invoiceid);
    Optional<Map<String, Object>> getFile(Long fileId, Long invoiceid);
    Long addFile(Long invoiceid, String fileName, String fileType,
            String fileDescription, byte[] fileContent, Long uploadedBy);
    void deleteFile(Long fileId, Long invoiceid);
}

