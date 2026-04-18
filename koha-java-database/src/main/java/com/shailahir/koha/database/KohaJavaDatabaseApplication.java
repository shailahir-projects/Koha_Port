package com.shailahir.koha.database;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KohaJavaDatabaseApplication {
    public static void main(String[] args) {
        log.debug("Entering main - {}", args);
        SpringApplication.run(KohaJavaDatabaseApplication.class, args);
    }
}

