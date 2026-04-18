package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketGroupDto;
import com.shailahir.koha.acquisitions.dto.BasketSummaryDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for aqbasketgroups operations.
 * Ported from basketgroup.pl.
 */
public interface IBasketGroupRepository {

    List<BasketGroupDto> findByBookseller(Long booksellerid);
    Optional<BasketGroupDto> findById(Long id);
    Long insert(BasketGroupDto dto);
    void update(BasketGroupDto dto);
    void delete(Long id);
    void close(Long id);
    void reopen(Long id);
    List<BasketSummaryDto> findBasketsByGroup(Long basketgroupid);
    List<BasketSummaryDto> findBasketsByBookseller(Long booksellerid);
    void assignBaskets(Long basketgroupid, List<Long> basketList);
    void assignBasket(Long basketno, Long basketgroupid);
    BigDecimal basketTotal(Long basketno, boolean listIncGst);
    Optional<String> findVendorName(Long booksellerid);
    boolean vendorListIncGst(Long booksellerid);
    String exportAsCsv(Long basketgroupid);
}

