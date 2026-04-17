package com.shailahir.koha.patron.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VirtualShelfDto {
    private Long shelfnumber;
    private String shelfname;
    private String owner;
    private String category;
    private String sortfield;
    private LocalDateTime lastmodified;
    private Boolean allow_change_from_owner;
    private Boolean allow_change_from_others;
    private Boolean allow_change_from_staff;
}

