package com.modulos.tokenBucket.config;

import io.github.bucket4j.Bandwidth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Bucket4jConfig {

    @Bean
    public Bandwidth getLimit() {
        return Bandwidth.builder()
                .capacity(10) // máximo de tokens
                .refillGreedy(10, Duration.ofMinutes(1)) // 10 tokens cada minuto
                .build();
    }
}
