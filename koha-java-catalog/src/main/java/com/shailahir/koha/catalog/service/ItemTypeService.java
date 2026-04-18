package com.shailahir.koha.catalog.service;
import com.shailahir.koha.catalog.dto.ItemTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ItemTypeService {
    Page<ItemTypeDto> listItemTypes(String query, Pageable pageable);
    ItemTypeDto getItemType(String id);
}
