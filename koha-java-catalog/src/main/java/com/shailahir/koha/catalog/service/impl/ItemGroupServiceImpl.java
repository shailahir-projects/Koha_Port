package com.shailahir.koha.catalog.service.impl;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemGroupServiceImpl implements ItemGroupService {

    private final CatalogRepository repo;

    @Override
    public List<ItemGroupDto> listItemGroups(Long biblioId, String query, Pageable pageable) {
        log.debug("Entering listItemGroups - {}, {}, {}", biblioId, query, pageable);
        return repo.findItemGroupsByBiblioId(biblioId);
    }

    @Override
    public ItemGroupDto addItemGroup(Long biblioId, ItemGroupDto itemGroup) {
        log.debug("Entering addItemGroup - {}, {}", biblioId, itemGroup);
        return repo.insertItemGroup(biblioId, itemGroup);
    }

    @Override
    public ItemGroupDto getItemGroup(Long biblioId, Long itemGroupId) {
        log.debug("Entering getItemGroup - {}, {}", biblioId, itemGroupId);
        return repo.findItemGroupById(biblioId, itemGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item group not found"));
    }

    @Override
    public ItemGroupDto updateItemGroup(Long biblioId, Long itemGroupId, ItemGroupDto itemGroup) {
        log.debug("Entering updateItemGroup - {}, {}, {}", biblioId, itemGroupId, itemGroup);
        repo.findItemGroupById(biblioId, itemGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item group not found"));
        return repo.updateItemGroup(biblioId, itemGroupId, itemGroup);
    }

    @Override
    public void deleteItemGroup(Long biblioId, Long itemGroupId) {
        log.debug("Entering deleteItemGroup - {}, {}", biblioId, itemGroupId);
        repo.deleteItemGroup(biblioId, itemGroupId);
    }

    @Override
    public ItemGroupDto addItemToGroup(Long biblioId, Long itemGroupId, ItemGroupItemLinkDto link) {
        log.debug("Entering addItemToGroup - {}, {}, {}", biblioId, itemGroupId, link);
        repo.addItemToGroup(itemGroupId, link.getItemId());
        return repo.findItemGroupById(biblioId, itemGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item group not found"));
    }

    @Override
    public void removeItemFromGroup(Long biblioId, Long itemGroupId, Long itemId) {
        log.debug("Entering removeItemFromGroup - {}, {}, {}", biblioId, itemGroupId, itemId);
        repo.removeItemFromGroup(itemGroupId, itemId);
    }
}

