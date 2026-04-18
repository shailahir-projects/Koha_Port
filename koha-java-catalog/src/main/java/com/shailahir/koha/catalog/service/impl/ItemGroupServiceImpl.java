package com.shailahir.koha.catalog.service.impl;

import com.shailahir.koha.catalog.dto.ItemGroupDto;
import com.shailahir.koha.catalog.dto.ItemGroupItemLinkDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.ItemGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemGroupServiceImpl implements ItemGroupService {

    private final CatalogRepository repo;

    @Override
    public List<ItemGroupDto> listItemGroups(Long biblioId, String query, Pageable pageable) {
        return repo.findItemGroupsByBiblioId(biblioId);
    }

    @Override
    public ItemGroupDto addItemGroup(Long biblioId, ItemGroupDto itemGroup) {
        return repo.insertItemGroup(biblioId, itemGroup);
    }

    @Override
    public ItemGroupDto getItemGroup(Long biblioId, Long itemGroupId) {
        return repo.findItemGroupById(biblioId, itemGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item group not found"));
    }

    @Override
    public ItemGroupDto updateItemGroup(Long biblioId, Long itemGroupId, ItemGroupDto itemGroup) {
        repo.findItemGroupById(biblioId, itemGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item group not found"));
        return repo.updateItemGroup(biblioId, itemGroupId, itemGroup);
    }

    @Override
    public void deleteItemGroup(Long biblioId, Long itemGroupId) {
        repo.deleteItemGroup(biblioId, itemGroupId);
    }

    @Override
    public ItemGroupDto addItemToGroup(Long biblioId, Long itemGroupId, ItemGroupItemLinkDto link) {
        repo.addItemToGroup(itemGroupId, link.getItemId());
        return repo.findItemGroupById(biblioId, itemGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item group not found"));
    }

    @Override
    public void removeItemFromGroup(Long biblioId, Long itemGroupId, Long itemId) {
        repo.removeItemFromGroup(itemGroupId, itemId);
    }
}

