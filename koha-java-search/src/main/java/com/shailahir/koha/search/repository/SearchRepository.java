package com.shailahir.koha.search.repository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
@RequiredArgsConstructor
public class SearchRepository {
    private final JdbcTemplate jdbc;
}
