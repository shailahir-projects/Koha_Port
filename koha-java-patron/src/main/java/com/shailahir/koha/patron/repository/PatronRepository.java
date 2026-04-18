package com.shailahir.koha.patron.repository;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.patron.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

/**
 * Repository for patron (borrowers) data access.
 * Mirrors: members/moremember.pl, members/memberentry.pl, members/deletemem.pl
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PatronRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<PatronDto> PATRON_ROW_MAPPER = (rs, rowNum) -> {
        log.debug("Entering = - {}, {}", rs, rowNum);
        PatronDto dto = new PatronDto();
        dto.setPatronId(rs.getLong("borrowernumber"));
        dto.setCardnumber(rs.getString("cardnumber"));
        dto.setSurname(rs.getString("surname"));
        dto.setFirstname(rs.getString("firstname"));
        dto.setMiddlename(rs.getString("middle"));
        dto.setTitle(rs.getString("title"));
        dto.setOthernames(rs.getString("othernames"));
        dto.setInitials(rs.getString("initials"));
        dto.setStreetnumber(rs.getString("streetnumber"));
        dto.setStreettype(rs.getString("streettype"));
        dto.setAddress(rs.getString("address"));
        dto.setAddress2(rs.getString("address2"));
        dto.setCity(rs.getString("city"));
        dto.setState(rs.getString("state"));
        dto.setZipcode(rs.getString("zipcode"));
        dto.setCountry(rs.getString("country"));
        dto.setEmail(rs.getString("email"));
        dto.setPhone(rs.getString("phone"));
        dto.setMobile(rs.getString("mobile"));
        dto.setFax(rs.getString("fax"));
        dto.setEmailpro(rs.getString("emailpro"));
        dto.setPhonepro(rs.getString("phonepro"));
        dto.setB_address(rs.getString("B_address"));
        dto.setB_city(rs.getString("B_city"));
        dto.setB_state(rs.getString("B_state"));
        dto.setB_zipcode(rs.getString("B_zipcode"));
        dto.setB_country(rs.getString("B_country"));
        dto.setB_email(rs.getString("B_email"));
        dto.setB_phone(rs.getString("B_phone"));
        dto.setDatebirth(rs.getObject("dateofbirth", LocalDate.class));
        dto.setDateofbirth(rs.getObject("dateofbirth", LocalDate.class));
        dto.setBranchcode(rs.getString("branchcode"));
        dto.setCategorycode(rs.getString("categorycode"));
        dto.setDateenrolled(rs.getObject("dateenrolled", LocalDate.class));
        dto.setDateexpiry(rs.getObject("dateexpiry", LocalDate.class));
        dto.setGonenoaddress(rs.getObject("gonenoaddress", Boolean.class));
        dto.setLost(rs.getObject("lost", Boolean.class));
        dto.setDebarred(rs.getObject("debarred", LocalDate.class));
        dto.setDebarredcomment(rs.getString("debarredcomment"));
        dto.setContactname(rs.getString("contactname"));
        dto.setContactfirstname(rs.getString("contactfirstname"));
        dto.setContacttitle(rs.getString("contacttitle"));
        dto.setSex(rs.getString("sex"));
        dto.setFlags(rs.getObject("flags", Long.class));
        dto.setUserid(rs.getString("userid"));
        dto.setOpacnote(rs.getString("opacnote"));
        dto.setContactnote(rs.getString("contactnote"));
        dto.setSort1(rs.getString("sort1"));
        dto.setSort2(rs.getString("sort2"));
        dto.setAltcontactfirstname(rs.getString("altcontactfirstname"));
        dto.setAltcontactsurname(rs.getString("altcontactsurname"));
        dto.setAltcontactaddress1(rs.getString("altcontactaddress1"));
        dto.setAltcontactaddress2(rs.getString("altcontactaddress2"));
        dto.setAltcontactaddress3(rs.getString("altcontactaddress3"));
        dto.setAltcontactzipcode(rs.getString("altcontactzipcode"));
        dto.setAltcontactcountry(rs.getString("altcontactcountry"));
        dto.setAltcontactphone(rs.getString("altcontactphone"));
        dto.setSmsalertnumber(rs.getString("smsalertnumber"));
        dto.setPrivacy(rs.getInt("privacy"));
        dto.setLang(rs.getString("lang"));
        dto.setLogin_attempts(rs.getInt("login_attempts"));
        return dto;
    };

    public Page<PatronDto> findAll(String query, Pageable pageable) {
        log.debug("Entering findAll - {}, {}", query, pageable);
        String whereClause = "";
        Object[] params;
        if (query != null && !query.isBlank()) {
            whereClause = " WHERE (surname ILIKE ? OR firstname ILIKE ? OR cardnumber ILIKE ? OR userid ILIKE ?)";
            String like = "%" + query + "%";
            params = new Object[]{like, like, like, like};
        } else {
            params = new Object[]{};
        }
        String countSql = "SELECT COUNT(*) FROM borrowers" + whereClause;
        int total = jdbc.queryForObject(countSql, Integer.class, params);

        String sql = "SELECT * FROM borrowers" + whereClause + " ORDER BY borrowernumber LIMIT ? OFFSET ?";
        Object[] pageParams = appendPageParams(params, pageable);
        List<PatronDto> list = jdbc.query(sql, PATRON_ROW_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<PatronDto> findById(Long patronId) {
        log.debug("Entering findById - {}", patronId);
        try {
            PatronDto dto = jdbc.queryForObject("SELECT * FROM borrowers WHERE borrowernumber = ?", PATRON_ROW_MAPPER, patronId);
            return Optional.ofNullable(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public PatronDto insert(PatronDto dto) {
        log.debug("Entering insert - {}", dto);
        String sql = """
            INSERT INTO borrowers (cardnumber, surname, firstname, middle, title, othernames, initials,
                streetnumber, streettype, address, address2, city, state, zipcode, country,
                email, phone, mobile, fax, emailpro, phonepro,
                B_address, B_city, B_state, B_zipcode, B_country, B_email, B_phone,
                dateofbirth, branchcode, categorycode, dateenrolled, dateexpiry,
                sex, userid, opacnote, contactnote, sort1, sort2, lang, privacy)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getCardnumber());
            ps.setString(2, dto.getSurname());
            ps.setString(3, dto.getFirstname());
            ps.setString(4, dto.getMiddlename());
            ps.setString(5, dto.getTitle());
            ps.setString(6, dto.getOthernames());
            ps.setString(7, dto.getInitials());
            ps.setString(8, dto.getStreetnumber());
            ps.setString(9, dto.getStreettype());
            ps.setString(10, dto.getAddress());
            ps.setString(11, dto.getAddress2());
            ps.setString(12, dto.getCity());
            ps.setString(13, dto.getState());
            ps.setString(14, dto.getZipcode());
            ps.setString(15, dto.getCountry());
            ps.setString(16, dto.getEmail());
            ps.setString(17, dto.getPhone());
            ps.setString(18, dto.getMobile());
            ps.setString(19, dto.getFax());
            ps.setString(20, dto.getEmailpro());
            ps.setString(21, dto.getPhonepro());
            ps.setString(22, dto.getB_address());
            ps.setString(23, dto.getB_city());
            ps.setString(24, dto.getB_state());
            ps.setString(25, dto.getB_zipcode());
            ps.setString(26, dto.getB_country());
            ps.setString(27, dto.getB_email());
            ps.setString(28, dto.getB_phone());
            ps.setObject(29, dto.getDatebirth());
            ps.setString(30, dto.getBranchcode());
            ps.setString(31, dto.getCategorycode());
            ps.setObject(32, dto.getDateenrolled());
            ps.setObject(33, dto.getDateexpiry());
            ps.setString(34, dto.getSex());
            ps.setString(35, dto.getUserid());
            ps.setString(36, dto.getOpacnote());
            ps.setString(37, dto.getContactnote());
            ps.setString(38, dto.getSort1());
            ps.setString(39, dto.getSort2());
            ps.setString(40, dto.getLang());
            ps.setInt(41, dto.getPrivacy() != null ? dto.getPrivacy() : 1);
            return ps;
        }, kh);
        dto.setPatronId(((Number) kh.getKeys().get("borrowernumber")).longValue());
        return dto;
    }

    public PatronDto update(Long patronId, PatronDto dto) {
        log.debug("Entering update - {}, {}", patronId, dto);
        jdbc.update("""
            UPDATE borrowers SET cardnumber=?, surname=?, firstname=?, middle=?, title=?,
                email=?, phone=?, mobile=?, branchcode=?, categorycode=?,
                dateexpiry=?, sex=?, userid=?, opacnote=?, contactnote=?,
                sort1=?, sort2=?, lang=?, privacy=?
            WHERE borrowernumber=?
            """,
            dto.getCardnumber(), dto.getSurname(), dto.getFirstname(), dto.getMiddlename(), dto.getTitle(),
            dto.getEmail(), dto.getPhone(), dto.getMobile(), dto.getBranchcode(), dto.getCategorycode(),
            dto.getDateexpiry(), dto.getSex(), dto.getUserid(), dto.getOpacnote(), dto.getContactnote(),
            dto.getSort1(), dto.getSort2(), dto.getLang(),
            dto.getPrivacy() != null ? dto.getPrivacy() : 1,
            patronId);
        dto.setPatronId(patronId);
        return dto;
    }

    public void delete(Long patronId) {
        log.debug("Entering delete - {}", patronId);
        jdbc.update("DELETE FROM borrowers WHERE borrowernumber = ?", patronId);
    }

    public boolean exists(Long patronId) {
        log.debug("Entering exists - {}", patronId);
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM borrowers WHERE borrowernumber = ?", Integer.class, patronId);
        return count != null && count > 0;
    }

    public void updatePassword(Long patronId, String hashedPassword) {
        log.debug("Entering updatePassword - {}, {}", patronId, hashedPassword);
        jdbc.update("UPDATE borrowers SET password = ?, login_attempts = 0 WHERE borrowernumber = ?", hashedPassword, patronId);
    }

    public void updatePasswordExpiration(Long patronId, LocalDate expirationDate) {
        log.debug("Entering updatePasswordExpiration - {}, {}", patronId, expirationDate);
        jdbc.update("UPDATE borrowers SET password_expiration_date = ? WHERE borrowernumber = ?", expirationDate, patronId);
    }

    public void updateStatus(Long patronId, Boolean lost, Boolean gonenoaddress, LocalDate debarred, String debarredComment) {
        log.debug("Entering updateStatus - {}, {}, {}, {}, {}", patronId, lost, gonenoaddress, debarred, debarredComment);
        jdbc.update("""
            UPDATE borrowers SET lost=?, gonenoaddress=?, debarred=?, debarredcomment=?
            WHERE borrowernumber=?
            """, lost, gonenoaddress, debarred, debarredComment, patronId);
    }

    public void mergePatrons(Long keepPatronId, Long deletePatronId) {
        log.debug("Entering mergePatrons - {}, {}", keepPatronId, deletePatronId);
        // Update foreign-key references to keep patron, then delete the other
        jdbc.update("UPDATE issues SET borrowernumber = ? WHERE borrowernumber = ?", keepPatronId, deletePatronId);
        jdbc.update("UPDATE reserves SET borrowernumber = ? WHERE borrowernumber = ?", keepPatronId, deletePatronId);
        jdbc.update("UPDATE accountlines SET borrowernumber = ? WHERE borrowernumber = ?", keepPatronId, deletePatronId);
        jdbc.update("DELETE FROM borrowers WHERE borrowernumber = ?", deletePatronId);
    }

    private Object[] appendPageParams(Object[] base, Pageable pageable) {
        log.debug("Entering appendPageParams - {}, {}", base, pageable);
        Object[] result = new Object[base.length + 2];
        System.arraycopy(base, 0, result, 0, base.length);
        result[base.length] = pageable.getPageSize();
        result[base.length + 1] = pageable.getOffset();
        return result;
    }
}

