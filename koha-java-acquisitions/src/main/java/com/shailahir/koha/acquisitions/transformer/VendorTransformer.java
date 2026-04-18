package com.shailahir.koha.acquisitions.transformer;

import com.shailahir.koha.acquisitions.dto.VendorBasketsDto;
import com.shailahir.koha.acquisitions.dto.VendorSummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Transformer for Vendor/Bookseller-related DTOs.
 */
@Component
public class VendorTransformer {

    public VendorSummaryDto toSummary(Map<String, Object> row,
                                       long basketCount, long subscCount, long contractCount) {
        if (row == null) return null;
        return VendorSummaryDto.builder()
                .id(toLong(row, "id"))
                .name(toString(row, "name"))
                .active(toBoolean(row, "active"))
                .url(toString(row, "url"))
                .phone(toString(row, "phone"))
                .accountnumber(toString(row, "accountnumber"))
                .address1(toString(row, "address1"))
                .postal(toString(row, "postal"))
                .currency(toString(row, "currency"))
                .discount(toBigDecimal(row, "discount"))
                .basketCount(basketCount)
                .subscriptionCount(subscCount)
                .contractsCount(contractCount)
                .build();
    }

    public VendorBasketsDto toVendorBasketsDto(Map<String, Object> row) {
        if (row == null) return null;
        return VendorBasketsDto.builder()
                .booksellerid(toLong(row, "id"))
                .name(toString(row, "name"))
                .active(toBoolean(row, "active"))
                .build();
    }

    private Long toLong(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? ((Number) v).longValue() : null;
    }
    private String toString(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString() : null;
    }
    private Boolean toBoolean(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v == null) return false;
        if (v instanceof Boolean b) return b;
        return ((Number) v).intValue() != 0;
    }
    private BigDecimal toBigDecimal(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v == null) return null;
        if (v instanceof BigDecimal bd) return bd;
        return new BigDecimal(v.toString());
    }
}
