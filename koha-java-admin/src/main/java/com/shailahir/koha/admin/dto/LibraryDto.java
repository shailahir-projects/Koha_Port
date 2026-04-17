package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LibraryDto {
    private String libraryId;
    private String name;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zipPostal;
    private String country;
    private String phone;
    private String fax;
    private String email;
    private String replyToEmail;
    private String returnPath;
    private String url;
    private String ip;
    private String notes;
    private Boolean pickup;
    private Boolean opacHiddenItems;
}

