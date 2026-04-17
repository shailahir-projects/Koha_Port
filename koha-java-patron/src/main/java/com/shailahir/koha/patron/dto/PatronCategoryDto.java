package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PatronCategoryDto {
    private String categorycode;
    private String description;
    private Integer enrolmentperiod;
    private Integer upperagelimit;
    private Integer dateofbirthrequired;
    private String category_type;
    private Boolean default_privacy;
    private String enrolmentfee;
    private Boolean overduenoticerequired;
    private String issuelimit;
    private String reservefee;
    private Boolean hidelostitems;
    private Integer BlockExpiredPatronOpacActions;
    private Boolean checkprevcheckout;
    private String can_place_ill_in_opac;
    private String can_be_guarantee;
    private String reset_password;
    private String change_password;
    private Boolean min_password_length;
    private Boolean require_strong_password;
    private String exclude_from_local_holds_priority;
}

