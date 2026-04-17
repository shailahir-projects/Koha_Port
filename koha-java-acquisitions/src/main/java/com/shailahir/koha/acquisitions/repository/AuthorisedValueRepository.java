package com.shailahir.koha.acquisitions.repository;

import com.shailahir.koha.acquisitions.dto.AuthorisedValueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JDBC repository for the authorised_values table.
 * Mirrors Koha::AuthorisedValues->search_with_library_limits().
 */
@Repository
@RequiredArgsConstructor
public class AuthorisedValueRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<AuthorisedValueDto> AV_MAPPER = (rs, rn) -> AuthorisedValueDto.builder()
            .id(rs.getLong("id"))
            .category(rs.getString("category"))
            .authorisedValue(rs.getString("authorised_value"))
            .lib(rs.getString("lib"))
            .libOpac(rs.getString("lib_opac"))
            .imageUrl(rs.getString("image_url"))
            .build();

    /**
     * Returns authorised values for a given category, optionally restricted
     * to those visible from a specific branch.
     * <p>
     * When {@code branchcode} is null or blank, all values for the category
     * are returned (same as a staff user with no branch restriction).
     * <p>
     * Mirrors: Koha::AuthorisedValues->search_with_library_limits(
     *              { category => $category },
     *              { order_by => ['category','lib','lib_opac'] },
     *              $branch_limit )
     *
     * @param category   authorised value category code (required)
     * @param branchcode optional branch code to limit results
     * @return ordered list of authorised values
     */
    public List<AuthorisedValueDto> findByCategory(String category, String branchcode) {
        if (branchcode != null && !branchcode.isBlank()) {
            // Return values that are either not restricted to any branch,
            // or are explicitly allowed for this branch
            String sql = """
                    SELECT av.id, av.category, av.authorised_value, av.lib, av.lib_opac, av.image_url
                      FROM authorised_values av
                     WHERE av.category = ?
                       AND (
                               NOT EXISTS (
                                   SELECT 1 FROM authorised_values_branches avb
                                    WHERE avb.av_id = av.id
                               )
                               OR EXISTS (
                                   SELECT 1 FROM authorised_values_branches avb
                                    WHERE avb.av_id = av.id
                                      AND avb.branchcode = ?
                               )
                           )
                     ORDER BY av.category, av.lib, av.lib_opac
                    """;
            return jdbc.query(sql, AV_MAPPER, category, branchcode);
        } else {
            String sql = """
                    SELECT id, category, authorised_value, lib, lib_opac, image_url
                      FROM authorised_values
                     WHERE category = ?
                     ORDER BY category, lib, lib_opac
                    """;
            return jdbc.query(sql, AV_MAPPER, category);
        }
    }
}

