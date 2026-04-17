package com.shailahir.koha.acquisitions.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for budget, biblio, suggestion and item operations used by OrderService.
 */
@Repository
@RequiredArgsConstructor
public class BudgetRepository {

    private final JdbcTemplate jdbc;

    // ── Budget ─────────────────────────────────────────────────────────────────

    public Optional<Map<String, Object>> findBudgetById(Long budgetId) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqbudgets WHERE budget_id = ?", budgetId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the total ecost * quantity already spent (received) for a budget.
     */
    public BigDecimal getBudgetSpent(Long budgetId) {
        String sql = """
                SELECT COALESCE(SUM(unitprice_tax_included * quantityreceived), 0)
                  FROM aqorders
                 WHERE budget_id = ?
                   AND orderstatus NOT IN ('cancelled')
                   AND datereceived IS NOT NULL
                """;
        return jdbc.queryForObject(sql, BigDecimal.class, budgetId);
    }

    /**
     * Returns total ecost * quantity ordered (not yet received) for a budget.
     */
    public BigDecimal getBudgetOrdered(Long budgetId) {
        String sql = """
                SELECT COALESCE(SUM(ecost_tax_included * quantity), 0)
                  FROM aqorders
                 WHERE budget_id = ?
                   AND orderstatus NOT IN ('cancelled')
                   AND datereceived IS NULL
                """;
        return jdbc.queryForObject(sql, BigDecimal.class, budgetId);
    }

    /**
     * Returns the ecost_tax_included and quantity for a specific order (used when modifying).
     */
    public Optional<BigDecimal> getOrderEcostTaxIncluded(Long ordernumber) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT ecost_tax_included * quantity FROM aqorders WHERE ordernumber = ?",
                            BigDecimal.class, ordernumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Active currency ────────────────────────────────────────────────────────

    public Optional<String> getActiveCurrencySymbol() {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT symbol FROM currency WHERE active = 1 LIMIT 1", String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Biblio / duplicate detection ───────────────────────────────────────────

    /**
     * Naive duplicate detection: look for a biblio with same ISBN or title+author.
     * Returns the biblionumber of any match, or empty.
     */
    public Optional<Long> findDuplicateBiblio(String isbn, String title, String author) {
        if (isbn != null && !isbn.isBlank()) {
            try {
                Long found = jdbc.queryForObject(
                        """
                        SELECT bi.biblionumber FROM biblioitems bi
                         WHERE bi.isbn LIKE ?
                         LIMIT 1
                        """,
                        Long.class, isbn.strip() + "%");
                if (found != null) return Optional.of(found);
            } catch (EmptyResultDataAccessException ignored) {
            }
        }
        if (title != null && !title.isBlank()) {
            try {
                Long found = jdbc.queryForObject(
                        """
                        SELECT b.biblionumber FROM biblio b
                         WHERE LOWER(b.title) = LOWER(?)
                           AND LOWER(COALESCE(b.author,'')) = LOWER(COALESCE(?,''))
                         LIMIT 1
                        """,
                        Long.class, title.strip(), author != null ? author.strip() : "");
                if (found != null) return Optional.of(found);
            } catch (EmptyResultDataAccessException ignored) {
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a minimal biblio record and returns the biblionumber.
     */
    public Long insertBiblio(String title, String author, String isbn, String ean,
                             String publishercode, String publicationyear,
                             String itemtype, String editionstatement, String series) {
        // Insert into biblio
        String biblioSql = """
                INSERT INTO biblio (title, author, seriestitle, copyrightdate, unititle, notes, serial, frameworkcode, datecreated)
                VALUES (?, ?, ?, ?, '', '', 0, '', NOW())
                """;
        KeyHolder biblioKh = new GeneratedKeyHolder();
        String seriesVal = series != null ? series : "";
        String yearVal   = publicationyear != null ? publicationyear : "";
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(biblioSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title != null ? title : "");
            ps.setString(2, author != null ? author : "");
            ps.setString(3, seriesVal);
            ps.setString(4, yearVal);
            return ps;
        }, biblioKh);
        Long biblionumber = ((Number) biblioKh.getKeys().get("biblionumber")).longValue();

        // Insert into biblioitems
        String bibitemSql = """
                INSERT INTO biblioitems (biblionumber, publishercode, publicationyear, isbn, ean,
                    itemtype, editionstatement, marc, marcxml)
                VALUES (?,?,?,?,?,?,?,'','')
                """;
        jdbc.update(bibitemSql,
                biblionumber,
                publishercode != null ? publishercode : "",
                publicationyear != null ? publicationyear : "",
                isbn != null ? isbn : "",
                ean != null ? ean : "",
                itemtype != null ? itemtype : "",
                editionstatement != null ? editionstatement : "");

        return biblionumber;
    }

    // ── Suggestion ─────────────────────────────────────────────────────────────

    public void updateSuggestionOrdered(Long suggestionid, Long biblionumber) {
        jdbc.update("""
                UPDATE suggestions SET STATUS = 'ORDERED', biblionumber = ?
                 WHERE suggestionid = ?
                """, biblionumber, suggestionid);
    }

    // ── Items ──────────────────────────────────────────────────────────────────

    /**
     * Inserts a minimal item row and returns the itemnumber.
     */
    public Long insertItem(Long biblionumber, Long biblioitemnumber,
                           String barcode, String homebranch, String holdingbranch,
                           String itype, String location, BigDecimal replacementprice,
                           String callnumber) {
        String sql = """
                INSERT INTO items (biblionumber, biblioitemnumber, barcode, homebranch, holdingbranch,
                    itype, location, replacementprice, itemcallnumber, notforloan, damaged, lost, withdrawn, onloan)
                VALUES (?,?,?,?,?,?,?,?,?,0,0,0,0,NULL)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, biblionumber);
            ps.setObject(2, biblioitemnumber);
            ps.setString(3, barcode);
            ps.setString(4, homebranch != null ? homebranch : "");
            ps.setString(5, holdingbranch != null ? holdingbranch : "");
            ps.setString(6, itype != null ? itype : "");
            ps.setString(7, location != null ? location : "");
            ps.setObject(8, replacementprice);
            ps.setString(9, callnumber != null ? callnumber : "");
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("itemnumber")).longValue();
    }

    /**
     * Returns the biblioitemnumber for a given biblionumber (first match).
     */
    public Optional<Long> getBiblioitemnumber(Long biblionumber) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT biblioitemnumber FROM biblioitems WHERE biblionumber = ? LIMIT 1",
                            Long.class, biblionumber));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Acquisition log ────────────────────────────────────────────────────────

    public void insertAcquisitionLog(String module, String action, Long objectNumber, String info) {
        jdbc.update("""
                INSERT INTO action_logs (timestamp, user, module, action, object, info)
                VALUES (NOW(), 0, ?, ?, ?, ?)
                """, module, action, objectNumber, info);
    }

    // ── Vendors ───────────────────────────────────────────────────────────────

    public List<Map<String, Object>> findAllVendors(int offset, int limit) {
        return jdbc.queryForList(
                "SELECT * FROM aqbooksellers ORDER BY id DESC LIMIT ? OFFSET ?", limit, offset);
    }

    public Optional<Map<String, Object>> findVendorById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForMap("SELECT * FROM aqbooksellers WHERE id = ?", id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ── Funds (read-only) ──────────────────────────────────────────────────────

    public List<Map<String, Object>> findAllFunds(int offset, int limit) {
        return jdbc.queryForList(
                "SELECT * FROM aqbudgets ORDER BY budget_id DESC LIMIT ? OFFSET ?", limit, offset);
    }
}

