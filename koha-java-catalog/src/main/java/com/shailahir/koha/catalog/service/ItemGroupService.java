package com.shailahir.koha.catalog.service;

import com.shailahir.koha.catalog.dto.ItemGroupDto;
import com.shailahir.koha.catalog.dto.ItemGroupItemLinkDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemGroupService {
    List<ItemGroupDto> listItemGroups(Long biblioId, String query, Pageable pageable);
    ItemGroupDto addItemGroup(Long biblioId, ItemGroupDto itemGroup);
    ItemGroupDto getItemGroup(Long biblioId, Long itemGroupId);
    ItemGroupDto updateItemGroup(Long biblioId, Long itemGroupId, ItemGroupDto itemGroup);
    void deleteItemGroup(Long biblioId, Long itemGroupId);
    ItemGroupDto addItemToGroup(Long biblioId, Long itemGroupId, ItemGroupItemLinkDto link);
    void removeItemFromGroup(Long biblioId, Long itemGroupId, Long itemId);
}

