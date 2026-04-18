package com.shailahir.koha.opac.gateway;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KohaJavaOpacGatewayApplication {

	public static void main(String[] args) {
        log.debug("Entering main - {}", args);
		SpringApplication.run(KohaJavaOpacGatewayApplication.class, args);
	}

}

