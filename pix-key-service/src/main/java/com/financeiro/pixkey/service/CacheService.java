package com.financeiro.pixkey.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final Long CACHE_TTL = 10L;
    private static final String PIX_KEY_PREFIX = "pix:";

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cachePixKey(String keyValue, Long accountId) {
        String key = PIX_KEY_PREFIX + keyValue;
        redisTemplate.opsForValue().set(key, accountId.toString(), CACHE_TTL, TimeUnit.MINUTES);
    }

    public Optional<Long> getPixKeyFromCache(String keyValue) {
        String key = PIX_KEY_PREFIX + keyValue;
        Object value = redisTemplate.opsForValue().get(key);

        if (value != null) {
            return Optional.of(Long.valueOf(value.toString()));
        }

        return Optional.empty();
    }

    public void invalidatePixKey(String keyValue) {
        String key = PIX_KEY_PREFIX + keyValue;
        redisTemplate.delete(key);
    }
}
