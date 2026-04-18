package com.shailahir.koha.auth.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class AuthProviderDto {
    private Long authProviderId;
    private String code;
    private String description;
    private String protocol;
    private String config;
    private String mappingConfig;
    private Boolean enabled;
    private Boolean autoRegister;
    private Boolean updateOnAuth;
    private String defaultLibraryId;
    private String defaultCategory;
}
