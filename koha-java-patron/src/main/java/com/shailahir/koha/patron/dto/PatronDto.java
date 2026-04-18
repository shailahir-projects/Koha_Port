package com.shailahir.koha.patron.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class PatronDto {
    private Long patronId;
    private String cardnumber;
    private String surname;
    private String firstname;
    private String middlename;
    private String othernames;
    private String title;
    private String initials;
    private String streetnumber;
    private String streettype;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private String email;
    private String phone;
    private String mobile;
    private String fax;
    private String emailpro;
    private String phonepro;
    private String B_streetnumber;
    private String B_streettype;
    private String B_address;
    private String B_address2;
    private String B_city;
    private String B_state;
    private String B_zipcode;
    private String B_country;
    private String B_email;
    private String B_phone;
    // dateofbirth maps to DB column dateofbirth; also aliased as datebirth for legacy compat
    private LocalDate datebirth;
    private LocalDate dateofbirth;
    private String branchcode;
    private String categorycode;
    private LocalDate dateenrolled;
    private LocalDate dateexpiry;
    private String userid;
    private String opacnote;
    private String contactnote;
    private String borrowernotes;
    private Boolean gonenoaddress;
    private Boolean lost;
    private LocalDate debarred;
    private String debarredcomment;
    private String contactname;
    private String contactfirstname;
    private String contacttitle;
    private String sex;
    private String password;
    private Long flags;
    private LocalDateTime lastseen;
    private Integer login_attempts;
    private String overdrive_auth_token;
    private String stripe_customer_id;
    private Boolean anonymized;
    private String lang;
    private Integer privacy;
    private Integer privacyGuarantor;
    private Boolean privacy_guarantor_checkouts;
    private Boolean privacy_guarantor_fines;
    private String sort1;
    private String sort2;
    private String altcontactfirstname;
    private String altcontactsurname;
    private String altcontactaddress1;
    private String altcontactaddress2;
    private String altcontactaddress3;
    private String altcontactstate;
    private String altcontactzipcode;
    private String altcontactcountry;
    private String altcontactphone;
    private String smsalertnumber;
    private String sms_provider_id;
    private String smsprovider;
    private String stripeCustomerId;
    private String overdrive_auth_tokenAlias;
    private Boolean primary_address_valid;
    private Boolean secondary_address_valid;
    private String overdriveAuthToken;
}

