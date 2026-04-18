package com.shailahir.koha.acquisitions.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Central application configuration for the Koha Java Acquisitions microservice.
 *
 * <p>Configures:
 * <ul>
 *   <li>Jackson ObjectMapper with Java 8 time module (LocalDate, LocalDateTime)</li>
 *   <li>XmlMapper for XML content-type support</li>
 * </ul>
 */
@Configuration
public class AcquisitionsConfig {

    /**
     * Primary JSON ObjectMapper — disables writing dates as timestamps
     * so LocalDate serialises as "2026-04-18" not [2026,4,18].
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * XmlMapper for application/xml content negotiation.
     */
    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}

