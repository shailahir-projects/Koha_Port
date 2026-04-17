package com.shailahir.koha.holds.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LibraryDto {
    private String libraryId;
    private String name;
    private Boolean pickup;
}

