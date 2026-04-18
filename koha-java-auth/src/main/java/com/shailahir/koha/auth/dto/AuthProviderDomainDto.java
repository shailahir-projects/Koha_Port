package com.shailahir.koha.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthProviderDomainDto {
    private Long domainId;
    private Long authProviderId;
    private String domain;
    private Boolean autoRegister;
    private Boolean updateOnAuth;
    private Long defaultCategoryId;
    private String defaultLibraryId;
    private String defaultCategory;
    private Integer matchpoint;
    private Boolean enabled;
}
