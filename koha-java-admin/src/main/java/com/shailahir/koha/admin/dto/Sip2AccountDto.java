package com.shailahir.koha.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Sip2AccountDto {
    private Long sipAccountId;
    private String login;
    private String password;
    private String branchcode;
    private Boolean autoCheckin;
    private Long checkoutChargeType;
    private Boolean ageRestrictionOverride;
    private Boolean circulate;
    private Boolean checkout;
    private Boolean checkin;
    private Boolean renewals;
    private Boolean patronInfo;
    private Boolean holdsModule;
    private Boolean itemInfo;
}

