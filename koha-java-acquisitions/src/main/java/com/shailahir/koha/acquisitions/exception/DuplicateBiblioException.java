package com.shailahir.koha.acquisitions.exception;

/**
 * Thrown when a probable duplicate bibliographic record is detected during order creation.
 * Ported from duplicate detection logic in addorder.pl / FindDuplicate.
 */
public class DuplicateBiblioException extends AcquisitionException {

    private final Long existingBiblionumber;

    public DuplicateBiblioException(Long existingBiblionumber) {
        super("Duplicate biblio detected: biblionumber=" + existingBiblionumber);
        this.existingBiblionumber = existingBiblionumber;
    }

    public Long getExistingBiblionumber() {
        return existingBiblionumber;
    }
}

