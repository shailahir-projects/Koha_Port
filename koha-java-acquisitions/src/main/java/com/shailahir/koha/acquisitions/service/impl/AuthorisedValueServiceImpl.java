package com.shailahir.koha.acquisitions.service.impl;

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
@Service
@RequiredArgsConstructor
public class AuthorisedValueServiceImpl implements AuthorisedValueService {

    private final AuthorisedValueRepository authorisedValueRepo;

    @Override
    public List<AuthorisedValueDto> getValues(String category, String branchcode) {
        return authorisedValueRepo.findByCategory(category, branchcode);
    }
}

