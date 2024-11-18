package com.modulos.tokenBucket.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final Bandwidth limit;

    public RateLimitService(Bandwidth limit) {
        this.limit = limit;
    }

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(limit)
                .build());
    }
}
