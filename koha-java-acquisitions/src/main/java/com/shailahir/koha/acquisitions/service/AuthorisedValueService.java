package com.shailahir.koha.acquisitions.service;

import com.shailahir.koha.acquisitions.dto.AuthorisedValueDto;

import java.util.List;

/**
 * Business logic for authorised values lookup.
 * Ports ajax-getauthvaluedropbox.pl.
 */
public interface AuthorisedValueService {

    /**
     * Returns authorised values for a given category and optional branch code.
     * Mirrors ajax-getauthvaluedropbox.pl.
     *
     * @param category   the authorised value category (e.g., "SUGGEST_STATUS")
     * @param branchcode optional branch code filter
     * @return list of authorised values
     */
    List<AuthorisedValueDto> getValues(String category, String branchcode);
}

