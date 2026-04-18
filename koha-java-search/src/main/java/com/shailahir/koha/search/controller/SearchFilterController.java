package com.shailahir.koha.search.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.search.dto.SearchFilterDto;
import com.shailahir.koha.search.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchFilterController {
    private final SearchFilterService searchFilterService;

    @GetMapping("/search_filters", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<SearchFilterDto>> listFilters(Pageable pageable) {
        log.debug("Entering listFilters - {}", pageable);
        return ResponseEntity.ok(searchFilterService.listFilters(pageable));
    }
    @PostMapping("/search_filters", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SearchFilterDto> addSearchFilter(@RequestBody SearchFilterDto dto) {
        log.debug("Entering addSearchFilter - {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(searchFilterService.addSearchFilter(dto));
    }
    @GetMapping("/search_filters/{search_filter_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SearchFilterDto> getSearchFilter(@PathVariable("search_filter_id") Long id) {
        log.debug("Entering getSearchFilter - {}", id);
        return ResponseEntity.ok(searchFilterService.getSearchFilter(id));
    }
    @PutMapping("/search_filters/{search_filter_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SearchFilterDto> updateSearchFilter(@PathVariable("search_filter_id") Long id, @RequestBody SearchFilterDto dto) {
        log.debug("Entering updateSearchFilter - {}, {}", id, dto);
        return ResponseEntity.ok(searchFilterService.updateSearchFilter(id, dto));
    }
    @DeleteMapping("/search_filters/{search_filter_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteSearchFilter(@PathVariable("search_filter_id") Long id) {
        log.debug("Entering deleteSearchFilter - {}", id);
        searchFilterService.deleteSearchFilter(id);
        return ResponseEntity.noContent().build();
    }
}

