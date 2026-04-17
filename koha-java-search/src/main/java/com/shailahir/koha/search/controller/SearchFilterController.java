package com.shailahir.koha.search.controller;

import com.shailahir.koha.search.dto.SearchFilterDto;
import com.shailahir.koha.search.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SearchFilterController {
    private final SearchFilterService searchFilterService;

    @GetMapping("/search_filters")
    public ResponseEntity<Page<SearchFilterDto>> listFilters(Pageable pageable) {
        return ResponseEntity.ok(searchFilterService.listFilters(pageable));
    }
    @PostMapping("/search_filters")
    public ResponseEntity<SearchFilterDto> addSearchFilter(@RequestBody SearchFilterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(searchFilterService.addSearchFilter(dto));
    }
    @GetMapping("/search_filters/{search_filter_id}")
    public ResponseEntity<SearchFilterDto> getSearchFilter(@PathVariable("search_filter_id") Long id) {
        return ResponseEntity.ok(searchFilterService.getSearchFilter(id));
    }
    @PutMapping("/search_filters/{search_filter_id}")
    public ResponseEntity<SearchFilterDto> updateSearchFilter(@PathVariable("search_filter_id") Long id, @RequestBody SearchFilterDto dto) {
        return ResponseEntity.ok(searchFilterService.updateSearchFilter(id, dto));
    }
    @DeleteMapping("/search_filters/{search_filter_id}")
    public ResponseEntity<Void> deleteSearchFilter(@PathVariable("search_filter_id") Long id) {
        searchFilterService.deleteSearchFilter(id);
        return ResponseEntity.noContent().build();
    }
}

