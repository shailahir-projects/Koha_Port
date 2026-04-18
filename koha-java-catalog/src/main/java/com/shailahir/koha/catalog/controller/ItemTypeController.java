package com.shailahir.koha.catalog.controller;
import lombok.extern.slf4j.Slf4j;
import com.shailahir.koha.catalog.dto.ItemTypeDto;
import com.shailahir.koha.catalog.service.ItemTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/v1/item_types")
@RequiredArgsConstructor
public class ItemTypeController {
    private final ItemTypeService itemTypeService;
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ItemTypeDto>> listItemTypes(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        log.debug("Entering listItemTypes - {}, {}", query, pageable);
        return ResponseEntity.ok(itemTypeService.listItemTypes(query, pageable));
    }
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemTypeDto> getItemType(@PathVariable String id) {
        log.debug("Entering getItemType - {}", id);
        return ResponseEntity.ok(itemTypeService.getItemType(id));
    }
}
