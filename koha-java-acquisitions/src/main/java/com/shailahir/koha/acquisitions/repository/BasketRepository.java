package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketDto;
import com.shailahir.koha.acquisitions.dto.BasketOrderLineDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC repository for basket-centric operations needed by basket.pl.
 */
@Repository
@RequiredArgsConstructor
public class BasketRepository {

    private final JdbcTemplate jdbc;

    // ── Row mapper ─────────────────────────────────────────────────────────────

    private final RowMapper<BasketDto> BASKET_MAPPER = (rs, rn) -> BasketDto.builder()
            .basketno(rs.getLong("basketno"))
            .basketname(rs.getString("basketname"))
            .booksellerid(rs.getObject("booksellerid") != null ? rs.getLong("booksellerid") : null)
            .authorisedby(rs.getObject("authorisedby") != null ? rs.getLong("authorisedby") : null)
            .isStanding(rs.getBoolean("is_standing"))
            .createItems(rs.getString("create_items"))
            .closedate(rs.getString("closedate"))
            .creationdate(rs.getString("creationdate"))
            .note(rs.getString("note"))
            .booksellernote(rs.getString("booksellernote"))
            .contractnumber(rs.getObject("contractnumber") != null ? rs.getLong("contractnumber") : null)
            .basketgroupid(rs.getObject("basketgroupid") != null ? rs.getLong("basketgroupid") : null)
            .branch(rs.getString("branch"))
            .deliveryplace(rs.getString("deliveryplace"))
            .billingplace(rs.getString("billingplace"))
            .build();

    private final RowMapper<BasketOrderLineDto> ORDER_LINE_MAPPER = (rs, rn) -> {
        BigDecimal unitpriceTaxIncluded = rs.getBigDecimal("unitprice_tax_included");
        BigDecimal unitpriceTaxExcluded = rs.getBigDecimal("unitprice_tax_excluded");
        BigDecimal ecostTaxIncluded     = rs.getBigDecimal("ecost_tax_included");
        BigDecimal ecostTaxExcluded     = rs.getBigDecimal("ecost_tax_excluded");

        // Use actual unit price when set, else ecost (mirrors get_order_infos)
        BigDecimal costIncl = (unitpriceTaxIncluded != null && unitpriceTaxIncluded.compareTo(BigDecimal.ZERO) != 0)
                ? unitpriceTaxIncluded : (ecostTaxIncluded != null ? ecostTaxIncluded : BigDecimal.ZERO);
        BigDecimal costExcl = (unitpriceTaxExcluded != null && unitpriceTaxExcluded.compareTo(BigDecimal.ZERO) != 0)
                ? unitpriceTaxExcluded : (ecostTaxExcluded != null ? ecostTaxExcluded : BigDecimal.ZERO);

        int qty = rs.getObject("quantity") != null ? rs.getInt("quantity") : 0;

        return BasketOrderLineDto.builder()
                .ordernumber(rs.getLong("ordernumber"))
                .basketno(rs.getLong("basketno"))
                .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .isbn(rs.getString("isbn"))
                .quantity(qty)
                .quantityreceived(rs.getObject("quantityreceived") != null ? rs.getInt("quantityreceived") : 0)
                .budgetId(rs.getObject("budget_id") != null ? rs.getLong("budget_id") : null)
                .listprice(rs.getBigDecimal("listprice"))
                .ecostTaxExcluded(ecostTaxExcluded)
                .ecostTaxIncluded(ecostTaxIncluded)
                .unitpriceTaxExcluded(unitpriceTaxExcluded)
                .unitpriceTaxIncluded(unitpriceTaxIncluded)
                .totalTaxIncluded(costIncl.multiply(BigDecimal.valueOf(qty)))
                .totalTaxExcluded(costExcl.multiply(BigDecimal.valueOf(qty)))
                .taxRate(rs.getBigDecimal("tax_rate_on_ordering"))
                .taxValue(rs.getBigDecimal("tax_value_on_ordering"))
                .uncertainprice(rs.getBoolean("uncertainprice"))
                .orderstatus(rs.getString("orderstatus"))
                .invoiceid(rs.getObject("invoiceid") != null ? rs.getLong("invoiceid") : null)
                .estimatedDeliveryDate(rs.getObject("estimated_delivery_date", LocalDate.class))
                .build();
    };

    // ── Basket CRUD ────────────────────────────────────────────────────────────

    public Optional<BasketDto> findById(Long basketno) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM aqbasket WHERE basketno = ?", BASKET_MAPPER, basketno));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<BasketDto> findAll(int offset, int limit) {
        return jdbc.query(
                "SELECT * FROM aqbasket ORDER BY basketno DESC LIMIT ? OFFSET ?",
                BASKET_MAPPER, limit, offset);
    }

    public Long insert(BasketDto dto) {
        String sql = """
                INSERT INTO aqbasket
                    (basketname, booksellerid, authorisedby, is_standing, create_items,
                     note, booksellernote, contractnumber, branch, deliveryplace, billingplace, creationdate)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW())
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
            ps.setString(7, dto.getBooksellernote());
            ps.setObject(8, dto.getContractnumber());
            ps.setString(9, dto.getBranch());
            ps.setString(10, dto.getDeliveryplace());
            ps.setString(11, dto.getBillingplace());
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("basketno")).longValue();
    }

    /**
     * Close a basket — sets closedate to NOW().
     * Mirrors Koha::Acquisition::Baskets->find($basketno)->close.
     */
    public void close(Long basketno) {
        jdbc.update("UPDATE aqbasket SET closedate = NOW() WHERE basketno = ?", basketno);
    }

    /**
     * Reopen a basket — clears closedate.
     * Mirrors ReopenBasket().
     */
    public void reopen(Long basketno) {
        jdbc.update("UPDATE aqbasket SET closedate = NULL WHERE basketno = ?", basketno);
    }

    /**
     * Soft-delete basket: cancel all active orders and delete the basket row.
     * Mirrors the cud-delete op in basket.pl.
     */
    public void delete(Long basketno) {
        // Cancel non-cancelled orders
        jdbc.update("""
                UPDATE aqorders SET orderstatus = 'cancelled'
                 WHERE basketno = ? AND orderstatus != 'cancelled'
                """, basketno);
        jdbc.update("DELETE FROM aqbasket WHERE basketno = ?", basketno);
    }

    /**
     * Update basket branch.
     * Mirrors ModBasket({ basketno => ..., branch => ... }).
     */
    public void updateBranch(Long basketno, String branch) {
        jdbc.update("UPDATE aqbasket SET branch = ? WHERE basketno = ?", branch, basketno);
    }

    /**
     * Assign basket to a basket group.
     */
    public void updateBasketgroup(Long basketno, Long basketgroupid) {
        jdbc.update("UPDATE aqbasket SET basketgroupid = ? WHERE basketno = ?", basketgroupid, basketno);
    }

    /**
     * Hard-deletes a cancelled order that has no biblionumber.
     * Mirrors the cud-delete-order op in basket.pl.
     */
    public void deleteCancelledOrderWithoutBiblio(Long ordernumber) {
        jdbc.update(
                "DELETE FROM aqorders WHERE ordernumber = ? AND orderstatus = 'cancelled' AND biblionumber IS NULL",
                ordernumber);
    }

    // ── Basket users ───────────────────────────────────────────────────────────

    public List<Long> getBasketUsers(Long basketno) {
        return jdbc.queryForList(
                "SELECT borrowernumber FROM aqbasketusers WHERE basketno = ?", Long.class, basketno);
    }

    /**
     * Replace the full set of basket users.
     * Mirrors ModBasketUsers().
     */
    public void setBasketUsers(Long basketno, List<Long> userIds) {
        jdbc.update("DELETE FROM aqbasketusers WHERE basketno = ?", basketno);
        for (Long uid : userIds) {
            jdbc.update("INSERT INTO aqbasketusers (basketno, borrowernumber) VALUES (?,?)", basketno, uid);
        }
    }

    // ── Orders for basket ──────────────────────────────────────────────────────

    /**
     * Returns all active (non-cancelled) orders for the basket joined with biblio data.
     */
    public List<BasketOrderLineDto> findActiveOrders(Long basketno) {
        String sql = """
                SELECT o.*,
                       b.title, b.author,
                       bi.isbn
                  FROM aqorders o
                  LEFT JOIN biblio b  ON b.biblionumber  = o.biblionumber
                  LEFT JOIN biblioitems bi ON bi.biblionumber = o.biblionumber
                 WHERE o.basketno = ?
                   AND o.orderstatus != 'cancelled'
                 ORDER BY o.ordernumber
                """;
        return jdbc.query(sql, ORDER_LINE_MAPPER, basketno);
    }

    /**
     * Returns all cancelled orders for the basket.
     */
    public List<BasketOrderLineDto> findCancelledOrders(Long basketno) {
        String sql = """
                SELECT o.*,
                       b.title, b.author,
                       bi.isbn
                  FROM aqorders o
                  LEFT JOIN biblio b  ON b.biblionumber  = o.biblionumber
                  LEFT JOIN biblioitems bi ON bi.biblionumber = o.biblionumber
                 WHERE o.basketno = ?
                   AND o.orderstatus = 'cancelled'
                 ORDER BY o.ordernumber
                """;
        return jdbc.query(sql, ORDER_LINE_MAPPER, basketno);
    }

    // ── Budget/biblio enrichment helpers ──────────────────────────────────────

    public Optional<Map<String, Object>> findBudget(Long budgetId) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqbudgets WHERE budget_id = ?", budgetId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Map<String, Object>> findContract(Long contractnumber) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqcontract WHERE contractnumber = ?", contractnumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findVendorName(Long booksellerid) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT name FROM aqbooksellers WHERE id = ?", String.class, booksellerid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /** Returns vendor deliverytime (days) for estimating delivery date. */
    public Optional<Integer> findVendorDeliverytime(Long booksellerid) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT deliverytime FROM aqbooksellers WHERE id = ?",
                            Integer.class, booksellerid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /** Counts uncancelled orders for a biblio (to decide if safe to delete). */
    public int countUncancelledOrdersForBiblio(Long biblionumber) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM aqorders WHERE biblionumber = ? AND orderstatus != 'cancelled'",
                Integer.class, biblionumber);
        return c != null ? c : 0;
    }

    public int countItemsForBiblio(Long biblionumber) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM items WHERE biblionumber = ?", Integer.class, biblionumber);
        return c != null ? c : 0;
    }

    public int countItemsForOrder(Long ordernumber) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM aqorders_items WHERE ordernumber = ?", Integer.class, ordernumber);
        return c != null ? c : 0;
    }

    public int countSubscriptionsForBiblio(Long biblionumber) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM subscription WHERE biblionumber = ?", Integer.class, biblionumber);
        return c != null ? c : 0;
    }

    public int countHoldsForBiblio(Long biblionumber) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM reserves WHERE biblionumber = ? AND found IS NULL",
                Integer.class, biblionumber);
        return c != null ? c : 0;
    }

    public int countItemHoldsForOrder(Long biblionumber, Long ordernumber) {
        Integer c = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM reserves r
                  JOIN aqorders_items oi ON oi.itemnumber = r.itemnumber
                 WHERE r.biblionumber = ? AND oi.ordernumber = ?
                """,
                Integer.class, biblionumber, ordernumber);
        return c != null ? c : 0;
    }

    /** Returns the suggestion_id linked to a biblionumber, or null. */
    public Optional<Long> findSuggestionForBiblio(Long biblionumber) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT suggestionid FROM suggestions WHERE biblionumber = ? LIMIT 1",
                            Long.class, biblionumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Basket group ───────────────────────────────────────────────────────────

    /**
     * Creates a new basket group and returns its id.
     * Mirrors NewBasketgroup().
     */
    public Long createBasketGroup(String name, Long booksellerid, String deliveryplace,
                                  String billingplace, boolean closed) {
        String sql = """
                INSERT INTO aqbasketgroups (name, booksellerid, deliveryplace, billingplace, closed)
                VALUES (?,?,?,?,?)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setObject(2, booksellerid);
            ps.setString(3, deliveryplace);
            ps.setString(4, billingplace);
            ps.setInt(5, closed ? 1 : 0);
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("id")).longValue();
    }

    // ── Active currency ────────────────────────────────────────────────────────

    public Optional<String> getActiveCurrency() {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT currency FROM currency WHERE active = 1 LIMIT 1", String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Active budgets check ───────────────────────────────────────────────────

    public boolean hasActiveBudgets() {
        Integer c = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM aqbudgets b
                  JOIN aqbudgetperiods p ON p.budget_period_id = b.budget_period_id
                 WHERE p.budget_period_active = 1
                """, Integer.class);
        return c != null && c > 0;
    }
}

