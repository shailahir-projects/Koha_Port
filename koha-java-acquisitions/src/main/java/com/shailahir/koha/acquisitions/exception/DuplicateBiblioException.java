package com.shailahir.koha.acquisitions.exception;
import lombok.extern.slf4j.Slf4j;

/**
 * Thrown when a probable duplicate bibliographic record is detected during order creation.
 * Ported from duplicate detection logic in addorder.pl / FindDuplicate.
 */
@Slf4j
public class DuplicateBiblioException extends AcquisitionException {

    private final Long existingBiblionumber;

    public DuplicateBiblioException(Long existingBiblionumber) {
        super("Duplicate biblio detected: biblionumber=" + existingBiblionumber);
        this.existingBiblionumber = existingBiblionumber;
    }

    public Long getExistingBiblionumber() {
        log.debug("Entering getExistingBiblionumber");
        return existingBiblionumber;
    }
}

