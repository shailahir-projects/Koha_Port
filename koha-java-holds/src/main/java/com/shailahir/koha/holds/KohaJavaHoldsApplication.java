package com.shailahir.koha.holds;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KohaJavaHoldsApplication {

	public static void main(String[] args) {
        log.debug("Entering main - {}", args);
		SpringApplication.run(KohaJavaHoldsApplication.class, args);
	}

}

