package com.shailahir.koha.catalog.service;

import com.shailahir.koha.catalog.dto.AuthorityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorityService {
    Page<AuthorityDto> listAuthorities(String query, Pageable pageable);
    AuthorityDto addAuthority(AuthorityDto authority, String authorityType);
    AuthorityDto getAuthority(Long authorityId);
    AuthorityDto updateAuthority(Long authorityId, AuthorityDto authority, String authorityType);
    void deleteAuthority(Long authorityId);
}

