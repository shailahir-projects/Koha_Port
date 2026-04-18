package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for aqorders and aqbasket CRUD operations.
 * Ported from addorder.pl, basket.pl operations.
 */
public interface IOrderRepository {

    List<OrderDto> findAll(int offset, int limit);
    Optional<OrderDto> findById(Long ordernumber);
    Long insert(OrderDto order);
    void update(OrderDto order);
    int updateDeliveryDate(Long ordernumber, LocalDate date);
    void cancel(Long ordernumber);

    List<BasketDto> findAllBaskets(int offset, int limit);
    Optional<BasketDto> findBasketById(Long basketno);
    Long insertBasket(BasketDto dto);
    void setOrderUsers(Long ordernumber, List<Long> userIds);
    void linkItemToOrder(Long ordernumber, Long itemnumber);
}

