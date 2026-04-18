package com.shailahir.koha.acquisitions.transformer;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.BasketGroupDto;
import com.shailahir.koha.acquisitions.dto.BasketHeaderRequest;
import com.shailahir.koha.acquisitions.dto.BasketSummaryDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transformer for Basket and BasketGroup-related DTOs.
 *
 * <p>Mirrors field-mapping done in basket.pl, basketheader.pl, basketgroup.pl.
 */
@Component
public class BasketTransformer {

    /**
     * Converts a {@link BasketHeaderRequest} to a {@link BasketDto} for persistence.
     */
    public BasketDto fromHeaderRequest(BasketHeaderRequest req) {
        if (req == null) return null;
        return BasketDto.builder()
                .basketno(req.getBasketno())
                .basketname(req.getBasketname())
                .booksellerid(req.getBooksellerid())
                .authorisedby(req.getAuthorisedby())
                .note(req.getNote())
                .isStanding(req.getIsStanding())
                .createItems(req.getCreateItems())
                .contractnumber(req.getContractnumber())
                .build();
    }

    /**
     * Projects a full {@link BasketDto} to a lightweight {@link BasketSummaryDto}.
     */
    public BasketSummaryDto toSummary(BasketDto dto) {
        if (dto == null) return null;
        return BasketSummaryDto.builder()
                .basketno(dto.getBasketno())
                .basketname(dto.getBasketname())
                .booksellerid(dto.getBooksellerid())
                .authorisedby(dto.getAuthorisedby())
                .closedate(dto.getClosedate())
                .isStanding(dto.getIsStanding())
                .build();
    }

    /**
     * Batch convert baskets to summary list.
     */
    public List<BasketSummaryDto> toSummaryList(List<BasketDto> baskets) {
        if (baskets == null) return Collections.emptyList();
        return baskets.stream().map(this::toSummary).collect(Collectors.toList());
    }

    /**
     * Maps a {@link BasketGroupDto} to a display label (for dropdowns, etc.).
     */
    public String toGroupLabel(BasketGroupDto group) {
        if (group == null) return "";
        return String.format("[%d] %s", group.getId(), group.getName());
    }
}

