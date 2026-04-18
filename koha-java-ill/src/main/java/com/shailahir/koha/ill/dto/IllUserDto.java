package com.shailahir.koha.ill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IllUserDto {
    private Long patronId;
    private String cardnumber;
    private String firstname;
    private String surname;
    private String email;
    private String libraryId;
}

