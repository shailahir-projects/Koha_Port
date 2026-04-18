import os
base_dir = r"C:\PHASE2\Koha_Port\koha-java-catalog\src\main\java\com\shailahir\koha\catalog"
ctrl_dir = os.path.join(base_dir, "controller")
dto_dir = os.path.join(base_dir, "dto")
svc_dir = os.path.join(base_dir, "service")
rep_dir = os.path.join(base_dir, "repository")
impl_dir = os.path.join(svc_dir, "impl")
os.makedirs(ctrl_dir, exist_ok=True)
os.makedirs(dto_dir, exist_ok=True)
os.makedirs(svc_dir, exist_ok=True)
os.makedirs(impl_dir, exist_ok=True)
# 1. DTOs
dtos = {
    "ImportBatchDto.java": """package com.shailahir.koha.catalog.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
@Data
@JacksonXmlRootElement
public class ImportBatchDto {
    private Long importBatchId;
    private String status;
    private String fileName;
}
""",
    "ItemTypeDto.java": """package com.shailahir.koha.catalog.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
@Data
@JacksonXmlRootElement
public class ItemTypeDto {
    private String itemType;
    private String description;
}
"""
}
for name, content in dtos.items():
    with open(os.path.join(dto_dir, name), 'w') as f:
        f.write(content)
# 2. Services
svcs = {
    "ImportBatchService.java": """package com.shailahir.koha.catalog.service;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ImportBatchService {
    Page<ImportBatchDto> listImportBatches(String query, Pageable pageable);
    ImportBatchDto getImportBatch(Long id);
}
""",
    "ItemTypeService.java": """package com.shailahir.koha.catalog.service;
import com.shailahir.koha.catalog.dto.ItemTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ItemTypeService {
    Page<ItemTypeDto> listItemTypes(String query, Pageable pageable);
    ItemTypeDto getItemType(String id);
}
"""
}
for name, content in svcs.items():
    with open(os.path.join(svc_dir, name), 'w') as f:
        f.write(content)
# 3. Impl
impls = {
    "ImportBatchServiceImpl.java": """package com.shailahir.koha.catalog.service.impl;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import com.shailahir.koha.catalog.repository.CatalogRepository;
import com.shailahir.koha.catalog.service.ImportBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ImportBatchServiceImpl implements ImportBatchService {
    private final CatalogRepository repo;
    @Override
    public Page<ImportBatchDto> listImportBatches(String query, Pageable pageable) {
        return repo.findAllImportBatches(query, pageable);
    }
    @Override
    public ImportBatchDto getImportBatch(Long id) {
        return repo.findImportBatchById(id).orElseThrow();
    }
}
""",
    "ItemTypeServiceImpl.java": """package com.shailahir.koha.catalog.service.impl;
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
"""
}
for name, content in impls.items():
    with open(os.path.join(impl_dir, name), 'w') as f:
        f.write(content)
# 4. Controllers
ctrls = {
    "ImportBatchController.java": """package com.shailahir.koha.catalog.controller;
import com.shailahir.koha.catalog.dto.ImportBatchDto;
import com.shailahir.koha.catalog.service.ImportBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/import_batches")
@RequiredArgsConstructor
public class ImportBatchController {
    private final ImportBatchService importBatchService;
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ImportBatchDto>> listImportBatches(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(importBatchService.listImportBatches(query, pageable));
    }
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ImportBatchDto> getImportBatch(@PathVariable Long id) {
        return ResponseEntity.ok(importBatchService.getImportBatch(id));
    }
}
""",
    "ItemTypeController.java": """package com.shailahir.koha.catalog.controller;
import com.shailahir.koha.catalog.dto.ItemTypeDto;
import com.shailahir.koha.catalog.service.ItemTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/item_types")
@RequiredArgsConstructor
public class ItemTypeController {
    private final ItemTypeService itemTypeService;
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ItemTypeDto>> listItemTypes(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(itemTypeService.listItemTypes(query, pageable));
    }
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ItemTypeDto> getItemType(@PathVariable String id) {
        return ResponseEntity.ok(itemTypeService.getItemType(id));
    }
}
"""
}
for name, content in ctrls.items():
    with open(os.path.join(ctrl_dir, name), 'w') as f:
        f.write(content)
print("Generated missing catalog components.")
