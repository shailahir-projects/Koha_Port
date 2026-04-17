package com.shailahir.koha.acquisitions.service;

/**
 * Thrown when a potential duplicate biblio is detected and the caller
 * has not confirmed they wish to proceed (confirm_not_duplicate=false).
 */
public class DuplicateBiblioException extends RuntimeException {

    private final Long duplicateBiblionumber;

    public DuplicateBiblioException(Long duplicateBiblionumber) {
        super("Possible duplicate biblio: " + duplicateBiblionumber);
        this.duplicateBiblionumber = duplicateBiblionumber;
    }

    public Long getDuplicateBiblionumber() {
        return duplicateBiblionumber;
    }
}

