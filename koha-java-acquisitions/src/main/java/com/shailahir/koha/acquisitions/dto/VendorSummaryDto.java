package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Vendor summary row – mirrors vendors.pl. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorSummaryDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("url")
    private String url;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("accountnumber")
    private String accountnumber;

    @JsonProperty("address1")
    private String address1;

    @JsonProperty("postal")
    private String postal;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("discount")
    private BigDecimal discount;

    @JsonProperty("basket_count")
    private Long basketCount;

    @JsonProperty("subscription_count")
    private Long subscriptionCount;

    @JsonProperty("contracts_count")
    private Long contractsCount;
}

