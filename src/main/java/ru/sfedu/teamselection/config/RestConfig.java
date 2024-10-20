package ru.sfedu.teamselection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class RestConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
