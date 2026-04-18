package com.shailahir.koha.acquisitions.transformer;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.OrderDto;
import com.shailahir.koha.acquisitions.dto.OrderHistoryDto;
import com.shailahir.koha.acquisitions.dto.OrderRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transformer for Order-related DTOs.
 *
 * <p>Centralises all mapping between {@link OrderRequest} (incoming form data),
 * {@link OrderDto} (internal / DB representation) and
 * {@link OrderHistoryDto} (read-only history view).
 *
 * <p>Mirrors the field-mapping Koha does in addorder.pl and histsearch.pl.
 */
@Slf4j
@Component
public class OrderTransformer {

    /**
     * Converts an {@link OrderRequest} (create/update form) to an {@link OrderDto}
     * suitable for persistence. Fills in sane defaults for nullable fields.
     *
     * @param req incoming request
     * @return dto ready for repository insert/update
     */
    public OrderDto fromRequest(OrderRequest req) {
        log.debug("Entering fromRequest - {}", req);
        if (req == null) return null;
        return OrderDto.builder()
                .ordernumber(req.getOrdernumber())
                .basketno(req.getBasketno())
                .biblionumber(req.getBiblionumber())
                .invoiceid(req.getInvoiceid())
                .budgetId(req.getBudgetId())
                .quantity(req.getQuantity())
                .currency(req.getCurrency())
                .listprice(req.getListprice())
                .uncertainprice(req.getUncertainprice())
                .taxRateOnOrdering(req.getTaxRate())
                .discount(req.getDiscount())
                .rrp(req.getRrp())
                .ecost(req.getEcost())
                .unitprice(req.getUnitprice() != null ? req.getUnitprice() : req.getEcost())
                .replacementprice(req.getReplacementprice())
                .orderInternalnote(req.getOrderInternalnote())
                .orderVendornote(req.getOrderVendornote())
                .sort1(req.getSort1())
                .sort2(req.getSort2())
                .subscriptionid(req.getSubscriptionid())
                .estimatedDeliveryDate(req.getEstimatedDeliveryDate())
                .build();
    }

    /**
     * Converts an {@link OrderDto} to an {@link OrderHistoryDto} read-only view.
     *
     * @param dto the full order dto
     * @return history dto (summary view)
     */
    public OrderHistoryDto toHistoryDto(OrderDto dto) {
        log.debug("Entering toHistoryDto - {}", dto);
        if (dto == null) return null;
        return OrderHistoryDto.builder()
                .ordernumber(dto.getOrdernumber())
                .basketno(dto.getBasketno())
                .biblionumber(dto.getBiblionumber())
                .quantity(dto.getQuantity())
                .quantityreceived(dto.getQuantityreceived())
                .ecostTaxExcluded(dto.getEcostTaxExcluded())
                .ecostTaxIncluded(dto.getEcostTaxIncluded())
                .listprice(dto.getListprice())
                .budgetId(dto.getBudgetId())
                .currency(dto.getCurrency())
                .orderstatus(dto.getOrderstatus())
                .basketname(dto.getBasketname())
                .build();
    }

    /**
     * Batch convert a list of {@link OrderDto} to {@link OrderHistoryDto}.
     */
    public List<OrderHistoryDto> toHistoryDtoList(List<OrderDto> orders) {
        log.debug("Entering toHistoryDtoList - {}", orders);
        if (orders == null) return Collections.emptyList();
        return orders.stream().map(this::toHistoryDto).collect(Collectors.toList());
    }
}

