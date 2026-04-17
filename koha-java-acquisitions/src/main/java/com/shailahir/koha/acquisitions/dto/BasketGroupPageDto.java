package com.shailahir.koha.acquisitions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for the basket group display page (op=display / op=add_form).
 * Mirrors the template variables set in displaybasketgroups() in basketgroup.pl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketGroupPageDto {

    /** All basket groups for this vendor */
    @JsonProperty("basket_groups")
    private List<BasketGroupDto> basketGroups;

    /**
     * Closed baskets not yet assigned to any group
     * (open baskets are excluded, mirrors the splice logic in displaybasketgroups())
     */
    @JsonProperty("unassigned_baskets")
    private List<BasketSummaryDto> unassignedBaskets;

    @JsonProperty("booksellerid")
    private Long booksellerid;

    @JsonProperty("booksellername")
    private String booksellername;
}

