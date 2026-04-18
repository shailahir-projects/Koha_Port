package com.shailahir.koha.acquisitions.service.impl;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.AuthorisedValueDto;
import com.shailahir.koha.acquisitions.repository.AuthorisedValueRepository;
import com.shailahir.koha.acquisitions.service.AuthorisedValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link AuthorisedValueService}.
 * Ports ajax-getauthvaluedropbox.pl business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorisedValueServiceImpl implements AuthorisedValueService {

    private final AuthorisedValueRepository authorisedValueRepo;

    @Override
    public List<AuthorisedValueDto> getValues(String category, String branchcode) {
        log.debug("Entering getValues - {}, {}", category, branchcode);
        return authorisedValueRepo.findByCategory(category, branchcode);
    }
}

