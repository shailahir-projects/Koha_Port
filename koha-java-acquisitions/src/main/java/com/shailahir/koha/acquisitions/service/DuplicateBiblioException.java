package com.shailahir.koha.acquisitions.service;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated Use {@link com.shailahir.koha.acquisitions.exception.DuplicateBiblioException} instead.
 */
@Slf4j
@Deprecated(since = "1.0", forRemoval = true)
public class DuplicateBiblioException extends com.shailahir.koha.acquisitions.exception.DuplicateBiblioException {
    public DuplicateBiblioException(Long existingBiblionumber) {
        super(existingBiblionumber);
    }
    /** Backward-compat alias for {@link #getExistingBiblionumber()}. */
    public Long getDuplicateBiblionumber() {
        log.debug("Entering getDuplicateBiblionumber");
        return getExistingBiblionumber();
    }
}
