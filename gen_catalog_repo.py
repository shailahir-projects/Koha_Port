import os
repo_file = r"C:\PHASE2\Koha_Port\koha-java-catalog\src\main\java\com\shailahir\koha\catalog\repository\CatalogRepository.java"
with open(repo_file, 'r') as f:
    content = f.read()
# add methods to CatalogRepository
add_methods = """
    // -- Import Batches --------------------------------------------------------
    private static final RowMapper<ImportBatchDto> IMPORT_BATCH_MAPPER = (rs, rn) -> {
        ImportBatchDto dto = new ImportBatchDto();
        dto.setImportBatchId(rs.getLong("import_batch_id"));
        dto.setStatus(rs.getString("import_status"));
        dto.setFileName(rs.getString("file_name"));
        return dto;
    };
    public Page<ImportBatchDto> findAllImportBatches(String query, Pageable pageable) {
        String where = (query != null && !query.isBlank()) ? " WHERE file_name ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM import_batches" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<ImportBatchDto> list = jdbc.query(
                "SELECT * FROM import_batches" + where + " ORDER BY import_batch_id DESC LIMIT ? OFFSET ?",
                IMPORT_BATCH_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }
    public Optional<ImportBatchDto> findImportBatchById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM import_batches WHERE import_batch_id = ?", IMPORT_BATCH_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    // -- Item Types --------------------------------------------------------
    private static final RowMapper<ItemTypeDto> ITEM_TYPE_MAPPER = (rs, rn) -> {
        ItemTypeDto dto = new ItemTypeDto();
        dto.setItemType(rs.getString("itemtype"));
        dto.setDescription(rs.getString("description"));
        return dto;
    };
    public Page<ItemTypeDto> findAllItemTypes(String query, Pageable pageable) {
        String where = (query != null && !query.isBlank()) ? " WHERE description ILIKE ?" : "";
        Object[] params = (query != null && !query.isBlank()) ? new Object[]{"%" + query + "%"} : new Object[]{};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM itemtypes" + where, Integer.class, params);
        Object[] pageParams = appendPaging(params, pageable);
        List<ItemTypeDto> list = jdbc.query(
                "SELECT * FROM itemtypes" + where + " ORDER BY itemtype LIMIT ? OFFSET ?",
                ITEM_TYPE_MAPPER, pageParams);
        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }
    public Optional<ItemTypeDto> findItemTypeById(String id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM itemtypes WHERE itemtype = ?", ITEM_TYPE_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
"""
if "findAllImportBatches" not in content:
    content = content.replace("    // -- Helper ----------------------------------------------------------------", add_methods + "    // -- Helper ----------------------------------------------------------------")
    with open(repo_file, 'w') as f:
        f.write(content)
    print("Injected into CatalogRepository")
