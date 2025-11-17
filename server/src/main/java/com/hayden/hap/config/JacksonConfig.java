package com.hayden.hap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.hap.serial.VOObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean(name = "voObjectMapper")
    @Primary
    public ObjectMapper voObjectMapper() {
        return new VOObjectMapper();
    }
}
