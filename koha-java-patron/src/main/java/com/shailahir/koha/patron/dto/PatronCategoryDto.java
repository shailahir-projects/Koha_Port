package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class PatronCategoryDto {
    private String categorycode;
    private String description;
    private Integer enrolmentperiod;
    private java.time.LocalDate enrolmentperioddate;
    private Integer passwordExpiry;
    private Integer upperagelimit;
    private Integer dateofbirthrequired;
    private Integer finenoticerequired;
    private Integer issuelimit;
    private java.math.BigDecimal reservefee;
    private Boolean hidelostitems;
    private String categorytype;
    private String category_type;
    private String BlockExpiredPatronOpacActions;
    private String defaultPrivacy;
    private Boolean default_privacy;
    private Integer maximumHolds;
    private Boolean excludeFromLocalHoldsPriority;
    private String smsprovider;
    private String branchcode;
    private Boolean overduenoticerequired;
    private Boolean checkprevcheckout;
    private String can_place_ill_in_opac;
    private String can_be_guarantee;
    private String reset_password;
    private String change_password;
    private Boolean min_password_length;
    private Boolean require_strong_password;
    private String exclude_from_local_holds_priority;
    private String enrolmentfee;
}

