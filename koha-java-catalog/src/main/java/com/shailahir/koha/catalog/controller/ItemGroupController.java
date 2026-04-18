package com.shailahir.koha.catalog.controller;

import com.shailahir.koha.catalog.dto.ItemGroupDto;
import com.shailahir.koha.catalog.dto.ItemGroupItemLinkDto;
import com.shailahir.koha.catalog.service.ItemGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * REST controller for item groups.
 * Maps to Swagger paths: /biblios/{biblio_id}/item_groups,
 * /biblios/{biblio_id}/item_groups/{item_group_id},
 * /biblios/{biblio_id}/item_groups/{item_group_id}/items,
 * /biblios/{biblio_id}/item_groups/{item_group_id}/items/{item_id}
 */
@RestController
@RequiredArgsConstructor
public class ItemGroupController {

    private final ItemGroupService itemGroupService;

    @GetMapping("/biblios/{biblio_id}/item_groups", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ItemGroupDto>> listItemGroups(
            @PathVariable("biblio_id") Long biblioId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed,
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemGroupService.listItemGroups(biblioId, query, pageable));
    }

    @PostMapping("/biblios/{biblio_id}/item_groups", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemGroupDto> addItemGroup(
            @PathVariable("biblio_id") Long biblioId,
            @RequestBody ItemGroupDto itemGroup) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemGroupService.addItemGroup(biblioId, itemGroup));
    }

    @GetMapping("/biblios/{biblio_id}/item_groups/{item_group_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemGroupDto> getItemGroup(
            @PathVariable("biblio_id") Long biblioId,
            @PathVariable("item_group_id") Long itemGroupId,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed) {
        return ResponseEntity.ok(itemGroupService.getItemGroup(biblioId, itemGroupId));
    }

    @PutMapping("/biblios/{biblio_id}/item_groups/{item_group_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemGroupDto> updateItemGroup(
            @PathVariable("biblio_id") Long biblioId,
            @PathVariable("item_group_id") Long itemGroupId,
            @RequestBody ItemGroupDto itemGroup,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed) {
        return ResponseEntity.ok(itemGroupService.updateItemGroup(biblioId, itemGroupId, itemGroup));
    }

    @DeleteMapping("/biblios/{biblio_id}/item_groups/{item_group_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteItemGroup(
            @PathVariable("biblio_id") Long biblioId,
            @PathVariable("item_group_id") Long itemGroupId) {
        itemGroupService.deleteItemGroup(biblioId, itemGroupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/biblios/{biblio_id}/item_groups/{item_group_id}/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemGroupDto> addItemToGroup(
            @PathVariable("biblio_id") Long biblioId,
            @PathVariable("item_group_id") Long itemGroupId,
            @RequestBody ItemGroupItemLinkDto link,
            @RequestHeader(value = "x-koha-embed", required = false) List<String> embed) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemGroupService.addItemToGroup(biblioId, itemGroupId, link));
    }

    @DeleteMapping("/biblios/{biblio_id}/item_groups/{item_group_id}/items/{item_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> removeItemFromGroup(
            @PathVariable("biblio_id") Long biblioId,
            @PathVariable("item_group_id") Long itemGroupId,
            @PathVariable("item_id") Long itemId) {
        itemGroupService.removeItemFromGroup(biblioId, itemGroupId, itemId);
        return ResponseEntity.noContent().build();
    }
}

