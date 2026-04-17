package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbc;

    // ── Row mappers ────────────────────────────────────────────────────────────

    private final RowMapper<OrderDto> ORDER_ROW_MAPPER = (rs, rn) -> OrderDto.builder()
            .ordernumber(rs.getLong("ordernumber"))
            .basketno(rs.getLong("basketno"))
            .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
            .invoiceid(rs.getObject("invoiceid") != null ? rs.getLong("invoiceid") : null)
            .budgetId(rs.getLong("budget_id"))
            .quantity(rs.getObject("quantity") != null ? rs.getInt("quantity") : null)
            .quantityreceived(rs.getObject("quantityreceived") != null ? rs.getInt("quantityreceived") : null)
            .currency(rs.getString("currency"))
            .listprice(rs.getBigDecimal("listprice"))
            .uncertainprice(rs.getBoolean("uncertainprice"))
            .taxRateOnOrdering(rs.getBigDecimal("tax_rate_on_ordering"))
            .taxRateOnReceiving(rs.getBigDecimal("tax_rate_on_receiving"))
            .discount(rs.getBigDecimal("discount"))
            .rrp(rs.getBigDecimal("rrp"))
            .rrpTaxExcluded(rs.getBigDecimal("rrp_tax_excluded"))
            .rrpTaxIncluded(rs.getBigDecimal("rrp_tax_included"))
            .ecost(rs.getBigDecimal("ecost"))
            .ecostTaxExcluded(rs.getBigDecimal("ecost_tax_excluded"))
            .ecostTaxIncluded(rs.getBigDecimal("ecost_tax_included"))
            .unitprice(rs.getBigDecimal("unitprice"))
            .unitpriceTaxExcluded(rs.getBigDecimal("unitprice_tax_excluded"))
            .unitpriceTaxIncluded(rs.getBigDecimal("unitprice_tax_included"))
            .replacementprice(rs.getBigDecimal("replacementprice"))
            .orderInternalnote(rs.getString("order_internalnote"))
            .orderVendornote(rs.getString("order_vendornote"))
            .sort1(rs.getString("sort1"))
            .sort2(rs.getString("sort2"))
            .subscriptionid(rs.getObject("subscriptionid") != null ? rs.getLong("subscriptionid") : null)
            .estimatedDeliveryDate(rs.getObject("estimated_delivery_date", LocalDate.class))
            .orderstatus(rs.getString("orderstatus"))
            .build();

    private final RowMapper<BasketDto> BASKET_ROW_MAPPER = (rs, rn) -> BasketDto.builder()
            .basketno(rs.getLong("basketno"))
            .basketname(rs.getString("basketname"))
            .booksellerid(rs.getObject("booksellerid") != null ? rs.getLong("booksellerid") : null)
            .authorisedby(rs.getObject("authorisedby") != null ? rs.getLong("authorisedby") : null)
            .isStanding(rs.getBoolean("is_standing"))
            .createItems(rs.getString("create_items"))
            .closedate(rs.getString("closedate"))
            .note(rs.getString("note"))
            .contractnumber(rs.getObject("contractnumber") != null ? rs.getLong("contractnumber") : null)
            .build();

    // ── Order queries ──────────────────────────────────────────────────────────

    public List<OrderDto> findAll(int offset, int limit) {
        String sql = """
                SELECT o.*, b.basketname
                  FROM aqorders o
                  JOIN aqbasket b ON b.basketno = o.basketno
                 ORDER BY o.ordernumber DESC
                 LIMIT ? OFFSET ?
                """;
        return jdbc.query(sql, ORDER_ROW_MAPPER, limit, offset);
    }

    public Optional<OrderDto> findById(Long ordernumber) {
        try {
            String sql = """
                    SELECT o.*, b.basketname
                      FROM aqorders o
                      JOIN aqbasket b ON b.basketno = o.basketno
                     WHERE o.ordernumber = ?
                    """;
            return Optional.ofNullable(jdbc.queryForObject(sql, ORDER_ROW_MAPPER, ordernumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Insert a new order row and return the generated ordernumber.
     * Implements populate_with_prices_for_ordering: copies ecost → unitprice when unitprice is null.
     */
    public Long insert(OrderDto order) {
        // If unitprice not set, default to ecost (mirrors Koha's populate_with_prices_for_ordering)
        var unitprice = order.getUnitprice() != null ? order.getUnitprice() : order.getEcost();

        String sql = """
                INSERT INTO aqorders (
                    basketno, biblionumber, invoiceid, budget_id,
                    quantity, quantityreceived, currency, listprice, uncertainprice,
                    tax_rate_on_ordering, discount,
                    rrp, rrp_tax_excluded, rrp_tax_included,
                    ecost, ecost_tax_excluded, ecost_tax_included,
                    unitprice, unitprice_tax_excluded, unitprice_tax_included,
                    replacementprice,
                    order_internalnote, order_vendornote,
                    sort1, sort2, subscriptionid, estimated_delivery_date,
                    datecreated, orderstatus
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'new')
                """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, order.getBasketno());
            ps.setObject(2, order.getBiblionumber());
            ps.setObject(3, order.getInvoiceid());
            ps.setObject(4, order.getBudgetId());
            ps.setObject(5, order.getQuantity());
            ps.setObject(6, 0); // quantityreceived = 0 for new order
            ps.setObject(7, order.getCurrency());
            ps.setObject(8, order.getListprice());
            ps.setObject(9, order.getUncertainprice() != null && order.getUncertainprice() ? 1 : 0);
            ps.setObject(10, order.getTaxRateOnOrdering());
            ps.setObject(11, order.getDiscount());
            ps.setObject(12, order.getRrp());
            ps.setObject(13, order.getRrpTaxExcluded());
            ps.setObject(14, order.getRrpTaxIncluded());
            ps.setObject(15, order.getEcost());
            ps.setObject(16, order.getEcostTaxExcluded());
            ps.setObject(17, order.getEcostTaxIncluded());
            ps.setObject(18, unitprice);
            ps.setObject(19, order.getUnitpriceTaxExcluded());
            ps.setObject(20, order.getUnitpriceTaxIncluded());
            ps.setObject(21, order.getReplacementprice());
            ps.setObject(22, order.getOrderInternalnote());
            ps.setObject(23, order.getOrderVendornote());
            ps.setObject(24, order.getSort1());
            ps.setObject(25, order.getSort2());
            ps.setObject(26, order.getSubscriptionid());
            ps.setObject(27, order.getEstimatedDeliveryDate());
            return ps;
        }, kh);

        return ((Number) kh.getKeys().get("ordernumber")).longValue();
    }

    /**
     * Update an existing order row.
     */
    public void update(OrderDto order) {
        var unitprice = order.getUnitprice() != null ? order.getUnitprice() : order.getEcost();

        String sql = """
                UPDATE aqorders SET
                    biblionumber            = ?,
                    invoiceid               = ?,
                    budget_id               = ?,
                    quantity                = ?,
                    currency                = ?,
                    listprice               = ?,
                    uncertainprice          = ?,
                    tax_rate_on_ordering    = ?,
                    discount                = ?,
                    rrp                     = ?,
                    rrp_tax_excluded        = ?,
                    rrp_tax_included        = ?,
                    ecost                   = ?,
                    ecost_tax_excluded      = ?,
                    ecost_tax_included      = ?,
                    unitprice               = ?,
                    unitprice_tax_excluded  = ?,
                    unitprice_tax_included  = ?,
                    replacementprice        = ?,
                    order_internalnote      = ?,
                    order_vendornote        = ?,
                    sort1                   = ?,
                    sort2                   = ?,
                    subscriptionid          = ?,
                    estimated_delivery_date = ?
                WHERE ordernumber = ?
                """;
        jdbc.update(sql,
                order.getBiblionumber(),
                order.getInvoiceid(),
                order.getBudgetId(),
                order.getQuantity(),
                order.getCurrency(),
                order.getListprice(),
                order.getUncertainprice() != null && order.getUncertainprice() ? 1 : 0,
                order.getTaxRateOnOrdering(),
                order.getDiscount(),
                order.getRrp(),
                order.getRrpTaxExcluded(),
                order.getRrpTaxIncluded(),
                order.getEcost(),
                order.getEcostTaxExcluded(),
                order.getEcostTaxIncluded(),
                unitprice,
                order.getUnitpriceTaxExcluded(),
                order.getUnitpriceTaxIncluded(),
                order.getReplacementprice(),
                order.getOrderInternalnote(),
                order.getOrderVendornote(),
                order.getSort1(),
                order.getSort2(),
                order.getSubscriptionid(),
                order.getEstimatedDeliveryDate(),
                order.getOrdernumber()
        );
    }

    /**
     * Soft-delete: mark order as 'cancelled'.
     */
    public void cancel(Long ordernumber) {
        jdbc.update("UPDATE aqorders SET orderstatus = 'cancelled' WHERE ordernumber = ?", ordernumber);
    }

    // ── Basket queries ─────────────────────────────────────────────────────────

    public List<BasketDto> findAllBaskets(int offset, int limit) {
        return jdbc.query(
                "SELECT * FROM aqbasket ORDER BY basketno DESC LIMIT ? OFFSET ?",
                BASKET_ROW_MAPPER, limit, offset);
    }

    public Optional<BasketDto> findBasketById(Long basketno) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM aqbasket WHERE basketno = ?", BASKET_ROW_MAPPER, basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long insertBasket(BasketDto dto) {
        String sql = """
                INSERT INTO aqbasket (basketname, booksellerid, authorisedby, is_standing, create_items, note, contractnumber)
                VALUES (?,?,?,?,?,?,?)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getBasketname());
            ps.setObject(2, dto.getBooksellerid());
            ps.setObject(3, dto.getAuthorisedby());
            ps.setObject(4, dto.getIsStanding() != null && dto.getIsStanding() ? 1 : 0);
            ps.setString(5, dto.getCreateItems());
            ps.setString(6, dto.getNote());
            ps.setObject(7, dto.getContractnumber());
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("basketno")).longValue();
    }

    // ── Order-user relation ────────────────────────────────────────────────────

    public void setOrderUsers(Long ordernumber, List<Long> userIds) {
        jdbc.update("DELETE FROM aqorder_users WHERE ordernumber = ?", ordernumber);
        for (Long uid : userIds) {
            jdbc.update("INSERT INTO aqorder_users (ordernumber, borrowernumber) VALUES (?,?)", ordernumber, uid);
        }
    }

    // ── Item linkage ───────────────────────────────────────────────────────────

    public void linkItemToOrder(Long ordernumber, Long itemnumber) {
        jdbc.update(
                "INSERT INTO aqorders_items (ordernumber, itemnumber) VALUES (?,?) ON CONFLICT DO NOTHING",
                ordernumber, itemnumber);
    }
}

