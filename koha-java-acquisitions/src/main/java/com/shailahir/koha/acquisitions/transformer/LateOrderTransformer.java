package com.shailahir.koha.acquisitions.transformer;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.LateOrderDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Transformer for LateOrder/Claim-related DTOs.
 */
@Slf4j
@Component
public class LateOrderTransformer {

    public LateOrderDto fromRow(Map<String, Object> row) {
        log.debug("Entering fromRow - {}", row);
        if (row == null) return null;
        return LateOrderDto.builder()
                .ordernumber(toLong(row, "ordernumber"))
                .basketno(toLong(row, "basketno"))
                .biblionumber(toLong(row, "biblionumber"))
                .title(toString(row, "title"))
                .author(toString(row, "author"))
                .isbn(toString(row, "isbn"))
                .quantity(toInt(row, "quantity"))
                .quantityreceived(toInt(row, "quantityreceived"))
                .ecostTaxIncluded(toBigDecimal(row, "ecost"))
                .booksellerid(toLong(row, "booksellerid"))
                .vendorName(toString(row, "vendor"))
                .basketname(toString(row, "basketname"))
                .estimatedDeliveryDate(toLocalDate(row, "estimated_delivery_date"))
                .claimedDate(toLocalDate(row, "claimed_date"))
                .claimsCount(toInt(row, "claims_count"))
                .build();
    }

    private Long toLong(Map<String, Object> row, String key) {
        log.debug("Entering toLong - {}, {}", row, key);
        Object v = row.get(key); return v != null ? ((Number) v).longValue() : null;
    }
    private Integer toInt(Map<String, Object> row, String key) {
        log.debug("Entering toInt - {}, {}", row, key);
        Object v = row.get(key); return v != null ? ((Number) v).intValue() : null;
    }
    private String toString(Map<String, Object> row, String key) {
        log.debug("Entering toString - {}, {}", row, key);
        Object v = row.get(key); return v != null ? v.toString() : null;
    }
    private BigDecimal toBigDecimal(Map<String, Object> row, String key) {
        log.debug("Entering toBigDecimal - {}, {}", row, key);
        Object v = row.get(key);
        if (v == null) return null;
        if (v instanceof BigDecimal bd) return bd;
        return new BigDecimal(v.toString());
    }
    private LocalDate toLocalDate(Map<String, Object> row, String key) {
        log.debug("Entering toLocalDate - {}, {}", row, key);
        Object v = row.get(key);
        if (v == null) return null;
        if (v instanceof LocalDate ld) return ld;
        if (v instanceof java.sql.Date sd) return sd.toLocalDate();
        String s = v.toString();
        return s.length() >= 10 ? LocalDate.parse(s.substring(0, 10)) : null;
    }
}
