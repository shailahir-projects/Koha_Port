package com.shailahir.koha.auth.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
@Data
@JacksonXmlRootElement
public class LoginRequest {
    private String username;
    private String password;
}
