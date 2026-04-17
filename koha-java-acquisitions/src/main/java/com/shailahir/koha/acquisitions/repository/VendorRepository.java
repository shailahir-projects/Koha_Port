package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.BasketInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC repository for vendor (bookseller) queries needed by booksellers.pl.
 * Mirrors GetBasketsInfosByBookseller(), vendor counts, and basket group lookup.
 */
@Repository
@RequiredArgsConstructor
public class VendorRepository {

    private final JdbcTemplate jdbc;

    // ── Vendor info ────────────────────────────────────────────────────────────

    public Optional<Map<String, Object>> findVendorById(Long booksellerid) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqbooksellers WHERE id = ?", booksellerid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int countBaskets(Long booksellerid) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM aqbasket WHERE booksellerid = ?", Integer.class, booksellerid);
        return c != null ? c : 0;
    }

    public int countSubscriptions(Long booksellerid) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM subscription WHERE aqbooksellerid = ?", Integer.class, booksellerid);
        return c != null ? c : 0;
    }

    // ── GetBasketsInfosByBookseller ────────────────────────────────────────────

    /**
     * Returns enriched basket info for a vendor.
     * Mirrors GetBasketsInfosByBookseller($booksellerid, $allbaskets).
     *
     * When {@code allBaskets} is false only open (not-closed) baskets are returned.
     * Counts:
     * <ul>
     *   <li>total_items   = SUM(quantity) per basket</li>
     *   <li>total_biblios = COUNT(DISTINCT biblionumber) per basket</li>
     *   <li>expected_items = SUM(quantity - quantityreceived) for non-received orders</li>
     * </ul>
     */
    public List<BasketInfoDto> findBasketsInfoByVendor(Long booksellerid, boolean allBaskets) {
        String closedFilter = allBaskets ? "" : "AND b.closedate IS NULL";

        String sql = """
                SELECT b.basketno,
                       b.basketname,
                       b.basketgroupid,
                       bg.name                        AS basketgroupname,
                       b.authorisedby,
                       CONCAT(p.firstname,' ',p.surname) AS authorisedbyname,
                       b.closedate,
                       b.creationdate,
                       b.is_standing,
                       b.branch,
                       COALESCE(SUM(o.quantity), 0)   AS total_items,
                       COUNT(DISTINCT o.biblionumber)  AS total_biblios,
                       COALESCE(SUM(
                           CASE WHEN o.datereceived IS NULL
                                THEN o.quantity - COALESCE(o.quantityreceived, 0)
                                ELSE 0 END), 0)        AS expected_items,
                       COALESCE(SUM(o.ecost_tax_included * o.quantity), 0) AS total_cost
                  FROM aqbasket b
                  LEFT JOIN aqbasketgroups bg ON bg.id = b.basketgroupid
                  LEFT JOIN borrowers p       ON p.borrowernumber = b.authorisedby
                  LEFT JOIN aqorders o        ON o.basketno = b.basketno
                                             AND o.orderstatus != 'cancelled'
                 WHERE b.booksellerid = ?
                 """ + closedFilter + """
                 GROUP BY b.basketno, b.basketname, b.basketgroupid, bg.name,
                          b.authorisedby, authorisedbyname,
                          b.closedate, b.creationdate, b.is_standing, b.branch
                 ORDER BY b.basketno DESC
                """;

        RowMapper<BasketInfoDto> mapper = (rs, rn) -> BasketInfoDto.builder()
                .basketno(rs.getLong("basketno"))
                .basketname(rs.getString("basketname"))
                .basketgroupid(rs.getObject("basketgroupid") != null ? rs.getLong("basketgroupid") : null)
                .basketgroupname(rs.getString("basketgroupname"))
                .authorisedby(rs.getObject("authorisedby") != null ? rs.getLong("authorisedby") : null)
                .authorisedbyname(rs.getString("authorisedbyname"))
                .closedate(rs.getString("closedate"))
                .creationdate(rs.getString("creationdate"))
                .isStanding(rs.getBoolean("is_standing"))
                .branch(rs.getString("branch"))
                .totalItems(rs.getInt("total_items"))
                .totalBiblios(rs.getInt("total_biblios"))
                .expectedItems(rs.getInt("expected_items"))
                .totalCost(rs.getBigDecimal("total_cost"))
                .build();

        return jdbc.query(sql, mapper, booksellerid);
    }

    // ── Budget check ───────────────────────────────────────────────────────────

    /** Returns true if at least one active budget exists (mirrors has_budgets in booksellers.pl). */
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

