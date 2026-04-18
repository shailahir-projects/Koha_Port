package com.shailahir.koha.catalog.service.impl;
import com.shailahir.koha.catalog.dto.ItemTypeDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.ItemTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ItemTypeServiceImpl implements ItemTypeService {
    private final CatalogRepository repo;
    @Override
    public Page<ItemTypeDto> listItemTypes(String query, Pageable pageable) {
        return repo.findAllItemTypes(query, pageable);
    }
    @Override
    public ItemTypeDto getItemType(String id) {
        return repo.findItemTypeById(id).orElseThrow();
    }
}
