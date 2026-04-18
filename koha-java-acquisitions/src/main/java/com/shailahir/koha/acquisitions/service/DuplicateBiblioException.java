package com.shailahir.koha.acquisitions.service;

/**
 * @deprecated Use {@link com.shailahir.koha.acquisitions.exception.DuplicateBiblioException} instead.
 */
@Deprecated(since = "1.0", forRemoval = true)
public class DuplicateBiblioException extends com.shailahir.koha.acquisitions.exception.DuplicateBiblioException {
    public DuplicateBiblioException(Long existingBiblionumber) {
        super(existingBiblionumber);
    }
    /** Backward-compat alias for {@link #getExistingBiblionumber()}. */
    public Long getDuplicateBiblionumber() {
        return getExistingBiblionumber();
    }
}
