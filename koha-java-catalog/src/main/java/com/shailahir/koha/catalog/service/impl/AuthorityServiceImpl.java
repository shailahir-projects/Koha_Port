package com.shailahir.koha.catalog.service.impl;

import com.shailahir.koha.catalog.dto.AuthorityDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final CatalogRepository repo;

    @Override
    public Page<AuthorityDto> listAuthorities(String query, Pageable pageable) {
        return repo.findAllAuthorities(query, null, pageable);
    }

    @Override
    public AuthorityDto addAuthority(AuthorityDto authority, String authorityType) {
        if (authorityType != null) authority.setAuthorityType(authorityType);
        return repo.insertAuthority(authority);
    }

    @Override
    public AuthorityDto getAuthority(Long authorityId) {
        return repo.findAuthorityById(authorityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authority not found"));
    }

    @Override
    public AuthorityDto updateAuthority(Long authorityId, AuthorityDto authority, String authorityType) {
        repo.findAuthorityById(authorityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authority not found"));
        if (authorityType != null) authority.setAuthorityType(authorityType);
        return repo.updateAuthority(authorityId, authority);
    }

    @Override
    public void deleteAuthority(Long authorityId) {
        repo.deleteAuthority(authorityId);
    }
}

