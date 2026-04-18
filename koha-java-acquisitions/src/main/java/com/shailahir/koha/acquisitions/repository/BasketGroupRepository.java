package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketGroupDto;
import com.shailahir.koha.acquisitions.dto.BasketSummaryDto;
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
import java.util.List;
import java.util.Optional;

/**
 * JDBC repository for aqbasketgroups operations.
 * Mirrors GetBasketgroups, GetBasketgroup, NewBasketgroup, ModBasketgroup,
 * DelBasketgroup, CloseBasketgroup, ReOpenBasketgroup,
 * GetBasketsByBasketgroup, GetBasketsByBookseller, GetBasketGroupAsCSV.
 */
@Repository
@RequiredArgsConstructor
public class BasketGroupRepository {

    private final JdbcTemplate jdbc;

    // ── Row mappers ────────────────────────────────────────────────────────────

    private final RowMapper<BasketGroupDto> GROUP_MAPPER = (rs, rn) -> BasketGroupDto.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .booksellerid(rs.getObject("booksellerid") != null ? rs.getLong("booksellerid") : null)
            .deliveryplace(rs.getString("deliveryplace"))
            .freedeliveryplace(rs.getString("freedeliveryplace"))
            .deliverycomment(rs.getString("deliverycomment"))
            .billingplace(rs.getString("billingplace"))
            .closed(rs.getBoolean("closed"))
            .build();

    private final RowMapper<BasketSummaryDto> BASKET_MAPPER = (rs, rn) -> BasketSummaryDto.builder()
            .basketno(rs.getLong("basketno"))
            .basketname(rs.getString("basketname"))
            .booksellerid(rs.getObject("booksellerid") != null ? rs.getLong("booksellerid") : null)
            .basketgroupid(rs.getObject("basketgroupid") != null ? rs.getLong("basketgroupid") : null)
            .closedate(rs.getString("closedate"))
            .authorisedby(rs.getObject("authorisedby") != null ? rs.getLong("authorisedby") : null)
            .branch(rs.getString("branch"))
            .isStanding(rs.getBoolean("is_standing"))
            .build();

    // ── Basket group queries ───────────────────────────────────────────────────

    /** Mirrors GetBasketgroups($booksellerid). */
    public List<BasketGroupDto> findByBookseller(Long booksellerid) {
        return jdbc.query(
                "SELECT * FROM aqbasketgroups WHERE booksellerid = ? ORDER BY id DESC",
                GROUP_MAPPER, booksellerid);
    }

    /** Mirrors GetBasketgroup($basketgroupid). */
    public Optional<BasketGroupDto> findById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM aqbasketgroups WHERE id = ?", GROUP_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /** Mirrors NewBasketgroup(). Returns the new id. */
    public Long insert(BasketGroupDto dto) {
        String sql = """
                INSERT INTO aqbasketgroups
                    (name, booksellerid, deliveryplace, freedeliveryplace, deliverycomment, billingplace, closed)
                VALUES (?,?,?,?,?,?,?)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getName());
            ps.setObject(2, dto.getBooksellerid());
            ps.setString(3, dto.getDeliveryplace());
            ps.setString(4, dto.getFreedeliveryplace());
            ps.setString(5, dto.getDeliverycomment());
            ps.setString(6, dto.getBillingplace());
            ps.setInt(7, Boolean.TRUE.equals(dto.getClosed()) ? 1 : 0);
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("id")).longValue();
    }

    /** Mirrors ModBasketgroup(). */
    public void update(BasketGroupDto dto) {
        jdbc.update("""
                UPDATE aqbasketgroups
                   SET name = ?, deliveryplace = ?, freedeliveryplace = ?,
                       deliverycomment = ?, billingplace = ?, closed = ?
                 WHERE id = ?
                """,
                dto.getName(), dto.getDeliveryplace(), dto.getFreedeliveryplace(),
                dto.getDeliverycomment(), dto.getBillingplace(),
                Boolean.TRUE.equals(dto.getClosed()) ? 1 : 0,
                dto.getId());
    }

    /** Mirrors DelBasketgroup(). Unlinks all baskets before deleting. */
    public void delete(Long id) {
        jdbc.update("UPDATE aqbasket SET basketgroupid = NULL WHERE basketgroupid = ?", id);
        jdbc.update("DELETE FROM aqbasketgroups WHERE id = ?", id);
    }

    /** Mirrors CloseBasketgroup(). */
    public void close(Long id) {
        jdbc.update("UPDATE aqbasketgroups SET closed = 1 WHERE id = ?", id);
    }

    /** Mirrors ReOpenBasketgroup(). */
    public void reopen(Long id) {
        jdbc.update("UPDATE aqbasketgroups SET closed = 0 WHERE id = ?", id);
    }

    // ── Basket queries ─────────────────────────────────────────────────────────

    /** Mirrors GetBasketsByBasketgroup($basketgroupid). */
    public List<BasketSummaryDto> findBasketsByGroup(Long basketgroupid) {
        return jdbc.query(
                "SELECT * FROM aqbasket WHERE basketgroupid = ? ORDER BY basketno",
                BASKET_MAPPER, basketgroupid);
    }

    /**
     * Mirrors GetBasketsByBookseller($booksellerid).
     * Returns ALL baskets for this vendor (open and closed).
     */
    public List<BasketSummaryDto> findBasketsByBookseller(Long booksellerid) {
        return jdbc.query(
                "SELECT * FROM aqbasket WHERE booksellerid = ? ORDER BY basketno",
                BASKET_MAPPER, booksellerid);
    }

    /**
     * Assigns baskets in basketList to this group,
     * and clears any baskets previously in the group that are no longer listed.
     * Mirrors the basket assignment logic in ModBasketgroup / cud-attachbasket.
     */
    public void assignBaskets(Long basketgroupid, List<Long> basketList) {
        // Detach baskets currently in the group
        jdbc.update("UPDATE aqbasket SET basketgroupid = NULL WHERE basketgroupid = ?", basketgroupid);
        // Re-attach selected baskets
        if (basketList != null) {
            for (Long basketno : basketList) {
                jdbc.update("UPDATE aqbasket SET basketgroupid = ? WHERE basketno = ?", basketgroupid, basketno);
            }
        }
    }

    /**
     * Assign a single basket to a group (or clear if basketgroupid is null).
     * Mirrors ModBasket({ basketno => ..., basketgroupid => ... }).
     */
    public void assignBasket(Long basketno, Long basketgroupid) {
        jdbc.update("UPDATE aqbasket SET basketgroupid = ? WHERE basketno = ?", basketgroupid, basketno);
    }

    // ── Basket total ───────────────────────────────────────────────────────────

    /**
     * Computes the total cost for a basket.
     * Mirrors BasketTotal() in basketgroup.pl.
     * Uses ecost_tax_included (listincgst=true) or ecost_tax_excluded (listincgst=false).
     *
     * @param basketno     basket number
     * @param listIncGst   whether the vendor prices include GST
     */
    public BigDecimal basketTotal(Long basketno, boolean listIncGst) {
        String field = listIncGst ? "ecost_tax_included" : "ecost_tax_excluded";
        String sql = "SELECT COALESCE(SUM(" + field + " * quantity), 0) FROM aqorders "
                + "WHERE basketno = ? AND orderstatus != 'cancelled'";
        BigDecimal result = jdbc.queryForObject(sql, BigDecimal.class, basketno);
        return result != null ? result : BigDecimal.ZERO;
    }

    // ── Vendor info ────────────────────────────────────────────────────────────

    public Optional<String> findVendorName(Long booksellerid) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT name FROM aqbooksellers WHERE id = ?", String.class, booksellerid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean vendorListIncGst(Long booksellerid) {
        try {
            Integer val = jdbc.queryForObject(
                    "SELECT listincgst FROM aqbooksellers WHERE id = ?", Integer.class, booksellerid);
            return val != null && val == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    // ── CSV export ─────────────────────────────────────────────────────────────

    /**
     * Builds a CSV export of all orders in all baskets belonging to the group.
     * Mirrors GetBasketGroupAsCSV().
     */
    public String exportAsCsv(Long basketgroupid) {
        String sql = """
                SELECT o.ordernumber, b.title, b.author, bi.isbn,
                       o.quantity, o.listprice,
                       o.ecost_tax_excluded, o.ecost_tax_included,
                       o.tax_rate_on_ordering,
                       (o.ecost_tax_excluded * o.quantity) AS total_tax_excluded,
                       (o.ecost_tax_included * o.quantity) AS total_tax_included,
                       bk.basketno, bk.basketname
                  FROM aqbasket bk
                  JOIN aqorders o  ON o.basketno = bk.basketno
                  LEFT JOIN biblio b   ON b.biblionumber  = o.biblionumber
                  LEFT JOIN biblioitems bi ON bi.biblionumber = o.biblionumber
                 WHERE bk.basketgroupid = ?
                   AND o.orderstatus != 'cancelled'
                 ORDER BY bk.basketno, o.ordernumber
                """;

        List<java.util.Map<String, Object>> rows = jdbc.queryForList(sql, basketgroupid);

        StringBuilder sb = new StringBuilder();
        sb.append("basketno,basketname,ordernumber,title,author,isbn,")
          .append("quantity,listprice,ecost_tax_excluded,ecost_tax_included,")
          .append("total_tax_excluded,total_tax_included,tax_rate\n");

        for (java.util.Map<String, Object> row : rows) {
            sb.append(csv(row.get("basketno"))).append(',')
              .append(csv(row.get("basketname"))).append(',')
              .append(csv(row.get("ordernumber"))).append(',')
              .append(csv(row.get("title"))).append(',')
              .append(csv(row.get("author"))).append(',')
              .append(csv(row.get("isbn"))).append(',')
              .append(csv(row.get("quantity"))).append(',')
              .append(csv(row.get("listprice"))).append(',')
              .append(csv(row.get("ecost_tax_excluded"))).append(',')
              .append(csv(row.get("ecost_tax_included"))).append(',')
              .append(csv(row.get("total_tax_excluded"))).append(',')
              .append(csv(row.get("total_tax_included"))).append(',')
              .append(csv(row.get("tax_rate_on_ordering")))
              .append('\n');
        }
        return sb.toString();
    }

    private String csv(Object v) {
        if (v == null) return "";
        String s = v.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}

