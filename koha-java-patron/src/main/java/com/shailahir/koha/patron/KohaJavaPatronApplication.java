package com.shailahir.koha.patron;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KohaJavaPatronApplication {

	public static void main(String[] args) {
        log.debug("Entering main - {}", args);
		SpringApplication.run(KohaJavaPatronApplication.class, args);
	}

}

