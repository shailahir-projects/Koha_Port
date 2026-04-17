package com.shailahir.koha.search.service;

import com.shailahir.koha.search.dto.SearchFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFilterService {
    Page<SearchFilterDto> listFilters(Pageable pageable);
    SearchFilterDto addSearchFilter(SearchFilterDto dto);
    SearchFilterDto getSearchFilter(Long id);
    SearchFilterDto updateSearchFilter(Long id, SearchFilterDto dto);
    void deleteSearchFilter(Long id);
}

