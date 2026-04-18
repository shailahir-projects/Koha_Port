package com.shailahir.koha.search.repository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepository {
    private final JdbcTemplate jdbc;
}
