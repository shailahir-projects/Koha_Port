package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.OrderHistoryDto;
import com.shailahir.koha.acquisitions.dto.OrderHistoryFilter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC repository for order history search and order duplication.
 * Mirrors GetHistory() from C4::Acquisition and
 * Koha::Acquisition::Order->duplicate_to().
 */
@Repository
@RequiredArgsConstructor
public class DuplicateOrderRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<OrderHistoryDto> HISTORY_MAPPER = (rs, rn) -> OrderHistoryDto.builder()
            .ordernumber(rs.getLong("ordernumber"))
            .basketno(rs.getLong("basketno"))
            .basketname(rs.getString("basketname"))
            .basketgroupid(rs.getObject("basketgroupid") != null ? rs.getLong("basketgroupid") : null)
            .basketgroupname(rs.getString("basketgroupname"))
            .biblionumber(rs.getObject("biblionumber") != null ? rs.getLong("biblionumber") : null)
            .title(rs.getString("title"))
            .author(rs.getString("author"))
            .isbn(rs.getString("isbn"))
            .booksellerid(rs.getObject("booksellerid") != null ? rs.getLong("booksellerid") : null)
            .name(rs.getString("vendorname"))
            .quantity(rs.getObject("quantity") != null ? rs.getInt("quantity") : null)
            .quantityreceived(rs.getObject("quantityreceived") != null ? rs.getInt("quantityreceived") : null)
            .ecostTaxExcluded(rs.getBigDecimal("ecost_tax_excluded"))
            .ecostTaxIncluded(rs.getBigDecimal("ecost_tax_included"))
            .listprice(rs.getBigDecimal("listprice"))
            .budgetId(rs.getObject("budget_id") != null ? rs.getLong("budget_id") : null)
            .budgetName(rs.getString("budget_name"))
            .currency(rs.getString("currency"))
            .orderstatus(rs.getString("orderstatus"))
            .datereceived(rs.getObject("datereceived", LocalDate.class))
            .datecancellationprinted(rs.getObject("datecancellationprinted", LocalDate.class))
            .orderInternalnote(rs.getString("order_internalnote"))
            .orderVendornote(rs.getString("order_vendornote"))
            .sort1(rs.getString("sort1"))
            .sort2(rs.getString("sort2"))
            .invoicenumber(rs.getString("invoicenumber"))
            .dateplaced(rs.getObject("creationdate", LocalDate.class))
            .build();

    // ── GetHistory ─────────────────────────────────────────────────────────────

    /**
     * Searches order history with optional filters.
     * Mirrors C4::Acquisition::GetHistory().
     */
    public List<OrderHistoryDto> getHistory(OrderHistoryFilter f) {
        StringBuilder sql = new StringBuilder("""
                SELECT o.ordernumber, o.basketno, o.biblionumber,
                       o.quantity, o.quantityreceived,
                       o.ecost_tax_excluded, o.ecost_tax_included, o.listprice,
                       o.budget_id, o.currency, o.orderstatus,
                       o.datereceived, o.datecancellationprinted,
                       o.order_internalnote, o.order_vendornote,
                       o.sort1, o.sort2,
                       b.basketname, b.basketgroupid, b.booksellerid,
                       bg.name AS basketgroupname,
                       bib.title, bib.author,
                       bi.isbn,
                       v.name AS vendorname,
                       aq.budget_name,
                       inv.invoicenumber,
                       b.creationdate
                  FROM aqorders o
                  JOIN aqbasket b   ON b.basketno = o.basketno
                  LEFT JOIN aqbasketgroups bg ON bg.id = b.basketgroupid
                  LEFT JOIN biblio bib        ON bib.biblionumber = o.biblionumber
                  LEFT JOIN biblioitems bi    ON bi.biblionumber  = o.biblionumber
                  LEFT JOIN aqbooksellers v   ON v.id = b.booksellerid
                  LEFT JOIN aqbudgets aq      ON aq.budget_id = o.budget_id
                  LEFT JOIN aqinvoices inv    ON inv.invoiceid = o.invoiceid
                 WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        // Include cancelled orders only when explicitly requested
        boolean includeCancelled = "any".equalsIgnoreCase(f.getOrderstatus());
        if (!includeCancelled) {
            sql.append(" AND o.orderstatus != 'cancelled'");
        }
        if (f.getOrderstatus() != null && !"any".equalsIgnoreCase(f.getOrderstatus())
                && !f.getOrderstatus().isBlank()) {
            sql.append(" AND o.orderstatus = ?"); params.add(f.getOrderstatus());
        }
        if (notBlank(f.getBasket())) {
            sql.append(" AND b.basketname LIKE ?"); params.add("%" + f.getBasket() + "%");
        }
        if (notBlank(f.getTitle())) {
            sql.append(" AND bib.title LIKE ?"); params.add("%" + f.getTitle() + "%");
        }
        if (notBlank(f.getAuthor())) {
            sql.append(" AND bib.author LIKE ?"); params.add("%" + f.getAuthor() + "%");
        }
        if (notBlank(f.getIsbn())) {
            sql.append(" AND bi.isbn LIKE ?"); params.add("%" + f.getIsbn() + "%");
        }
        if (notBlank(f.getName())) {
            sql.append(" AND v.name LIKE ?"); params.add("%" + f.getName() + "%");
        }
        if (notBlank(f.getEan())) {
            sql.append(" AND bi.ean LIKE ?"); params.add("%" + f.getEan() + "%");
        }
        if (notBlank(f.getBasketgroupname())) {
            sql.append(" AND bg.name LIKE ?"); params.add("%" + f.getBasketgroupname() + "%");
        }
        if (notBlank(f.getBooksellerinvoicenumber())) {
            sql.append(" AND inv.invoicenumber LIKE ?");
            params.add("%" + f.getBooksellerinvoicenumber() + "%");
        }
        if (notBlank(f.getBudget())) {
            sql.append(" AND aq.budget_name LIKE ?"); params.add("%" + f.getBudget() + "%");
        }
        if (notBlank(f.getOrdernumber())) {
            sql.append(" AND o.ordernumber = ?"); params.add(Long.parseLong(f.getOrdernumber().trim()));
        }
        if (f.getCreatedBy() != null && !f.getCreatedBy().isEmpty()) {
            String placeholders = String.join(",", f.getCreatedBy().stream().map(x -> "?").toList());
            sql.append(" AND b.authorisedby IN (").append(placeholders).append(")");
            params.addAll(f.getCreatedBy());
        }
        if (notBlank(f.getFromPlacedOn())) {
            sql.append(" AND b.creationdate >= ?"); params.add(LocalDate.parse(f.getFromPlacedOn().substring(0, 10)));
        }
        if (notBlank(f.getToPlacedOn())) {
            sql.append(" AND b.creationdate <= ?"); params.add(LocalDate.parse(f.getToPlacedOn().substring(0, 10)));
        }

        sql.append(" ORDER BY o.ordernumber DESC");
        return jdbc.query(sql.toString(), HISTORY_MAPPER, params.toArray());
    }

    /**
     * Returns history rows for a specific set of order numbers.
     * Mirrors GetHistory(ordernumbers => \@ordernumbers).
     */
    public List<OrderHistoryDto> getHistoryByOrdernumbers(List<Long> ordernumbers) {
        if (ordernumbers == null || ordernumbers.isEmpty()) return List.of();
        String placeholders = String.join(",", ordernumbers.stream().map(x -> "?").toList());
        String sql = """
                SELECT o.ordernumber, o.basketno, o.biblionumber,
                       o.quantity, o.quantityreceived,
                       o.ecost_tax_excluded, o.ecost_tax_included, o.listprice,
                       o.budget_id, o.currency, o.orderstatus,
                       o.datereceived, o.datecancellationprinted,
                       o.order_internalnote, o.order_vendornote,
                       o.sort1, o.sort2,
                       b.basketname, b.basketgroupid, b.booksellerid,
                       bg.name AS basketgroupname,
                       bib.title, bib.author,
                       bi.isbn,
                       v.name AS vendorname,
                       aq.budget_name,
                       inv.invoicenumber,
                       b.creationdate
                  FROM aqorders o
                  JOIN aqbasket b   ON b.basketno = o.basketno
                  LEFT JOIN aqbasketgroups bg ON bg.id = b.basketgroupid
                  LEFT JOIN biblio bib        ON bib.biblionumber = o.biblionumber
                  LEFT JOIN biblioitems bi    ON bi.biblionumber  = o.biblionumber
                  LEFT JOIN aqbooksellers v   ON v.id = b.booksellerid
                  LEFT JOIN aqbudgets aq      ON aq.budget_id = o.budget_id
                  LEFT JOIN aqinvoices inv    ON inv.invoiceid = o.invoiceid
                 WHERE o.ordernumber IN (
                """ + placeholders + ")";
        return jdbc.query(sql, HISTORY_MAPPER, ordernumbers.toArray());
    }

    // ── duplicate_to ───────────────────────────────────────────────────────────

    /**
     * Duplicates a single order into the target basket, applying default overrides
     * for fields not in copyExistingValue.
     * Mirrors Koha::Acquisition::Order->duplicate_to($basket, $default_values).
     *
     * @param ordernumber     source order to copy
     * @param targetBasketno  destination basket
     * @param defaults        field → value map of overrides (null values are skipped)
     * @param copyFields      field names that should be taken from the original order
     * @return new ordernumber
     */
    public Long duplicateOrder(Long ordernumber, Long targetBasketno,
                               Map<String, Object> defaults, List<String> copyFields) {
        Map<String, Object> orig = jdbc.queryForMap(
                "SELECT * FROM aqorders WHERE ordernumber = ?", ordernumber);

        // Resolve field values: use original when in copyFields, else use default
        String currency        = resolve("currency",        orig, defaults, copyFields);
        Object budgetId        = resolveObj("budget_id",    orig, defaults, copyFields);
        String internalnote    = resolve("order_internalnote", orig, defaults, copyFields);
        String vendornote      = resolve("order_vendornote",   orig, defaults, copyFields);
        String sort1           = resolve("sort1",           orig, defaults, copyFields);
        String sort2           = resolve("sort2",           orig, defaults, copyFields);

        String sql = """
                INSERT INTO aqorders
                    (basketno, biblionumber, quantity, listprice, ecost,
                     ecost_tax_excluded, ecost_tax_included,
                     rrp, rrp_tax_excluded, rrp_tax_included,
                     replacementprice, tax_rate_on_ordering,
                     uncertainprice, budget_id, currency,
                     order_internalnote, order_vendornote, sort1, sort2,
                     orderstatus)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'new')
                """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1,  targetBasketno);
            ps.setObject(2,  orig.get("biblionumber"));
            ps.setObject(3,  orig.get("quantity"));
            ps.setObject(4,  orig.get("listprice"));
            ps.setObject(5,  orig.get("ecost"));
            ps.setObject(6,  orig.get("ecost_tax_excluded"));
            ps.setObject(7,  orig.get("ecost_tax_included"));
            ps.setObject(8,  orig.get("rrp"));
            ps.setObject(9,  orig.get("rrp_tax_excluded"));
            ps.setObject(10, orig.get("rrp_tax_included"));
            ps.setObject(11, orig.get("replacementprice"));
            ps.setObject(12, orig.get("tax_rate_on_ordering"));
            ps.setObject(13, orig.get("uncertainprice"));
            ps.setObject(14, budgetId);
            ps.setObject(15, currency);
            ps.setObject(16, internalnote);
            ps.setObject(17, vendornote);
            ps.setObject(18, sort1);
            ps.setObject(19, sort2);
            return ps;
        }, kh);

        return ((Number) kh.getKeys().get("ordernumber")).longValue();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private String resolve(String field, Map<String, Object> orig,
                           Map<String, Object> defaults, List<String> copyFields) {
        if (copyFields != null && copyFields.contains(field)) {
            Object v = orig.get(field);
            return v != null ? v.toString() : null;
        }
        Object v = defaults != null ? defaults.get(field) : null;
        return v != null ? v.toString() : (orig.get(field) != null ? orig.get(field).toString() : null);
    }

    private Object resolveObj(String field, Map<String, Object> orig,
                              Map<String, Object> defaults, List<String> copyFields) {
        if (copyFields != null && copyFields.contains(field)) return orig.get(field);
        Object v = defaults != null ? defaults.get(field) : null;
        return v != null ? v : orig.get(field);
    }
}

